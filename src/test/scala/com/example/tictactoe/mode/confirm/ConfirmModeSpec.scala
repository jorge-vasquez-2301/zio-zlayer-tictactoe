package com.example.tictactoe.mode.confirm

import com.example.tictactoe.domain._
import com.example.tictactoe.mocks._
import com.example.tictactoe.parser.confirm.ConfirmCommandParser
import com.example.tictactoe.view.confirm.ConfirmView
import zio._
import zio.test.Assertion._
import zio.test._
import zio.test.mock.Expectation._

object ConfirmModeSpec extends DefaultRunnableSpec {
  def spec = suite("ConfirmMode")(
    suite("process")(
      testM("yes returns confirmed state") {
        checkProcess("yes", Some(ConfirmCommand.Yes), currentState, confirmedState)
      },
      testM("no returns declined state") {
        checkProcess("no", Some(ConfirmCommand.No), currentState, declinedState)
      },
      testM("any other returns current state with footer message: InvalidCommand") {
        checkProcess("foo", None, currentState, invalidCommandState)
      }
    ),
    suite("render")(
      testM("returns confirm frame") {
        val confirmViewMock: ULayer[ConfirmView] =
          ConfirmViewMock.Header(equalTo(ConfirmAction.NewGame), value("header")) ++
            ConfirmViewMock.Content(value("content")) ++
            ConfirmViewMock.Footer(equalTo(ConfirmFooterMessage.Empty), value("footer"))

        val env: ULayer[ConfirmMode] =
          (ConfirmCommandParser.dummy ++ confirmViewMock) >>> ConfirmMode.live
        val result = ConfirmMode.render(currentState).provideLayer(env)
        assertM(result)(equalTo(renderedFrame))
      }
    )
  )

  private val confirmedState = State.Game(
    Map.empty,
    Player.Human,
    Player.Ai,
    Piece.Cross,
    GameResult.Ongoing,
    GameFooterMessage.Empty
  )
  private val declinedState = State.Menu(None, MenuFooterMessage.Empty)
  private val invalidCommandState = State.Confirm(
    ConfirmAction.NewGame,
    confirmedState,
    declinedState,
    ConfirmFooterMessage.InvalidCommand
  )
  private val currentState = State.Confirm(
    ConfirmAction.NewGame,
    confirmedState,
    declinedState,
    ConfirmFooterMessage.Empty
  )
  private val renderedFrame = List("header", "content", "footer").mkString("\n\n")

  private def checkProcess(
    input: String,
    optionCommand: Option[ConfirmCommand],
    state: State.Confirm,
    updatedState: State
  ): UIO[TestResult] = {
    val confirmCommandParserMock: ULayer[ConfirmCommandParser] = optionCommand match {
      case Some(command) => ConfirmCommandParserMock.Parse(equalTo(input), value(command))
      case None          => ConfirmCommandParserMock.Parse(equalTo(input), failure(ParseError))
    }
    val env: ULayer[ConfirmMode] =
      (confirmCommandParserMock ++ ConfirmView.dummy) >>> ConfirmMode.live
    val result = ConfirmMode.process(input, state).provideLayer(env)
    assertM(result)(equalTo(updatedState))
  }
}
