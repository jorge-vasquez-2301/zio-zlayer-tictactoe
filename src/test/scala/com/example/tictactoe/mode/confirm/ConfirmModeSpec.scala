package com.example.tictactoe.mode.confirm

import com.example.tictactoe.domain._
import com.example.tictactoe.mocks._
import com.example.tictactoe.parser.confirm.ConfirmCommandParser
import com.example.tictactoe.view.confirm.ConfirmView
import zio._
import zio.mock._
import zio.test._

object ConfirmModeSpec extends ZIOSpecDefault {
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
        val confirmViewMock: ULayer[ConfirmView] =
          ConfirmViewMock.Header(Assertion.equalTo(ConfirmAction.NewGame), Expectation.value("header")) ++
            ConfirmViewMock.Content(Expectation.value("content")) ++
            ConfirmViewMock.Footer(Assertion.equalTo(ConfirmFooterMessage.Empty), Expectation.value("footer"))

        for {
          result <- ConfirmMode
                     .render(currentState)
                     .provide(ConfirmCommandParserMock.empty, confirmViewMock, ConfirmModeLive.layer)
        } yield assertTrue(result == renderedFrame)
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
      case Some(command) => ConfirmCommandParserMock.Parse(Assertion.equalTo(input), Expectation.value(command))
      case None          => ConfirmCommandParserMock.Parse(Assertion.equalTo(input), Expectation.failure(ParseError))
    }
    for {
      result <- ConfirmMode
                 .process(input, state)
                 .provide(confirmCommandParserMock, ConfirmViewMock.empty, ConfirmModeLive.layer)
    } yield assertTrue(result == updatedState)
  }
}
