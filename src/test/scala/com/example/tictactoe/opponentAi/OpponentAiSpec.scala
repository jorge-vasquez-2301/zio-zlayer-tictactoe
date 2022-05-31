package com.example.tictactoe.opponentAi

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.Piece
import zio.test._

object OpponentAiSpec extends ZIOSpecDefault {
  private val board = Map[Field, Piece](
    Field.NorthWest -> Piece.Cross,
    Field.West      -> Piece.Nought,
    Field.Center    -> Piece.Cross,
    Field.SouthEast -> Piece.Nought
  )

  private val fullBoard = Map[Field, Piece](
    Field.NorthWest -> Piece.Cross,
    Field.North     -> Piece.Cross,
    Field.NorthEast -> Piece.Cross,
    Field.West      -> Piece.Nought,
    Field.Center    -> Piece.Cross,
    Field.East      -> Piece.Cross,
    Field.SouthWest -> Piece.Nought,
    Field.South     -> Piece.Nought,
    Field.SouthEast -> Piece.Nought
  )

  def spec =
    suite("OpponentAi")(
      test("randomMove chooses a random, unoccupied field") {
        for {
          _      <- TestRandom.feedInts(1)
          result <- OpponentAi.randomMove(board)
        } yield assertTrue(result == Field.NorthEast)
      },
      test("randomMove dies when board is fully occupied") {
        for {
          result <- OpponentAi.randomMove(fullBoard).absorb.either.left
        } yield assertTrue(result.isInstanceOf[IllegalStateException])
      }
    ).provide(OpponentAiLive.layer)
}
