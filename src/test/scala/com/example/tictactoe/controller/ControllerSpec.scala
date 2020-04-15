package com.example.tictactoe.controller

import com.example.tictactoe.mocks._
import com.example.tictactoe.domain._
import com.example.tictactoe.mode.confirm.ConfirmMode
import com.example.tictactoe.mode.game.GameMode
import com.example.tictactoe.mode.menu.MenuMode
import zio._
import zio.test.Assertion._
import zio.test._
import zio.test.mock.Expectation._

object ControllerSpec extends DefaultRunnableSpec {
  def spec = suite("Controller")(
    suite("to process user input")(
      testM("State.Confirm delegates to ConfirmMode") {
        val app    = Controller.process(userInput, confirmState)
        val result = app.either.provideLayer(env)
        assertM(result)(isRight(equalTo(menuState)))
      },
      testM("State.Game delegates to GameMode") {
        val app    = Controller.process(userInput, gameState)
        val result = app.either.provideLayer(env)
        assertM(result)(isRight(equalTo(menuState)))
      },
      testM("State.Menu delegates to MenuMode") {
        val app    = Controller.process(userInput, menuState)
        val result = app.either.provideLayer(env)
        assertM(result)(isRight(equalTo(confirmState)))
      },
      testM("State.Shutdown fails with Unit") {
        val app    = Controller.process(userInput, shutdownState)
        val result = app.either.provideLayer(dummyEnv)
        assertM(result)(isLeft(equalTo(IllegalStateError)))
      }
    ),
    suite("to render")(
      testM("State.Confirm delegates to ConfirmMode") {
        val app    = Controller.render(confirmState)
        val result = app.provideLayer(env)
        assertM(result)(equalTo(renderedFrame))
      },
      testM("State.Game delegates to GameMode") {
        val app    = Controller.render(gameState)
        val result = app.provideLayer(env)
        assertM(result)(equalTo(renderedFrame))
      },
      testM("State.Menu delegates to MenuMode") {
        val app    = Controller.render(menuState)
        val result = app.provideLayer(env)
        assertM(result)(equalTo(renderedFrame))
      },
      testM("State.Shutdown returns shutdown message") {
        val app    = Controller.render(shutdownState)
        val result = app.provideLayer(dummyEnv)
        assertM(result)(equalTo(shutdownMessage))
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

  private val env: ULayer[Controller] =
    ((ConfirmModeMock.process(equalTo((userInput, confirmState))) returns value(menuState)) ||
      (GameModeMock.process(equalTo((userInput, gameState))) returns value(menuState)) ||
      (MenuModeMock.process(equalTo((userInput, menuState))) returns value(confirmState)) ||
      (ConfirmModeMock.render(equalTo(confirmState)) returns value(renderedFrame)) ||
      (GameModeMock.render(equalTo(gameState)) returns value(renderedFrame)) ||
      (MenuModeMock.render(equalTo(menuState)) returns value(renderedFrame))) >>> Controller.live

  private val dummyEnv: ULayer[Controller] =
    (ConfirmMode.dummy ++ GameMode.dummy ++ MenuMode.dummy) >>> Controller.live
}
