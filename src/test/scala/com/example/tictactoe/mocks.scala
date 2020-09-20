package com.example.tictactoe

import com.example.tictactoe.controller.Controller
import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain._
import com.example.tictactoe.gameLogic.GameLogic
import com.example.tictactoe.mode.confirm.ConfirmMode
import com.example.tictactoe.mode.game.GameMode
import com.example.tictactoe.mode.menu.MenuMode
import com.example.tictactoe.opponentAi.OpponentAi
import com.example.tictactoe.parser.confirm.ConfirmCommandParser
import com.example.tictactoe.parser.game.GameCommandParser
import com.example.tictactoe.parser.menu.MenuCommandParser
import com.example.tictactoe.terminal.Terminal
import com.example.tictactoe.view.confirm.ConfirmView
import com.example.tictactoe.view.game.GameView
import com.example.tictactoe.view.menu.MenuView
import zio.test.mock._
import zio._

object mocks {
  object ControllerMock extends Mock[Controller] {

    val compose: URLayer[Has[Proxy], Controller] =
      ZLayer.fromService(proxy =>
        new Controller.Service {
          override def process(input: String, state: State): UIO[State] = proxy(ControllerMock.process, input, state)
          override def render(state: State): UIO[String]                = proxy(ControllerMock.render, state)
        }
      )

    object process extends Effect[(String, State), Nothing, State]

    object render extends Effect[State, Nothing, String]
  }

  object ConfirmModeMock extends Mock[ConfirmMode] {

    val compose: URLayer[Has[Proxy], ConfirmMode] =
      ZLayer.fromService(invoke =>
        new ConfirmMode.Service {
          override def process(input: String, state: State.Confirm): UIO[State] =
            invoke(ConfirmModeMock.process, input, state)

          override def render(state: State.Confirm): UIO[String] = invoke(ConfirmModeMock.render, state)
        }
      )

    object process extends Effect[(String, State.Confirm), Nothing, State]

    object render extends Effect[State.Confirm, Nothing, String]
  }

  object GameModeMock extends Mock[GameMode] {
    val compose: URLayer[Has[Proxy], GameMode] =
      ZLayer.fromService { proxy =>
        new GameMode.Service {
          override def process(input: String, state: State.Game): UIO[State] =
            proxy(GameModeMock.process, input, state)

          override def render(state: State.Game): UIO[String] = proxy(GameModeMock.render, state)
        }
      }

    object process extends Effect[(String, State.Game), Nothing, State]

    object render extends Effect[State.Game, Nothing, String]
  }

  object MenuModeMock extends Mock[MenuMode] {
    val compose: URLayer[Has[Proxy], MenuMode] =
      ZLayer.fromService(invoke =>
        new MenuMode.Service {
          override def process(input: String, state: State.Menu): UIO[State] =
            invoke(MenuModeMock.process, input, state)

          override def render(state: State.Menu): UIO[String] = invoke(MenuModeMock.render, state)
        }
      )

    object process extends Effect[(String, State.Menu), Nothing, State]

    object render extends Effect[State.Menu, Nothing, String]
  }

  object ConfirmCommandParserMock extends Mock[ConfirmCommandParser] {

    val compose: URLayer[Has[Proxy], ConfirmCommandParser] =
      ZLayer.fromService(invoke => (input: String) => invoke(ConfirmCommandParserMock.parse, input))

    object parse extends Effect[String, AppError, ConfirmCommand]
  }

  object GameCommandParserMock extends Mock[GameCommandParser] {
    val compose: URLayer[Has[Proxy], GameCommandParser] =
      ZLayer.fromService(invoke => (input: String) => invoke(GameCommandParserMock.parse, input))

    object parse extends Effect[String, AppError, GameCommand]
  }

  object MenuCommandParserMock extends Mock[MenuCommandParser] {
    // Mock layer
    val compose: URLayer[Has[Proxy], MenuCommandParser] =
      ZLayer.fromService(invoke => (input: String) => invoke(MenuCommandParserMock.parse, input))

