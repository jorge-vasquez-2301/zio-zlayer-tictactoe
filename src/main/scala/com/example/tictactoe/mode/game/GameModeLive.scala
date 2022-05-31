package com.example.tictactoe.mode.game

import com.example.tictactoe.domain.Board.Field
import com.example.tictactoe.domain._
import com.example.tictactoe.gameLogic.GameLogic
import com.example.tictactoe.opponentAi.OpponentAi
import com.example.tictactoe.parser.game.GameCommandParser
import com.example.tictactoe.view.game.GameView
import zio._

final case class GameModeLive(
  gameCommandParser: GameCommandParser,
  gameView: GameView,
  opponentAi: OpponentAi,
  gameLogic: GameLogic
) extends GameMode {
  def process(input: String, state: State.Game): UIO[State] =
    if (state.result != GameResult.Ongoing) ZIO.succeed(State.Menu(None, MenuFooterMessage.Empty))
    else if (isAiTurn(state))
      opponentAi
        .randomMove(state.board)
        .flatMap(takeField(_, state))
    else
      gameCommandParser
        .parse(input)
        .flatMap {
          case GameCommand.Menu       => ZIO.succeed(State.Menu(Some(state), MenuFooterMessage.Empty))
          case GameCommand.Put(field) => takeField(field, state)
        }
        .orElseSucceed(state.copy(footerMessage = GameFooterMessage.InvalidCommand))

  private def isAiTurn(state: State.Game): Boolean =
    (state.turn == Piece.Cross && state.cross == Player.Ai) ||
      (state.turn == Piece.Nought && state.nought == Player.Ai)

  private def takeField(field: Field, state: State.Game): UIO[State] =
    (for {
      updatedBoard  <- gameLogic.putPiece(state.board, field, state.turn)
      updatedResult <- gameLogic.gameResult(updatedBoard)
      updatedTurn   <- gameLogic.nextTurn(state.turn)
    } yield state.copy(
      board = updatedBoard,
      result = updatedResult,
      turn = updatedTurn,
      footerMessage = GameFooterMessage.Empty
    )).orElseSucceed(state.copy(footerMessage = GameFooterMessage.FieldOccupied))

  def render(state: State.Game): UIO[String] = {
    val player = if (state.turn == Piece.Cross) state.cross else state.nought
    for {
      header  <- gameView.header(state.result, state.turn, player)
      content <- gameView.content(state.board, state.result)
      footer  <- gameView.footer(state.footerMessage)
    } yield List(header, content, footer).mkString("\n\n")
  }
}
object GameModeLive {
  val layer: URLayer[
    GameCommandParser with GameView with OpponentAi with GameLogic,
    GameMode
  ] = ZLayer.fromFunction(GameModeLive(_, _, _, _))
}
