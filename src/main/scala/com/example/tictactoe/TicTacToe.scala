package com.example.tictactoe

import com.example.tictactoe.controller.ControllerLive
import com.example.tictactoe.domain.State
import com.example.tictactoe.gameLogic.GameLogicLive
import com.example.tictactoe.mode.confirm.ConfirmModeLive
import com.example.tictactoe.mode.game.GameModeLive
import com.example.tictactoe.mode.menu.MenuModeLive
import com.example.tictactoe.opponentAi.OpponentAiLive
import com.example.tictactoe.parser.confirm.ConfirmCommandParserLive
import com.example.tictactoe.parser.game.GameCommandParserLive
import com.example.tictactoe.parser.menu.MenuCommandParserLive
import com.example.tictactoe.runLoop.{ RunLoop, RunLoopLive }
import com.example.tictactoe.terminal.TerminalLive
import com.example.tictactoe.view.confirm.ConfirmViewLive
import com.example.tictactoe.view.game.GameViewLive
import com.example.tictactoe.view.menu.MenuViewLive
import zio._

object TicTacToe extends App {

  val program: URIO[Has[RunLoop], Unit] = {
    def loop(state: State): URIO[Has[RunLoop], Unit] =
      RunLoop
        .step(state)
        .flatMap(loop)
        .ignore

    loop(State.initial)
  }

  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    program
      .injectCustom(
        ControllerLive.layer,
        GameLogicLive.layer,
        ConfirmModeLive.layer,
        GameModeLive.layer,
        MenuModeLive.layer,
        OpponentAiLive.layer,
        ConfirmCommandParserLive.layer,
        GameCommandParserLive.layer,
        MenuCommandParserLive.layer,
        RunLoopLive.layer,
        TerminalLive.layer,
        ConfirmViewLive.layer,
        GameViewLive.layer,
        MenuViewLive.layer
      )
      .exitCode
}
