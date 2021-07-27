package com.example.tictactoe.opponentAi

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ AppError, FullBoardError, Piece }
import zio._

final case class OpponentAiLive(random: Random) extends OpponentAi {
  override def randomMove(board: Map[Field, Piece]): IO[AppError, Field] = {
    val unoccupied = (Field.all.toSet -- board.keySet).toList.sortBy(_.value)
    unoccupied.size match {
      case 0 => IO.fail(FullBoardError)
      case n => random.nextIntBounded(n).map(unoccupied(_))
    }
  }
}
object OpponentAiLive {
  val layer: URLayer[Has[Random], Has[OpponentAi]] = (OpponentAiLive(_)).toLayer
}
