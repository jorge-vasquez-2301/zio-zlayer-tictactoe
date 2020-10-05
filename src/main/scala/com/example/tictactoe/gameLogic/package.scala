package com.example.tictactoe

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain._
import zio._
import zio.macros.accessible

package object gameLogic {
  type GameLogic = Has[GameLogic.Service]

  @accessible
  object GameLogic {
    trait Service {
      def putPiece(board: Map[Field, Piece], field: Field, piece: Piece): IO[AppError, Map[Field, Piece]]
      def gameResult(board: Map[Field, Piece]): UIO[GameResult]
      def nextTurn(currentTurn: Piece): UIO[Piece]
    }
    val live: ULayer[GameLogic] = ZLayer.succeed {
      new Service {
        override def putPiece(board: Map[Field, Piece], field: Field, piece: Piece): IO[AppError, Map[Field, Piece]] =
          board.get(field) match {
            case None => IO.succeed(board.updated(field, piece))
            case _    => IO.fail(FieldAlreadyOccupiedError)
          }

        override def gameResult(board: Map[Field, Piece]): UIO[GameResult] = {
          val pieces: Map[Piece, Set[Field]] =
            board
              .groupBy(_._2)
              .view
              .mapValues(_.keys.toSet)
              .toMap
              .withDefaultValue(Set.empty[Field])

          for {
            wins      <- Board.wins
            crossWin  = wins.exists(_ subsetOf pieces(Piece.Cross))
            noughtWin = wins.exists(_ subsetOf pieces(Piece.Nought))
            boardFull = board.size == 9
            gameResult <- if (crossWin && noughtWin)
                           ZIO.die {
                             new IllegalStateException(
                               "It should not be possible for both players to meet winning conditions."
                             )
                           }
                         else if (crossWin) UIO.succeed(GameResult.Win(Piece.Cross))
                         else if (noughtWin) UIO.succeed(GameResult.Win(Piece.Nought))
                         else if (boardFull) UIO.succeed(GameResult.Draw)
                         else UIO.succeed(GameResult.Ongoing)
          } yield gameResult
        }

        override def nextTurn(currentTurn: Piece): UIO[Piece] = UIO.succeed(currentTurn) map {
          case Piece.Cross  => Piece.Nought
          case Piece.Nought => Piece.Cross
        }
      }
    }

    val dummy: ULayer[GameLogic] = ZLayer.succeed {
      new Service {
        override def putPiece(board: Map[Field, Piece], field: Field, piece: Piece): IO[AppError, Map[Field, Piece]] =
          IO.succeed(Map.empty)

        override def gameResult(board: Map[Field, Piece]): UIO[GameResult] = UIO.succeed(GameResult.Draw)
        override def nextTurn(currentTurn: Piece): UIO[Piece]              = UIO.succeed(Piece.Cross)
      }
    }
  }
}
