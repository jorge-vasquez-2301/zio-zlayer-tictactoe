package com.example.tictactoe.view

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ GameFooterMessage, GameResult, Piece, Player }
import zio._
import zio.macros.accessible

package object game {
  type GameView = Has[GameView.Service]

  @accessible
  object GameView {
    trait Service {
      def header(result: GameResult, turn: Piece, player: Player): UIO[String]
      def content(board: Map[Field, Piece], result: GameResult): UIO[String]
      def footer(message: GameFooterMessage): UIO[String]
    }
    val live: ULayer[GameView] = ZLayer.succeed {
      new Service {
        override def header(result: GameResult, turn: Piece, player: Player): UIO[String] =
          UIO.succeed(result) map {
            case GameResult.Ongoing if player == Player.Human =>
              s"""$turn turn
                 |
                 |Select field number or type `menu` and confirm with <enter>.""".stripMargin

            case GameResult.Ongoing if player == Player.Ai =>
              s"""$turn turn
                 |
                 |Calculating computer opponent move. Press <enter> to continue.""".stripMargin

            case GameResult.Win(piece) =>
              s"""The game ended with $piece win.
                 |
                 |Press <enter> to continue.""".stripMargin

            case GameResult.Draw =>
              s"""The game ended in a draw.
                 |
                 |Press <enter> to continue.""".stripMargin
          }

        override def content(board: Map[Field, Piece], result: GameResult): UIO[String] =
          UIO.succeed {
            Field.all
              .map(field => board.get(field) -> field.value)
              .map {
                case (Some(Piece.Cross), _)  => "x"
                case (Some(Piece.Nought), _) => "o"
                case (None, value)           => if (result == GameResult.Ongoing) value.toString else " "
              }
              .sliding(3, 3)
              .map(fields => s""" ${fields.mkString(" ║ ")} """)
              .mkString("\n═══╬═══╬═══\n")
          }

        override def footer(message: GameFooterMessage): UIO[String] = UIO.succeed(message) map {
          case GameFooterMessage.Empty          => ""
          case GameFooterMessage.InvalidCommand => "Invalid command. Try again."
          case GameFooterMessage.FieldOccupied  => "Field occupied. Try another."
        }
      }
    }

    val dummy: ULayer[GameView] = ZLayer.succeed {
      new Service {
        override def header(result: GameResult, turn: Piece, player: Player): UIO[String] = UIO.succeed("")
        override def content(board: Map[Field, Piece], result: GameResult): UIO[String]   = UIO.succeed("")
        override def footer(message: GameFooterMessage): UIO[String]                      = UIO.succeed("")
      }
    }
  }
}