    object parse extends Effect[String, Nothing, MenuCommand]
  }

  object ConfirmViewMock extends Mock[ConfirmView] {
    val compose: URLayer[Has[Proxy], ConfirmView] =
      ZLayer.fromService(invoke =>
        new ConfirmView.Service {
          override def header(action: ConfirmAction): UIO[String]         = invoke(ConfirmViewMock.header, action)
          override val content: UIO[String]                               = invoke(ConfirmViewMock.content)
          override def footer(message: ConfirmFooterMessage): UIO[String] = invoke(ConfirmViewMock.footer, message)
        }
      )

    object header extends Effect[ConfirmAction, Nothing, String]

    object content extends Effect[Unit, Nothing, String]

    object footer extends Effect[ConfirmFooterMessage, Nothing, String]
  }

  object GameViewMock extends Mock[GameView] {
    val compose: URLayer[Has[Proxy], GameView] =
      ZLayer.fromService(invoke =>
        new GameView.Service {
          override def header(result: GameResult, turn: Piece, player: Player): UIO[String] =
            invoke(GameViewMock.header, result, turn, player)

          override def content(board: Map[Field, Piece], result: GameResult): UIO[String] =
            invoke(GameViewMock.content, board, result)

          override def footer(message: GameFooterMessage): UIO[String] = invoke(GameViewMock.footer, message)
        }
      )

    object header extends Effect[(GameResult, Piece, Player), Nothing, String]

    object content extends Effect[(Map[Field, Piece], GameResult), Nothing, String]

    object footer extends Effect[GameFooterMessage, Nothing, String]
  }

  object MenuViewMock extends Mock[MenuView] {
    val compose: URLayer[Has[Proxy], MenuView] =
      ZLayer.fromService(invoke =>
        new MenuView.Service {
          override val header: UIO[String]                             = invoke(MenuViewMock.header)
          override def content(isSuspended: Boolean): UIO[String]      = invoke(MenuViewMock.content, isSuspended)
          override def footer(message: MenuFooterMessage): UIO[String] = invoke(MenuViewMock.footer, message)
        }
      )

    object header extends Effect[Unit, Nothing, String]

    object content extends Effect[Boolean, Nothing, String]

    object footer extends Effect[MenuFooterMessage, Nothing, String]
  }

  object GameLogicMock extends Mock[GameLogic] {
    val compose: URLayer[Has[Proxy], GameLogic] =
      ZLayer.fromService(invoke =>
        new GameLogic.Service {
          override def putPiece(board: Map[Field, Piece], field: Field, piece: Piece): IO[AppError, Map[Field, Piece]] =
            invoke(GameLogicMock.putPiece, board, field, piece)

          override def gameResult(board: Map[Field, Piece]): UIO[GameResult] = invoke(GameLogicMock.gameResult, board)
          override def nextTurn(currentTurn: Piece): UIO[Piece]              = invoke(GameLogicMock.nextTurn, currentTurn)
        }
      )

    object putPiece extends Effect[(Map[Field, Piece], Field, Piece), AppError, Map[Field, Piece]]

    object gameResult extends Effect[Map[Field, Piece], Nothing, GameResult]

    object nextTurn extends Effect[Piece, Nothing, Piece]
  }

  object OpponentAiMock extends Mock[OpponentAi] {
    val compose: URLayer[Has[Proxy], OpponentAi] =
      ZLayer.fromService(invoke => (board: Map[Field, Piece]) => invoke(OpponentAiMock.randomMove, board))

    object randomMove extends Effect[Map[Field, Piece], Nothing, Field]
  }

  object TerminalMock extends Mock[Terminal] {
    val compose: URLayer[Has[Proxy], Terminal] =
      ZLayer.fromService(invoke =>
        new Terminal.Service {
          override val getUserInput: UIO[String]         = invoke(TerminalMock.getUserInput)
          override def display(frame: String): UIO[Unit] = invoke(TerminalMock.display, frame)
        }
      )

    object getUserInput extends Effect[Unit, Nothing, String]

    object display extends Effect[String, Nothing, Unit]
  }
}
