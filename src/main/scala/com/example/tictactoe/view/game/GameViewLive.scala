package com.example.tictactoe.view.game

import com.example.tictactoe.config.AppConfig
import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ GameFooterMessage, GameResult, Piece, Player }
import zio._

final case class GameViewLive() extends GameView {
  def header(result: GameResult, turn: Piece, player: Player): UIO[String] =
    ZIO.succeed(result) map {
      case GameResult.Ongoing if player == Player.Human =>
        s"""$turn turn
           |
           |Select field number or type `menu` and confirm with <enter>.""".stripMargin
      case GameResult.Ongoing =>
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

  def content(board: Map[Field, Piece], result: GameResult): UIO[String] =
    ZIO.succeed {
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

  def footer(message: GameFooterMessage): UIO[String] =
    ZIO
      .config(AppConfig.config.map(_.view.game))
      .map { config =>
        message match {
          case GameFooterMessage.Empty          => ""
          case GameFooterMessage.InvalidCommand => config.invalidCommandMessage
          case GameFooterMessage.FieldOccupied  => config.fieldOccupiedMessage
        }
      }
      .orDie
}
object GameViewLive {
  val layer: ULayer[GameView] = ZLayer.succeed(GameViewLive())
}
