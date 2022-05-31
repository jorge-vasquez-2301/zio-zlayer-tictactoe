package com.example.tictactoe.opponentAi

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.Piece
import zio._

final case class OpponentAiLive() extends OpponentAi {
  override def randomMove(board: Map[Field, Piece]): UIO[Field] = {
    val unoccupied = (Field.all.toSet -- board.keySet).toList.sortBy(_.value)
    unoccupied.size match {
      case 0 => ZIO.die(new IllegalStateException("Board is full"))
      case n => Random.nextIntBounded(n).map(unoccupied(_))
    }
  }
}
object OpponentAiLive {
  val layer: ULayer[OpponentAi] = ZLayer.succeed(OpponentAiLive())
}
