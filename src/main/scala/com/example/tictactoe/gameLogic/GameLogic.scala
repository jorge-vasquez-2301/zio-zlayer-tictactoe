package com.example.tictactoe.gameLogic

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{AppError, GameResult, Piece}
import zio._
import zio.macros._

@accessible
trait GameLogic {
  def putPiece(board: Map[Field, Piece], field: Field, piece: Piece): IO[AppError, Map[Field, Piece]]
  def gameResult(board: Map[Field, Piece]): UIO[GameResult]
  def nextTurn(currentTurn: Piece): UIO[Piece]
}
