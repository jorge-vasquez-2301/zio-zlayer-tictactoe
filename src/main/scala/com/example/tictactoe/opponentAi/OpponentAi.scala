package com.example.tictactoe.opponentAi

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ AppError, Piece }
import zio.{ Has, IO, ZIO }

trait OpponentAi {
  def randomMove(board: Map[Field, Piece]): IO[AppError, Field]
}
object OpponentAi {
  def randomMove(board: Map[Field, Piece]): ZIO[Has[OpponentAi], AppError, Field] =
    ZIO.serviceWith[OpponentAi](_.randomMove(board))
}
