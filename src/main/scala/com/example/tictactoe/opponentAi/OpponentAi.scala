package com.example.tictactoe.opponentAi

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.Piece
import zio._

trait OpponentAi {
  def randomMove(board: Map[Field, Piece]): UIO[Field]
}

object OpponentAi {
  def randomMove(board: Map[Field, Piece]): ZIO[OpponentAi, Nothing, Field] =
    ZIO.serviceWithZIO[OpponentAi](_.randomMove(board))
}
