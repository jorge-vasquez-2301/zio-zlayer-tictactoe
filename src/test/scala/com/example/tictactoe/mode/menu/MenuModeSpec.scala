package com.example.tictactoe.mode.menu

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain._
import com.example.tictactoe.mocks.{ MenuCommandParserMock, MenuViewMock }
import com.example.tictactoe.parser.menu.MenuCommandParser
import com.example.tictactoe.view.menu.MenuView
import zio._
import zio.magic._
import zio.test.Assertion._
import zio.test._
import zio.test.mock.Expectation._

object MenuModeSpec extends DefaultRunnableSpec {
  def spec = suite("MenuMode")(
    suite("process")(
      suite("game in progress")(
        testM("new game returns confirm state") {
          checkProcess("new game", MenuCommand.NewGame, suspendedMenuState, confirmNewGameState)
        },
        testM("resume returns current game state") {
          checkProcess("resume", MenuCommand.Resume, suspendedMenuState, runningGameState)
        },
        testM("quit returns confirm state") {
          checkProcess("quit", MenuCommand.Quit, suspendedMenuState, confirmQuitState)
        }
      ),
      suite("no game in progress")(
        testM("new game returns new game state") {
          checkProcess("new game", MenuCommand.NewGame, menuState, newGameState)
        },
        testM("resume returns current state with Message.InvalidCommand") {
          checkProcess("resume", MenuCommand.Resume, menuState, invalidCommandState)
        },
        testM("quit returns shutdown state") {
          checkProcess("quit", MenuCommand.Quit, menuState, State.Shutdown)
        }
      )
    ),
    suite("render")(
      testM("game in progress returns suspended menu frame") {
        checkRender(
          suspendedMenuState,
          MenuViewMock.Header(value("header")) ++
            MenuViewMock.Content(isTrue, value("content")) ++
            MenuViewMock.Footer(equalTo(MenuFooterMessage.Empty), value("footer"))
        )
      },
      testM("no game in progress returns default menu frame") {
        checkRender(
          menuState,
          MenuViewMock.Header(value("header")) ++
            MenuViewMock.Content(isFalse, value("content")) ++
            MenuViewMock.Footer(equalTo(MenuFooterMessage.Empty), value("footer"))
        )
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
    val menuCommandParserMock: ULayer[Has[MenuCommandParser]] =
      MenuCommandParserMock.Parse(equalTo(input), value(command))
    val result = MenuMode
      .process(input, state)
      .inject(
        menuCommandParserMock,
        MenuViewMock.empty,
        MenuModeLive.layer
      )
    assertM(result)(equalTo(updatedState))
  }

  private def checkRender(state: State.Menu, menuViewMock: ULayer[Has[MenuView]]): UIO[TestResult] = {
    val result = MenuMode
      .render(state)
      .inject(
        MenuCommandParserMock.empty,
        menuViewMock,
        MenuModeLive.layer
      )
    assertM(result)(equalTo(renderedFrame))
  }
}
