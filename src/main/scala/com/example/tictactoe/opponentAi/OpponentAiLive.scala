package com.example.tictactoe.opponentAi

import com.example.tictactoe.config.AppConfig
import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.Piece
import zio._

final case class OpponentAiLive() extends OpponentAi {
  override def randomMove(board: Map[Field, Piece]): UIO[Field] = {
    val unoccupied = (Field.all.toSet -- board.keySet).toList.sortBy(_.value)
    unoccupied.size match {
      case 0 => ZIO.die(new IllegalStateException("Board is full"))
      case n =>
        ZIO
          .config(AppConfig.config.map(_.ai))
          .flatMap { config =>
            if (config.randomGenRepetitions > 0)
              Random.nextIntBounded(n).repeatN(config.randomGenRepetitions).map(unoccupied(_))
            else Random.nextIntBounded(n).map(unoccupied(_))
          }
          .orDie
    }
  }
}
object OpponentAiLive {
  val layer: ULayer[OpponentAi] = ZLayer.succeed(OpponentAiLive())
}
