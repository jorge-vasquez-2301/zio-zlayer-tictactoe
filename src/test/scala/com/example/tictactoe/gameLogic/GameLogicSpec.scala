package com.example.tictactoe.gameLogic

import zio.test._
import zio.test.Assertion._
import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ Board, GameResult, Piece }
import zio._

object GameLogicSpec extends DefaultRunnableSpec {
  def spec =
    suite("GameLogic")(
      suite("putPiece")(
        testM("returns updated board if field is unoccupied") {
          val result = GameLogic.putPiece(board, Field.East, Piece.Cross).either
          assertM(result)(isRight(equalTo(updatedBoard)))
        },
        testM("fails if field is occupied") {
          val result = GameLogic.putPiece(board, Field.South, Piece.Cross).either
          assertM(result)(isLeft(isUnit))
        }
      ),
      suite("gameResult")(
        testM("returns GameResult.Win(Piece.Cross) if cross won") {
          val result = GameLogic.gameResult(crossWinBoard)
          assertM(result)(equalTo(GameResult.Win(Piece.Cross)))
        },
        testM("returns GameResult.Win(Piece.Nought) if nought won") {
          val result = GameLogic.gameResult(noughtWinBoard)
          assertM(result)(equalTo(GameResult.Win(Piece.Nought)))
        },
        testM("returns GameResult.Draw if the board is full and there are no winners") {
          val result = GameLogic.gameResult(drawBoard)
          assertM(result)(equalTo(GameResult.Draw))
        },
        testM("returns GameResult.Ongoing if the board is not full and there are no winners") {
          val result = GameLogic.gameResult(ongoingBoard)
          assertM(result)(equalTo(GameResult.Ongoing))
        },
        testM("returns GameResult.Ongoing if the board is empty") {
          val result = GameLogic.gameResult(emptyBoard)
          assertM(result)(equalTo(GameResult.Ongoing))
        },
        testM("dies with IllegalStateException if both players are in winning position") {
          val result = GameLogic.gameResult(bothWinBoard).absorb.either
          assertM(result)(isLeft(isSubtype[IllegalStateException](anything)))

        },
        testM("returns GameResult.Win for all possible 3-field straight lines")(
          for {
            wins <- Board.wins
            results <- ZIO.foreach(wins) { fields =>
                        val board: Map[Field, Piece] = fields.map(_ -> Piece.Cross).toMap
                        GameLogic.gameResult(board)
                      }
          } yield assert(results)(forall(equalTo(GameResult.Win(Piece.Cross))))
        ),
        testM("returns GameResult.Win(Piece.Cross) for example game") {
          val result = GameLogic.gameResult(exampleGameBoard)
          assertM(result)(equalTo(GameResult.Win(Piece.Cross)))
        }
      ),
      suite("nextTurn")(
        testM("returns Piece.Nought given Piece.Cross") {
          val result = GameLogic.nextTurn(Piece.Cross)
          assertM(result)(equalTo(Piece.Nought))
        },
        testM("returns Piece.Cross given Piece.Nought") {
          val result = GameLogic.nextTurn(Piece.Nought)
          assertM(result)(equalTo(Piece.Cross))
        }
      )
    ).provideCustomLayer(GameLogic.Service.live)

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
