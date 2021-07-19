package com.example.tictactoe.gameLogic

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ AppError, GameResult, Piece }
import zio.{ Has, IO, UIO, URIO, ZIO }

trait GameLogic {
  def putPiece(board: Map[Field, Piece], field: Field, piece: Piece): IO[AppError, Map[Field, Piece]]
  def gameResult(board: Map[Field, Piece]): UIO[GameResult]
  def nextTurn(currentTurn: Piece): UIO[Piece]
}
object GameLogic {
  def putPiece(board: Map[Field, Piece], field: Field, piece: Piece): ZIO[Has[GameLogic], AppError, Map[Field, Piece]] =
    ZIO.serviceWith[GameLogic](_.putPiece(board, field, piece))

  def gameResult(board: Map[Field, Piece]): URIO[Has[GameLogic], GameResult] =
    ZIO.serviceWith[GameLogic](_.gameResult(board))

  def nextTurn(currentTurn: Piece): URIO[Has[GameLogic], Piece] =
    ZIO.serviceWith[GameLogic](_.nextTurn(currentTurn))
}
