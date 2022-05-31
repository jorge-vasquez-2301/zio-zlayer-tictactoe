package com.example.tictactoe.controller

import com.example.tictactoe.domain._
import com.example.tictactoe.mocks._
import zio._
import zio.mock._
import zio.test._

object ControllerSpec extends ZIOSpecDefault {
  def spec =
    suite("Controller")(
      suite("to process user input")(
        test("State.Confirm delegates to ConfirmMode") {
          for {
            result <- Controller.process(userInput, confirmState).some.provideLayer(env)
          } yield assertTrue(result == menuState)
        },
        test("State.Game delegates to GameMode") {
          for {
            result <- Controller.process(userInput, gameState).some.provideLayer(env)
          } yield assertTrue(result == menuState)
        },
        test("State.Menu delegates to MenuMode") {
          for {
            result <- Controller.process(userInput, menuState).some.provideLayer(env)
          } yield assertTrue(result == confirmState)
        },
        test("State.Shutdown fails with Unit") {
          for {
            result <- Controller.process(userInput, shutdownState).provideLayer(dummyEnv)
          } yield assertTrue(result.isEmpty)
        }
      ),
      suite("to render")(
        test("State.Confirm delegates to ConfirmMode") {
          for {
            result <- Controller.render(confirmState).provideLayer(env)
          } yield assertTrue(result == renderedFrame)
        },
        test("State.Game delegates to GameMode") {
          for {
            result <- Controller.render(gameState).provideLayer(env)
          } yield assertTrue(result == renderedFrame)
        },
        test("State.Menu delegates to MenuMode") {
          for {
            result <- Controller.render(menuState).provideLayer(env)
          } yield assertTrue(result == renderedFrame)
        },
        test("State.Shutdown returns shutdown message") {
          for {
            result <- Controller.render(shutdownState).provideLayer(dummyEnv)
          } yield assertTrue(result == shutdownMessage)
        }
      )
    )

  private val confirmState = State.Confirm(
    ConfirmAction.NewGame,
    State.initial,
    State.initial,
    ConfirmFooterMessage.Empty
  )
  private val gameState = State.Game(
    Map.empty,
    Player.Human,
    Player.Ai,
    Piece.Cross,
    GameResult.Ongoing,
    GameFooterMessage.Empty
  )
  private val menuState     = State.Menu(None, MenuFooterMessage.Empty)
  private val shutdownState = State.Shutdown

  private val userInput       = "<user-input>"
  private val renderedFrame   = "<rendered-frame>"
  private val shutdownMessage = "Shutting down..."

  ConfirmModeMock.Process(Assertion.equalTo((userInput, confirmState)), Expectation.value(menuState)) ||
    GameModeMock.Process(Assertion.equalTo((userInput, gameState)), Expectation.value(menuState)) ||
    MenuModeMock.Process(Assertion.equalTo((userInput, menuState)), Expectation.value(confirmState)) ||
    ConfirmModeMock.Render(Assertion.equalTo(confirmState), Expectation.value(renderedFrame)) ||
    GameModeMock.Render(Assertion.equalTo(gameState), Expectation.value(renderedFrame)) ||
    MenuModeMock.Render(Assertion.equalTo(menuState), Expectation.value(renderedFrame))

  private val env: ULayer[Controller] =
    ZLayer.make[Controller](
      ConfirmModeMock.Process(Assertion.equalTo((userInput, confirmState)), Expectation.value(menuState)) ||
        GameModeMock.Process(Assertion.equalTo((userInput, gameState)), Expectation.value(menuState)) ||
        MenuModeMock.Process(Assertion.equalTo((userInput, menuState)), Expectation.value(confirmState)) ||
        ConfirmModeMock.Render(Assertion.equalTo(confirmState), Expectation.value(renderedFrame)) ||
        GameModeMock.Render(Assertion.equalTo(gameState), Expectation.value(renderedFrame)) ||
        MenuModeMock.Render(Assertion.equalTo(menuState), Expectation.value(renderedFrame)),
      ControllerLive.layer
    )

  private val dummyEnv: ULayer[Controller] =
    ZLayer.make[Controller](
      ConfirmModeMock.empty,
      GameModeMock.empty,
      MenuModeMock.empty,
      ControllerLive.layer
    )
}
