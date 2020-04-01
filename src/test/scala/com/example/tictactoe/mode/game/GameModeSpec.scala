package com.example.tictactoe.mode.game

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain._
import com.example.tictactoe.gameLogic.GameLogic
import com.example.tictactoe.mocks.{ GameCommandParserMock, GameLogicMock }
import com.example.tictactoe.opponentAi.OpponentAi
import com.example.tictactoe.parser.game.GameCommandParser
import com.example.tictactoe.view.game.GameView
import zio._
import zio.test.Assertion._
import zio.test._
import zio.test.mock.Expectation._

object GameModeSpec extends DefaultRunnableSpec {
  def spec = suite("GameMode")(
    suite("process")(
      testM("menu returns suspended menu state") {
        val gameCommandParserMock: ULayer[GameCommandParser] =
          GameCommandParserMock.parse(equalTo("menu")) returns value(GameCommand.Menu)
        val env: ULayer[GameMode] =
          (gameCommandParserMock ++ GameView.Service.dummy ++ OpponentAi.Service.dummy ++ GameLogic.Service.dummy) >>> GameMode.Service.live

        val result = GameMode.process("menu", gameState).provideLayer(env)
        assertM(result)(equalTo(suspendedMenuState))
      },
      suite("put <field>")(
        testM("returns current state with GameMessage.FieldOccupied if field is occupied") {
          val gameCommandParserMock: ULayer[GameCommandParser] =
            GameCommandParserMock.parse(equalTo("put 2")) returns value(GameCommand.Put(Field.North))
          val gameLogicMock: ULayer[GameLogic] =
            GameLogicMock.putPiece(equalTo((gameState.board, Field.North, Piece.Cross))) returns failure(())
          val env: ULayer[GameMode] =
            (gameCommandParserMock ++ GameView.Service.dummy ++ OpponentAi.Service.dummy ++ gameLogicMock) >>> GameMode.Service.live

          val result = GameMode.process("put 2", gameState).provideLayer(env)
          assertM(result)(equalTo(fieldOccupiedState))
        },
        testM("returns state with added piece and turn advanced to next player if field is unoccupied") {
          val gameCommandParserMock: ULayer[GameCommandParser] =
            GameCommandParserMock.parse(equalTo("put 6")) returns value(GameCommand.Put(Field.East))
          val gameLogicMock: ULayer[GameLogic] =
            (GameLogicMock.putPiece(equalTo((gameState.board, Field.East, Piece.Cross))) returns value(
              pieceAddedEastState.board
            )) ++
              (GameLogicMock.gameResult(equalTo(pieceAddedEastState.board)) returns value(GameResult.Ongoing)) ++
              (GameLogicMock.nextTurn(equalTo(Piece.Cross)) returns value(Piece.Nought))
          val env: ULayer[GameMode] =
            (gameCommandParserMock ++ GameView.Service.dummy ++ OpponentAi.Service.dummy ++ gameLogicMock) >>> GameMode.Service.live

          val result = GameMode.process("put 6", gameState).provideLayer(env)
          assertM(result)(equalTo(pieceAddedEastState))
        },
        testM("otherwise returns current state with GameMessage.InvalidCommand") {
          val gameCommandParserMock: ULayer[GameCommandParser] =
            GameCommandParserMock.parse(equalTo("foo")) returns failure(())
          val env: ULayer[GameMode] =
            (gameCommandParserMock ++ GameView.Service.dummy ++ OpponentAi.Service.dummy ++ GameLogic.Service.dummy) >>> GameMode.Service.live

          val result = GameMode.process("foo", gameState).provideLayer(env)
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
