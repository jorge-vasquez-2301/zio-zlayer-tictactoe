package com.example.tictactoe.view.game

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain.{ GameFooterMessage, GameResult, Piece }
import zio.ZIO
import zio.test._

object GameViewSpec extends ZIOSpecDefault {
  def spec =
    suite("GameView")(
      suite("content renders")(
        test("empty board") {
          for {
            result <- GameView.content(emptyBoard, GameResult.Ongoing)
            _      <- ZIO.debug("result diff emptyBoardView")
            _      <- ZIO.debug(result diff emptyBoardView)
            _      <- ZIO.debug("emptyBoardView diff result")
            _      <- ZIO.debug(emptyBoardView diff result)
          } yield assertTrue(result == emptyBoardView)
        },
        test("non empty board") {
          for {
            result <- GameView.content(nonEmptyBoard, GameResult.Ongoing)
          } yield assertTrue(result == nonEmptyBoardView)
        }
      ),
      suite("footer renders message")(
        test("Empty") {
          for {
            result <- GameView.footer(GameFooterMessage.Empty)
          } yield assertTrue(result == emptyMessage)
        },
        test("InvalidCommand") {
          for {
            result <- GameView.footer(GameFooterMessage.InvalidCommand)
          } yield assertTrue(result == invalidCommandMessage)
        }
      )
    ).provideLayer(GameViewLive.layer)

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
