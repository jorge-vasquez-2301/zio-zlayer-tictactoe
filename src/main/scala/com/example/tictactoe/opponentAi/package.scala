package com.example.tictactoe

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.Piece
import zio._
import zio.random._

package object opponentAi {
  type OpponentAi = Has[OpponentAi.Service]
  object OpponentAi {
    trait Service {
      def randomMove(board: Map[Field, Piece]): IO[Unit, Field]
    }
    object Service {
      val live: URLayer[Random, OpponentAi] = ZLayer.fromService { randomService =>
        new Service {
          override def randomMove(board: Map[Field, Piece]): IO[Unit, Field] = {
            val unoccupied = (Field.all.toSet -- board.keySet).toList.sortBy(_.value)
            unoccupied.size match {
              case 0 => IO.fail(())
              case n => randomService.nextInt(n).map(unoccupied(_))
            }
          }
        }
      }

      val dummy: ULayer[OpponentAi] = ZLayer.succeed {
        new Service {
          override def randomMove(board: Map[Field, Piece]): IO[Unit, Field] = IO.fail(())
        }
      }
    }

    // accessors
    def randomMove(board: Map[Field, Piece]): ZIO[OpponentAi, Unit, Field] = ZIO.accessM(_.get.randomMove(board))
  }
}
