package com.example.tictactoe.gameLogic

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ AppError, GameResult, Piece }
import zio._

trait GameLogic {
  def putPiece(board: Map[Field, Piece], field: Field, piece: Piece): IO[AppError, Map[Field, Piece]]
  def gameResult(board: Map[Field, Piece]): UIO[GameResult]
  def nextTurn(currentTurn: Piece): UIO[Piece]
}

object GameLogic {
  def putPiece(board: Map[Field, Piece], field: Field, piece: Piece): ZIO[GameLogic, AppError, Map[Field, Piece]] =
    ZIO.serviceWithZIO[GameLogic](_.putPiece(board, field, piece))

  def gameResult(board: Map[Field, Piece]): ZIO[GameLogic, Nothing, GameResult] =
    ZIO.serviceWithZIO[GameLogic](_.gameResult(board))

  def nextTurn(currentTurn: Piece): ZIO[GameLogic, Nothing, Piece] =
    ZIO.serviceWithZIO[GameLogic](_.nextTurn(currentTurn))
}
