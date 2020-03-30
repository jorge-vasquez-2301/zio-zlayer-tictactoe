package com.example.tictactoe.mode

import com.example.tictactoe.domain._
import Board.Field
import com.example.tictactoe.gameLogic.GameLogic
import com.example.tictactoe.opponentAi.OpponentAi
import com.example.tictactoe.parser.game.GameCommandParser
import com.example.tictactoe.view.game.GameView
import zio._

package object game {
  type GameMode = Has[GameMode.Service]
  object GameMode {
    trait Service {
      def process(input: String, state: State.Game): UIO[State]
      def render(state: State.Game): UIO[String]
    }
    object Service {
      val live: ZLayer[GameCommandParser with GameView with OpponentAi with GameLogic, Nothing, GameMode] =
        ZLayer.fromFunction { env =>
          val opponentAiService        = env.get[OpponentAi.Service]
          val gameCommandParserService = env.get[GameCommandParser.Service]
          val gameLogicService         = env.get[GameLogic.Service]
          val gameViewService          = env.get[GameView.Service]

          new Service {
            override def process(input: String, state: State.Game): UIO[State] =
              if (state.result != GameResult.Ongoing) UIO.succeed(State.Menu(None, MenuFooterMessage.Empty))
              else if (isAiTurn(state))
                opponentAiService
                  .randomMove(state.board)
                  .flatMap(takeField(_, state))
                  .orDieWith(_ => new IllegalStateException)
              else
                gameCommandParserService
                  .parse(input)
                  .flatMap {
                    case GameCommand.Menu       => UIO.succeed(State.Menu(Some(state), MenuFooterMessage.Empty))
                    case GameCommand.Put(field) => takeField(field, state)
                  }
                  .orElse(ZIO.succeed(state.copy(footerMessage = GameFooterMessage.InvalidCommand)))

            private def isAiTurn(state: State.Game): Boolean =
              (state.turn == Piece.Cross && state.cross == Player.Ai) ||
                (state.turn == Piece.Nought && state.nought == Player.Ai)

            private def takeField(field: Field, state: State.Game): UIO[State] =
              (for {
                updatedBoard  <- gameLogicService.putPiece(state.board, field, state.turn)
                updatedResult <- gameLogicService.gameResult(updatedBoard)
                updatedTurn   <- gameLogicService.nextTurn(state.turn)
              } yield state.copy(
                board = updatedBoard,
                result = updatedResult,
                turn = updatedTurn,
                footerMessage = GameFooterMessage.Empty
              )).orElse(UIO.succeed(state.copy(footerMessage = GameFooterMessage.FieldOccupied)))

            override def render(state: State.Game): UIO[String] = {
              val player = if (state.turn == Piece.Cross) state.cross else state.nought
              for {
                header  <- gameViewService.header(state.result, state.turn, player)
                content <- gameViewService.content(state.board, state.result)
                footer  <- gameViewService.footer(state.footerMessage)
              } yield List(header, content, footer).mkString("\n\n")
            }
          }
        }
    }

    // accessors
    def process(input: String, state: State.Game): URIO[GameMode, State] =
      ZIO.accessM(_.get.process(input, state))

    def render(state: State.Game): URIO[GameMode, String] = ZIO.accessM(_.get.render(state))
  }
}
