package com.example.tictactoe.mode.menu

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain._
import com.example.tictactoe.mocks.{ MenuCommandParserMock, MenuViewMock }
import com.example.tictactoe.parser.menu.MenuCommandParser
import com.example.tictactoe.view.menu.MenuView
import zio._
import zio.mock._
import zio.test._

object MenuModeSpec extends ZIOSpecDefault {
  def spec = suite("MenuMode")(
    suite("process")(
      suite("game in progress")(
        test("new game returns confirm state") {
          checkProcess("new game", MenuCommand.NewGame, suspendedMenuState, confirmNewGameState)
        },
        test("resume returns current game state") {
          checkProcess("resume", MenuCommand.Resume, suspendedMenuState, runningGameState)
        },
        test("quit returns confirm state") {
          checkProcess("quit", MenuCommand.Quit, suspendedMenuState, confirmQuitState)
        }
      ),
      suite("no game in progress")(
        test("new game returns new game state") {
          checkProcess("new game", MenuCommand.NewGame, menuState, newGameState)
        },
        test("resume returns current state with Message.InvalidCommand") {
          checkProcess("resume", MenuCommand.Resume, menuState, invalidCommandState)
        },
        test("quit returns shutdown state") {
          checkProcess("quit", MenuCommand.Quit, menuState, State.Shutdown)
        }
      )
    ),
    suite("render")(
      test("game in progress returns suspended menu frame") {
        checkRender(suspendedMenuState, Assertion.isTrue)
      },
      test("no game in progress returns default menu frame") {
        checkRender(menuState, Assertion.isFalse)
      }
    )
  )

  private val newGameState = State.Game(
    Map.empty,
    Player.Human,
    Player.Ai,
    Piece.Cross,
    GameResult.Ongoing,
    GameFooterMessage.Empty
  )
  private val runningGameState = State.Game(
    Map(
      Field.North -> Piece.Cross,
      Field.South -> Piece.Nought
    ),
    Player.Human,
    Player.Ai,
    Piece.Cross,
    GameResult.Ongoing,
    GameFooterMessage.Empty
  )

  private val menuState          = State.Menu(None, MenuFooterMessage.Empty)
  private val suspendedMenuState = State.Menu(Some(runningGameState), MenuFooterMessage.Empty)
  private val confirmNewGameState = State.Confirm(
    ConfirmAction.NewGame,
    newGameState,
    suspendedMenuState,
    ConfirmFooterMessage.Empty
  )
  private val confirmQuitState = State.Confirm(
    ConfirmAction.Quit,
    State.Shutdown,
    suspendedMenuState,
    ConfirmFooterMessage.Empty
  )
  private val invalidCommandState = State.Menu(None, MenuFooterMessage.InvalidCommand)
  private val renderedFrame       = List("header", "content", "footer").mkString("\n\n")

  private def checkProcess(
    input: String,
    command: MenuCommand,
    state: State.Menu,
    updatedState: State
  ): UIO[TestResult] = {
    val menuCommandParserMock: ULayer[MenuCommandParser] =
      MenuCommandParserMock.Parse(Assertion.equalTo(input), Expectation.value(command))

    for {
      result <- MenuMode
                 .process(input, state)
                 .provide(
                   menuCommandParserMock,
                   MenuViewMock.empty,
                   MenuModeLive.layer
                 )
    } yield assertTrue(result == updatedState)
  }

  private def checkRender(state: State.Menu, menuSuspendedAssertion: Assertion[Boolean]): UIO[TestResult] = {
    val menuViewMock: ULayer[MenuView] = MenuViewMock.Header(Expectation.value("header")) ++
      MenuViewMock.Content(menuSuspendedAssertion, Expectation.value("content")) ++
      MenuViewMock.Footer(Assertion.equalTo(MenuFooterMessage.Empty), Expectation.value("footer"))

    for {
      result <- MenuMode
                 .render(state)
                 .provide(
                   MenuCommandParserMock.empty,
                   menuViewMock,
                   MenuModeLive.layer
                 )
    } yield assertTrue(result == renderedFrame)
  }
}
