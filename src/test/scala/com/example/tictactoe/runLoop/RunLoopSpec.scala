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
      testM("displays current state and transforms it based on user input") {
        val controllerMock: ULayer[Controller] =
          ControllerMock.render(equalTo(currentState), value(renderedFrame)) ++
            ControllerMock.process(equalTo(userInput -> currentState), value(nextState))
        val terminalMock: ULayer[Terminal] =
          TerminalMock.display(equalTo(renderedFrame), unit) ++
            TerminalMock.getUserInput(value(userInput))

        val env: ULayer[RunLoop] = (controllerMock ++ terminalMock) >>> RunLoop.live
        val result               = RunLoop.step(currentState).either.provideLayer(env)
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
