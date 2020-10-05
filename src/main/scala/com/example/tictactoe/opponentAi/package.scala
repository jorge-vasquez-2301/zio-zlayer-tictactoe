package com.example.tictactoe

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ AppError, FullBoardError, Piece }
import zio._
import zio.macros.accessible
import zio.random._

package object opponentAi {
  type OpponentAi = Has[OpponentAi.Service]

  @accessible
  object OpponentAi {
    trait Service {
      def randomMove(board: Map[Field, Piece]): IO[AppError, Field]
    }
    val live: URLayer[Random, OpponentAi] = ZLayer.fromService { randomService =>
      new Service {
        override def randomMove(board: Map[Field, Piece]): IO[AppError, Field] = {
          val unoccupied = (Field.all.toSet -- board.keySet).toList.sortBy(_.value)
          unoccupied.size match {
            case 0 => IO.fail(FullBoardError)
            case n => randomService.nextIntBounded(n).map(unoccupied(_))
          }
        }
      }
    }

    val dummy: ULayer[OpponentAi] = ZLayer.succeed {
      new Service {
        override def randomMove(board: Map[Field, Piece]): IO[AppError, Field] = IO.fail(FullBoardError)
      }
    }
  }
}
