package com.example.tictactoe.runLoop

import com.example.tictactoe.controller.Controller
import com.example.tictactoe.domain._
import com.example.tictactoe.mocks.{ ControllerMock, TerminalMock }
import com.example.tictactoe.terminal.Terminal
import zio._
import zio.mock._
import zio.test._

object RunLoopSpec extends ZIOSpecDefault {
  def spec = suite("RunLoop")(
    suite("step")(
      test("displays current state and transforms it based on user input") {
        val controllerMock: ULayer[Controller] =
          ControllerMock.Render(Assertion.equalTo(currentState), Expectation.value(renderedFrame)) ++
            ControllerMock.Process(Assertion.equalTo(userInput -> currentState), Expectation.value(Some(nextState)))
        val terminalMock: ULayer[Terminal] =
          TerminalMock.Display(Assertion.equalTo(renderedFrame), Expectation.unit) ++ TerminalMock.GetUserInput(
            Expectation.value(userInput)
          )

        for {
          result <- RunLoop.step(currentState).provide(controllerMock, terminalMock, RunLoopLive.layer).some
        } yield assertTrue(result == nextState)
      }
    )
  )

  private val currentState: State = State.Menu(None, MenuFooterMessage.Empty)
  private val nextState: State =
    State.Game(Map.empty, Player.Human, Player.Ai, Piece.Cross, GameResult.Ongoing, GameFooterMessage.Empty)

  private val userInput     = "<user-input>"
  private val renderedFrame = "<rendered-frame>"
}
