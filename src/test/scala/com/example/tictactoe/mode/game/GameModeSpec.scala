package com.example.tictactoe.mode.game

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain._
import com.example.tictactoe.gameLogic.GameLogic
import com.example.tictactoe.mocks.{ GameCommandParserMock, GameLogicMock, GameViewMock, OpponentAiMock }
import com.example.tictactoe.parser.game.GameCommandParser
import zio._
import zio.test.Assertion._
import zio.test._
import zio.test.mock.Expectation._

object GameModeSpec extends DefaultRunnableSpec {
  def spec = suite("GameMode")(
    suite("process")(
      test("menu returns suspended menu state") {
        val gameCommandParserMock: ULayer[Has[GameCommandParser]] =
          GameCommandParserMock.Parse(equalTo("menu"), value(GameCommand.Menu))
        val result = GameMode
          .process("menu", gameState)
          .inject(
            gameCommandParserMock,
            GameViewMock.empty,
            OpponentAiMock.empty,
            GameLogicMock.empty,
            GameModeLive.layer
          )
        assertM(result)(equalTo(suspendedMenuState))
      },
      suite("put <field>")(
        test("returns current state with GameMessage.FieldOccupied if field is occupied") {
          val gameCommandParserMock: ULayer[Has[GameCommandParser]] =
            GameCommandParserMock.Parse(equalTo("put 2"), value(GameCommand.Put(Field.North)))
          val gameLogicMock: ULayer[Has[GameLogic]] =
            GameLogicMock
              .PutPiece(equalTo((gameState.board, Field.North, Piece.Cross)), failure(FieldAlreadyOccupiedError))
          val result = GameMode
            .process("put 2", gameState)
            .inject(
              gameCommandParserMock,
              GameViewMock.empty,
              OpponentAiMock.empty,
              gameLogicMock,
              GameModeLive.layer
            )
          assertM(result)(equalTo(fieldOccupiedState))
        },
        test("returns state with added piece and turn advanced to next player if field is unoccupied") {
          val gameCommandParserMock: ULayer[Has[GameCommandParser]] =
            GameCommandParserMock.Parse(equalTo("put 6"), value(GameCommand.Put(Field.East)))
          val gameLogicMock: ULayer[Has[GameLogic]] =
            GameLogicMock.PutPiece(
              equalTo((gameState.board, Field.East, Piece.Cross)),
              value(pieceAddedEastState.board)
            ) ++
              GameLogicMock.GameResult(equalTo(pieceAddedEastState.board), value(GameResult.Ongoing)) ++
              GameLogicMock.NextTurn(equalTo(Piece.Cross), value(Piece.Nought))
          val result = GameMode
            .process("put 6", gameState)
            .inject(
              gameCommandParserMock,
              GameViewMock.empty,
              OpponentAiMock.empty,
              gameLogicMock,
              GameModeLive.layer
            )
          assertM(result)(equalTo(pieceAddedEastState))
        },
        test("otherwise returns current state with GameMessage.InvalidCommand") {
          val gameCommandParserMock: ULayer[Has[GameCommandParser]] =
            GameCommandParserMock.Parse(equalTo("foo"), failure(ParseError))
          val result = GameMode
            .process("foo", gameState)
            .inject(
              gameCommandParserMock,
              GameViewMock.empty,
              OpponentAiMock.empty,
              GameLogicMock.empty,
              GameModeLive.layer
            )
          assertM(result)(equalTo(invalidCommandState))
        }
      )
    )
  )

  private val gameState = State.Game(
    Map(
      Field.North -> Piece.Cross,
      Field.South -> Piece.Nought
    ),
    Player.Human,
    Player.Ai,
    Piece.Cross,
    GameResult.Ongoing,
    GameFooterMessage.Empty
  )

  private val suspendedMenuState = State.Menu(Some(gameState), MenuFooterMessage.Empty)
  private val fieldOccupiedState = gameState.copy(footerMessage = GameFooterMessage.FieldOccupied)
  private val pieceAddedEastState = State.Game(
    Map(
      Field.North -> Piece.Cross,
      Field.South -> Piece.Nought,
      Field.East  -> Piece.Cross
    ),
    Player.Human,
    Player.Ai,
    Piece.Nought,
    GameResult.Ongoing,
    GameFooterMessage.Empty
  )

  private val invalidCommandState = gameState.copy(footerMessage = GameFooterMessage.InvalidCommand)
}
