package com.example.tictactoe.view.game

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ GameFooterMessage, GameResult, Piece }
import zio.test.Assertion._
import zio.test._

object GameViewSpec extends DefaultRunnableSpec {
  def spec =
    suite("GameView")(
      suite("content renders")(
        testM("empty board") {
          val result = GameView.content(emptyBoard, GameResult.Ongoing)
          assertM(result)(equalTo(emptyBoardView))
        },
        testM("non empty board") {
          val result = GameView.content(nonEmptyBoard, GameResult.Ongoing)
          assertM(result)(equalTo(nonEmptyBoardView))
        }
      ),
      suite("footer renders message")(
        testM("Empty") {
          val result = GameView.footer(GameFooterMessage.Empty)
          assertM(result)(equalTo(emptyMessage))
        },
        testM("InvalidCommand") {
          val result = GameView.footer(GameFooterMessage.InvalidCommand)
          assertM(result)(equalTo(invalidCommandMessage))
        }
      )
    ).provideCustomLayer(GameView.live)

  private val emptyBoard = Map.empty[Field, Piece]

  private val emptyBoardView =
    """ 1 ║ 2 ║ 3 
      |═══╬═══╬═══
      | 4 ║ 5 ║ 6 
      |═══╬═══╬═══
      | 7 ║ 8 ║ 9 """.stripMargin

  private val nonEmptyBoard = Map[Field, Piece](
    Field.NorthWest -> Piece.Cross,
    Field.West      -> Piece.Nought,
    Field.Center    -> Piece.Cross,
    Field.SouthEast -> Piece.Nought
  )

  private val nonEmptyBoardView =
    """ x ║ 2 ║ 3 
      |═══╬═══╬═══
      | o ║ x ║ 6 
      |═══╬═══╬═══
      | 7 ║ 8 ║ o """.stripMargin

  private val emptyMessage          = ""
  private val invalidCommandMessage = "Invalid command. Try again."
}
