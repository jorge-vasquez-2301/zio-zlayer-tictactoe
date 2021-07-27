package com.example.tictactoe.runLoop

import com.example.tictactoe.controller.Controller
import com.example.tictactoe.domain._
import com.example.tictactoe.mocks.{ ControllerMock, TerminalMock }
import com.example.tictactoe.terminal.Terminal
import zio._
import zio.test.Assertion._
import zio.test._
import zio.test.mock.Expectation._

object RunLoopSpec extends DefaultRunnableSpec {
  def spec = suite("RunLoop")(
    suite("step")(
      test("displays current state and transforms it based on user input") {
        val controllerMock: ULayer[Has[Controller]] =
          ControllerMock.Render(equalTo(currentState), value(renderedFrame)) ++
            ControllerMock.Process(equalTo(userInput -> currentState), value(nextState))
        val terminalMock: ULayer[Has[Terminal]] =
          TerminalMock.Display(equalTo(renderedFrame), unit) ++ TerminalMock.GetUserInput(value(userInput))

        val result = RunLoop.step(currentState).either.inject(controllerMock, terminalMock, RunLoopLive.layer)
        assertM(result)(isRight(equalTo(nextState)))
      }
    )
  )

  private val currentState: State = State.Menu(None, MenuFooterMessage.Empty)
  private val nextState: State =
    State.Game(Map.empty, Player.Human, Player.Ai, Piece.Cross, GameResult.Ongoing, GameFooterMessage.Empty)

  private val userInput     = "<user-input>"
  private val renderedFrame = "<rendered-frame>"
}
