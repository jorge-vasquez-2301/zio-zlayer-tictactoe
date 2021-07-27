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
      test("yes returns confirmed state") {
        checkProcess("yes", Some(ConfirmCommand.Yes), currentState, confirmedState)
      },
      test("no returns declined state") {
        checkProcess("no", Some(ConfirmCommand.No), currentState, declinedState)
      },
      test("any other returns current state with footer message: InvalidCommand") {
        checkProcess("foo", None, currentState, invalidCommandState)
      }
    ),
    suite("render")(
      test("returns confirm frame") {
        val confirmViewMock: ULayer[Has[ConfirmView]] =
          ConfirmViewMock.Header(equalTo(ConfirmAction.NewGame), value("header")) ++
            ConfirmViewMock.Content(value("content")) ++
            ConfirmViewMock.Footer(equalTo(ConfirmFooterMessage.Empty), value("footer"))

        val result = ConfirmMode
          .render(currentState)
          .inject(ConfirmCommandParserMock.empty, confirmViewMock, ConfirmModeLive.layer)
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
    val confirmCommandParserMock: ULayer[Has[ConfirmCommandParser]] = optionCommand match {
      case Some(command) => ConfirmCommandParserMock.Parse(equalTo(input), value(command))
      case None          => ConfirmCommandParserMock.Parse(equalTo(input), failure(ParseError))
    }
    val result =
      ConfirmMode.process(input, state).inject(confirmCommandParserMock, ConfirmViewMock.empty, ConfirmModeLive.layer)
    assertM(result)(equalTo(updatedState))
  }
}
