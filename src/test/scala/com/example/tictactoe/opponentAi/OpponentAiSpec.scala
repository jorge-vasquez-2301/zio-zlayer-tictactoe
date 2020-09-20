package com.example.tictactoe.opponentAi

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ FullBoardError, Piece }
import zio._
import zio.random._
import zio.test._
import zio.test.Assertion._
import zio.test.mock.Expectation._
import zio.test.mock._

object OpponentAiSpec extends DefaultRunnableSpec {
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

  def spec = suite("OpponentAi")(
    testM("randomMove chooses a random, unoccupied field") {
      val randomMock: ULayer[Random] = MockRandom.NextIntBounded(equalTo(5), value(1))
      val env: ULayer[OpponentAi]    = randomMock >>> OpponentAi.live
      val result                     = OpponentAi.randomMove(board).either.provideLayer(env)
      assertM(result)(isRight(equalTo(Field.NorthEast)))
    },
    testM("randomMove fails when board is fully occupied") {
      val result = OpponentAi.randomMove(fullBoard).either.provideCustomLayer(OpponentAi.live)
      assertM(result)(isLeft(equalTo(FullBoardError)))
    }
  )
}
