package com.example.tictactoe.gameLogic

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ Board, FieldAlreadyOccupiedError, GameResult, Piece }
import zio._
import zio.test._

object GameLogicSpec extends ZIOSpecDefault {
  def spec =
    suite("GameLogic")(
      suite("putPiece")(
        test("returns updated board if field is unoccupied") {
          for {
            result <- GameLogic.putPiece(board, Field.East, Piece.Cross).either.right
          } yield assertTrue(result == updatedBoard)
        },
        test("fails if field is occupied") {
          for {
            result <- GameLogic.putPiece(board, Field.South, Piece.Cross).either.left
          } yield assertTrue(result == FieldAlreadyOccupiedError)
        }
      ),
      suite("gameResult")(
        test("returns GameResult.Win(Piece.Cross) if cross won") {
          for {
            result <- GameLogic.gameResult(crossWinBoard)
          } yield assertTrue(result == GameResult.Win(Piece.Cross))
        },
        test("returns GameResult.Win(Piece.Nought) if nought won") {
          for {
            result <- GameLogic.gameResult(noughtWinBoard)
          } yield assertTrue(result == GameResult.Win(Piece.Nought))
        },
        test("returns GameResult.Draw if the board is full and there are no winners") {
          for {
            result <- GameLogic.gameResult(drawBoard)
          } yield assertTrue(result == GameResult.Draw)
        },
        test("returns GameResult.Ongoing if the board is not full and there are no winners") {
          for {
            result <- GameLogic.gameResult(ongoingBoard)
          } yield assertTrue(result == GameResult.Ongoing)
        },
        test("returns GameResult.Ongoing if the board is empty") {
          for {
            result <- GameLogic.gameResult(emptyBoard)
          } yield assertTrue(result == GameResult.Ongoing)
        },
        test("dies with IllegalStateException if both players are in winning position") {
          for {
            result <- GameLogic.gameResult(bothWinBoard).absorb.either.left
          } yield assertTrue(result.isInstanceOf[IllegalStateException])
        },
        test("returns GameResult.Win for all possible 3-field straight lines")(
          for {
            wins <- Board.wins
            results <- ZIO.foreach(wins) { fields =>
                        val board: Map[Field, Piece] = fields.map(_ -> Piece.Cross).toMap
                        GameLogic.gameResult(board)
                      }
          } yield assertTrue(results.forall(_ == GameResult.Win(Piece.Cross)))
        ),
        test("returns GameResult.Win(Piece.Cross) for example game") {
          for {
            result <- GameLogic.gameResult(exampleGameBoard)
          } yield assertTrue(result == GameResult.Win(Piece.Cross))
        }
      ),
      suite("nextTurn")(
        test("returns Piece.Nought given Piece.Cross") {
          for {
            result <- GameLogic.nextTurn(Piece.Cross)
          } yield assertTrue(result == Piece.Nought)
        },
        test("returns Piece.Cross given Piece.Nought") {
          for {
            result <- GameLogic.nextTurn(Piece.Nought)
          } yield assertTrue(result == Piece.Cross)
        }
      )
    ).provideLayer(GameLogicLive.layer)

  private val board = Map[Field, Piece](
    Field.North -> Piece.Cross,
    Field.South -> Piece.Nought
  )

  private val updatedBoard = Map[Field, Piece](
    Field.North -> Piece.Cross,
    Field.South -> Piece.Nought,
    Field.East  -> Piece.Cross
  )

  private val crossWinBoard = Map[Field, Piece](
    Field.North     -> Piece.Cross,
    Field.South     -> Piece.Nought,
    Field.NorthWest -> Piece.Cross,
    Field.SouthWest -> Piece.Nought,
    Field.NorthEast -> Piece.Cross
  )

  private val noughtWinBoard = Map[Field, Piece](
    Field.North     -> Piece.Nought,
    Field.South     -> Piece.Cross,
    Field.NorthWest -> Piece.Nought,
    Field.SouthWest -> Piece.Cross,
    Field.NorthEast -> Piece.Nought
  )

  private val bothWinBoard = Map[Field, Piece](
    Field.North     -> Piece.Cross,
    Field.South     -> Piece.Nought,
    Field.NorthWest -> Piece.Cross,
    Field.SouthWest -> Piece.Nought,
    Field.NorthEast -> Piece.Cross,
    Field.SouthEast -> Piece.Nought
  )

  private val drawBoard = Map[Field, Piece](
    Field.NorthWest -> Piece.Cross,
    Field.North     -> Piece.Cross,
    Field.NorthEast -> Piece.Nought,
    Field.West      -> Piece.Nought,
    Field.Center    -> Piece.Nought,
    Field.East      -> Piece.Cross,
    Field.SouthWest -> Piece.Cross,
    Field.South     -> Piece.Nought,
    Field.SouthEast -> Piece.Cross
  )

  private val ongoingBoard = Map[Field, Piece](
    Field.North -> Piece.Nought,
    Field.South -> Piece.Cross
  )

  private val exampleGameBoard = Map[Field, Piece](
    Field.NorthWest -> Piece.Cross,
    Field.North     -> Piece.Nought,
    Field.NorthEast -> Piece.Cross,
    Field.East      -> Piece.Nought,
    Field.Center    -> Piece.Cross,
    Field.West      -> Piece.Nought,
    Field.SouthWest -> Piece.Cross
  )

  private val emptyBoard = Map.empty[Field, Piece]
}
