package com.example.tictactoe.opponentAi

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.Piece
import zio._
import zio.macros._

@accessible
trait OpponentAi {
  def randomMove(board: Map[Field, Piece]): UIO[Field]
}
