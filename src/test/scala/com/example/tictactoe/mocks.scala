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
  object ControllerMock {
    // Capability tags
    sealed trait Tag[I, A] extends Method[Controller, I, A] {
      def envBuilder: URLayer[Has[Proxy], Controller] = ControllerMock.envBuilder
    }
    object process extends Tag[(String, State), State]
    object render  extends Tag[State, String]

    // Mock layer
    private val envBuilder: URLayer[Has[Proxy], Controller] =
      ZLayer.fromService(invoke =>
        new Controller.Service {
          override def process(input: String, state: State): UIO[State] = invoke(ControllerMock.process, input, state)
          override def render(state: State): UIO[String]                = invoke(ControllerMock.render, state)
        }
      )
  }

  object ConfirmModeMock {
    // Capability tags
    sealed trait Tag[I, A] extends Method[ConfirmMode, I, A] {
      def envBuilder: URLayer[Has[Proxy], ConfirmMode] = ConfirmModeMock.envBuilder
    }
    object process extends Tag[(String, State.Confirm), State]
    object render  extends Tag[State.Confirm, String]

    // Mock layer
    private val envBuilder: URLayer[Has[Proxy], ConfirmMode] =
      ZLayer.fromService(invoke =>
        new ConfirmMode.Service {
          override def process(input: String, state: State.Confirm): UIO[State] =
            invoke(ConfirmModeMock.process, input, state)

          override def render(state: State.Confirm): UIO[String] = invoke(ConfirmModeMock.render, state)
        }
      )
  }

  object GameModeMock {
    // Capability tags
    sealed trait Tag[I, A] extends Method[GameMode, I, A] {
      def envBuilder: URLayer[Has[Proxy], GameMode] = GameModeMock.envBuilder
    }
    object process extends Tag[(String, State.Game), State]
    object render  extends Tag[State.Game, String]

    // Mock layer
    private val envBuilder: URLayer[Has[Proxy], GameMode] =
      ZLayer.fromService(invoke =>
        new GameMode.Service {
          override def process(input: String, state: State.Game): UIO[State] =
            invoke(GameModeMock.process, input, state)

          override def render(state: State.Game): UIO[String] = invoke(GameModeMock.render, state)
        }
      )
  }

  object MenuModeMock {
    // Capability tags
    sealed trait Tag[I, A] extends Method[MenuMode, I, A] {
      def envBuilder: URLayer[Has[Proxy], MenuMode] = MenuModeMock.envBuilder
    }
    object process extends Tag[(String, State.Menu), State]
    object render  extends Tag[State.Menu, String]

    // Mock layer
    private val envBuilder: URLayer[Has[Proxy], MenuMode] =
      ZLayer.fromService(invoke =>
        new MenuMode.Service {
          override def process(input: String, state: State.Menu): UIO[State] =
            invoke(MenuModeMock.process, input, state)

          override def render(state: State.Menu): UIO[String] = invoke(MenuModeMock.render, state)
        }
      )
  }

  object ConfirmCommandParserMock {
    // Capability tags
    sealed trait Tag[I, A] extends Method[ConfirmCommandParser, I, A] {
      def envBuilder: URLayer[Has[Proxy], ConfirmCommandParser] = ConfirmCommandParserMock.envBuilder
    }
    object parse extends Tag[String, ConfirmCommand]

    // Mock layer
    private val envBuilder: URLayer[Has[Proxy], ConfirmCommandParser] =
      ZLayer.fromService(invoke =>
        new ConfirmCommandParser.Service {
          override def parse(input: String): IO[Unit, ConfirmCommand] = invoke(ConfirmCommandParserMock.parse, input)
        }
      )
  }

  object GameCommandParserMock {
    // Capability tags
    sealed trait Tag[I, A] extends Method[GameCommandParser, I, A] {
      def envBuilder: URLayer[Has[Proxy], GameCommandParser] = GameCommandParserMock.envBuilder
    }
    object parse extends Tag[String, GameCommand]

    // Mock layer
    private val envBuilder: URLayer[Has[Proxy], GameCommandParser] =
      ZLayer.fromService(invoke =>
        new GameCommandParser.Service {
          override def parse(input: String): IO[Unit, GameCommand] = invoke(GameCommandParserMock.parse, input)
        }
      )
  }

  object MenuCommandParserMock {
    // Capability tags
    sealed trait Tag[I, A] extends Method[MenuCommandParser, I, A] {
      def envBuilder: URLayer[Has[Proxy], MenuCommandParser] = MenuCommandParserMock.envBuilder
    }
    object parse extends Tag[String, MenuCommand]

    // Mock layer
    private val envBuilder: URLayer[Has[Proxy], MenuCommandParser] =
      ZLayer.fromService(invoke =>
        new MenuCommandParser.Service {
          override def parse(input: String): IO[Unit, MenuCommand] = invoke(MenuCommandParserMock.parse, input)
        }
      )
  }

  object ConfirmViewMock {
    // Capability tags
    sealed trait Tag[I, A] extends Method[ConfirmView, I, A] {
      def envBuilder: URLayer[Has[Proxy], ConfirmView] = ConfirmViewMock.envBuilder
    }
    object header  extends Tag[ConfirmAction, String]
    object content extends Tag[Unit, String]
    object footer  extends Tag[ConfirmFooterMessage, String]

    // Mock layer
    private val envBuilder: URLayer[Has[Proxy], ConfirmView] =
      ZLayer.fromService(invoke =>
        new ConfirmView.Service {
          override def header(action: ConfirmAction): UIO[String]         = invoke(ConfirmViewMock.header, action)
          override val content: UIO[String]                               = invoke(ConfirmViewMock.content)
          override def footer(message: ConfirmFooterMessage): UIO[String] = invoke(ConfirmViewMock.footer, message)
        }
      )
  }

  object GameViewMock {
    // Capability tags
    sealed trait Tag[I, A] extends Method[GameView, I, A] {
      def envBuilder: URLayer[Has[Proxy], GameView] = GameViewMock.envBuilder
    }
    object header  extends Tag[(GameResult, Piece, Player), String]
    object content extends Tag[(Map[Field, Piece], GameResult), String]
    object footer  extends Tag[GameFooterMessage, String]

    // Mock layer
    private val envBuilder: URLayer[Has[Proxy], GameView] =
      ZLayer.fromService(invoke =>
        new GameView.Service {
          override def header(result: GameResult, turn: Piece, player: Player): UIO[String] =
            invoke(GameViewMock.header, result, turn, player)

          override def content(board: Map[Field, Piece], result: GameResult): UIO[String] =
            invoke(GameViewMock.content, board, result)

          override def footer(message: GameFooterMessage): UIO[String] = invoke(GameViewMock.footer, message)
        }
      )
  }

  object MenuViewMock {
    // Capability tags
    sealed trait Tag[I, A] extends Method[MenuView, I, A] {
      def envBuilder: URLayer[Has[Proxy], MenuView] = MenuViewMock.envBuilder
    }
    object header  extends Tag[Unit, String]
    object content extends Tag[Boolean, String]
    object footer  extends Tag[MenuFooterMessage, String]

    // Mock layer
    private val envBuilder: URLayer[Has[Proxy], MenuView] =
      ZLayer.fromService(invoke =>
        new MenuView.Service {
          override val header: UIO[String]                             = invoke(MenuViewMock.header)
          override def content(isSuspended: Boolean): UIO[String]      = invoke(MenuViewMock.content, isSuspended)
          override def footer(message: MenuFooterMessage): UIO[String] = invoke(MenuViewMock.footer, message)
        }
      )
  }

  object GameLogicMock {
    // Capability tags
    sealed trait Tag[I, A] extends Method[GameLogic, I, A] {
      def envBuilder: URLayer[Has[Proxy], GameLogic] = GameLogicMock.envBuilder
    }

    object putPiece   extends Tag[(Map[Field, Piece], Field, Piece), Map[Field, Piece]]
    object gameResult extends Tag[Map[Field, Piece], GameResult]
    object nextTurn   extends Tag[Piece, Piece]

    // Mock layer
    private val envBuilder: URLayer[Has[Proxy], GameLogic] =
      ZLayer.fromService(invoke =>
        new GameLogic.Service {
          override def putPiece(board: Map[Field, Piece], field: Field, piece: Piece): IO[Unit, Map[Field, Piece]] =
            invoke(GameLogicMock.putPiece, board, field, piece)

          override def gameResult(board: Map[Field, Piece]): UIO[GameResult] = invoke(GameLogicMock.gameResult, board)
          override def nextTurn(currentTurn: Piece): UIO[Piece]              = invoke(GameLogicMock.nextTurn, currentTurn)
        }
      )
  }

  object OpponentAiMock {
    // Capability tags
    sealed trait Tag[I, A] extends Method[OpponentAi, I, A] {
      def envBuilder: URLayer[Has[Proxy], OpponentAi] = OpponentAiMock.envBuilder
    }

    object randomMove extends Tag[Map[Field, Piece], Field]

    // Mock layer
    private val envBuilder: URLayer[Has[Proxy], OpponentAi] =
      ZLayer.fromService(invoke =>
        new OpponentAi.Service {
          override def randomMove(board: Map[Field, Piece]): IO[Unit, Field] = invoke(OpponentAiMock.randomMove, board)
        }
      )
  }

  object TerminalMock {
    // Capability tags
    sealed trait Tag[I, A] extends Method[Terminal, I, A] {
      def envBuilder: URLayer[Has[Proxy], Terminal] = TerminalMock.envBuilder
    }
    object getUserInput extends Tag[Unit, String]
    object display      extends Tag[String, Unit]

    // Mock layer
    private val envBuilder: URLayer[Has[Proxy], Terminal] =
      ZLayer.fromService(invoke =>
        new Terminal.Service {
          override val getUserInput: UIO[String]         = invoke(TerminalMock.getUserInput)
          override def display(frame: String): UIO[Unit] = invoke(TerminalMock.display, frame)
        }
      )
  }
}
