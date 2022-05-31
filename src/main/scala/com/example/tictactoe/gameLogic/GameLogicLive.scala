package com.example.tictactoe.gameLogic

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ AppError, Board, FieldAlreadyOccupiedError, GameResult, Piece }
import zio._

final case class GameLogicLive() extends GameLogic {
  def putPiece(board: Map[Field, Piece], field: Field, piece: Piece): IO[AppError, Map[Field, Piece]] =
    board.get(field) match {
      case None => ZIO.succeed(board.updated(field, piece))
      case _    => ZIO.fail(FieldAlreadyOccupiedError)
    }

  def gameResult(board: Map[Field, Piece]): UIO[GameResult] = {
    val pieces: Map[Piece, Set[Field]] =
      board
        .groupBy(_._2)
        .view
        .mapValues(_.keys.toSet)
        .toMap
        .withDefaultValue(Set.empty)
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
                   else if (crossWin) ZIO.succeed(GameResult.Win(Piece.Cross))
                   else if (noughtWin) ZIO.succeed(GameResult.Win(Piece.Nought))
                   else if (boardFull) ZIO.succeed(GameResult.Draw)
                   else ZIO.succeed(GameResult.Ongoing)
    } yield gameResult
  }

  def nextTurn(currentTurn: Piece): UIO[Piece] = ZIO.succeed(currentTurn) map {
    case Piece.Cross  => Piece.Nought
    case Piece.Nought => Piece.Cross
  }
}
object GameLogicLive {
  val layer: ULayer[GameLogic] = ZLayer.succeed(GameLogicLive())
}
