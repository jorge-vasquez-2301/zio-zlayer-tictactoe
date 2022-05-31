package com.example.tictactoe.mode.game

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain._
import com.example.tictactoe.gameLogic.GameLogic
import com.example.tictactoe.mocks.{ GameCommandParserMock, GameLogicMock, GameViewMock, OpponentAiMock }
import com.example.tictactoe.parser.game.GameCommandParser
import zio._
import zio.mock._
import zio.test._

object GameModeSpec extends ZIOSpecDefault {
  def spec = suite("GameMode")(
    suite("process")(
      test("menu returns suspended menu state") {
        val gameCommandParserMock: ULayer[GameCommandParser] =
          GameCommandParserMock.Parse(Assertion.equalTo("menu"), Expectation.value(GameCommand.Menu))
        for {
          result <- GameMode
                     .process("menu", gameState)
                     .provide(
                       gameCommandParserMock,
                       GameViewMock.empty,
                       OpponentAiMock.empty,
                       GameLogicMock.empty,
                       GameModeLive.layer
                     )
        } yield assertTrue(result == suspendedMenuState)
      },
      suite("put <field>")(
        test("returns current state with GameMessage.FieldOccupied if field is occupied") {
          val gameCommandParserMock: ULayer[GameCommandParser] =
            GameCommandParserMock.Parse(Assertion.equalTo("put 2"), Expectation.value(GameCommand.Put(Field.North)))
          val gameLogicMock: ULayer[GameLogic] =
            GameLogicMock
              .PutPiece(
                Assertion.equalTo((gameState.board, Field.North, Piece.Cross)),
                Expectation.failure(FieldAlreadyOccupiedError)
              )
          for {
            result <- GameMode
                       .process("put 2", gameState)
                       .provide(
                         gameCommandParserMock,
                         GameViewMock.empty,
                         OpponentAiMock.empty,
                         gameLogicMock,
                         GameModeLive.layer
                       )
          } yield assertTrue(result == fieldOccupiedState)
        },
        test("returns state with added piece and turn advanced to next player if field is unoccupied") {
          val gameCommandParserMock: ULayer[GameCommandParser] =
            GameCommandParserMock.Parse(Assertion.equalTo("put 6"), Expectation.value(GameCommand.Put(Field.East)))
          val gameLogicMock: ULayer[GameLogic] =
            GameLogicMock.PutPiece(
              Assertion.equalTo((gameState.board, Field.East, Piece.Cross)),
              Expectation.value(pieceAddedEastState.board)
            ) ++
              GameLogicMock
                .GameResult(Assertion.equalTo(pieceAddedEastState.board), Expectation.value(GameResult.Ongoing)) ++
              GameLogicMock.NextTurn(Assertion.equalTo(Piece.Cross), Expectation.value(Piece.Nought))
          for {
            result <- GameMode
                       .process("put 6", gameState)
                       .provide(
                         gameCommandParserMock,
                         GameViewMock.empty,
                         OpponentAiMock.empty,
                         gameLogicMock,
                         GameModeLive.layer
                       )
          } yield assertTrue(result == pieceAddedEastState)
        },
        test("otherwise returns current state with GameMessage.InvalidCommand") {
          val gameCommandParserMock: ULayer[GameCommandParser] =
            GameCommandParserMock.Parse(Assertion.equalTo("foo"), Expectation.failure(ParseError))
          for {
            result <- GameMode
                       .process("foo", gameState)
                       .provide(
                         gameCommandParserMock,
                         GameViewMock.empty,
                         OpponentAiMock.empty,
                         GameLogicMock.empty,
                         GameModeLive.layer
                       )
          } yield assertTrue(result == invalidCommandState)
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
