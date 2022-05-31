package com.example.tictactoe.view.game

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ GameFooterMessage, GameResult, Piece, Player }
import zio._
import zio.macros._

@accessible
trait GameView {
  def header(result: GameResult, turn: Piece, player: Player): UIO[String]
  def content(board: Map[Field, Piece], result: GameResult): UIO[String]
  def footer(message: GameFooterMessage): UIO[String]
}
