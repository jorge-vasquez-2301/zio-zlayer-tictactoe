package com.example.tictactoe

import com.example.tictactoe.opponentAi.OpponentAi
import com.example.tictactoe.controller.Controller
import com.example.tictactoe.domain.State
import com.example.tictactoe.gameLogic.GameLogic
import com.example.tictactoe.mode.confirm.ConfirmMode
import com.example.tictactoe.mode.game.GameMode
import com.example.tictactoe.mode.menu.MenuMode
import com.example.tictactoe.parser.confirm.ConfirmCommandParser
import com.example.tictactoe.parser.game.GameCommandParser
import com.example.tictactoe.parser.menu.MenuCommandParser
import com.example.tictactoe.runLoop.RunLoop
import com.example.tictactoe.terminal.Terminal
import com.example.tictactoe.view.confirm.ConfirmView
import com.example.tictactoe.view.game.GameView
import com.example.tictactoe.view.menu.MenuView
import zio._
import zio.console.Console
import zio.random.Random

object TicTacToe extends App {

  val program: URIO[RunLoop, Unit] = {
    def loop(state: State): URIO[RunLoop, Unit] =
      RunLoop
        .step(state)
        .flatMap(loop)
        .ignore

    loop(State.initial)
  }

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = program.provideLayer(prepareEnvironment).as(0)

  private val prepareEnvironment: ULayer[RunLoop] = {
    val parsers =
      ConfirmCommandParser.Service.live ++ GameCommandParser.Service.live ++ MenuCommandParser.Service.live

    val views = ConfirmView.Service.live ++ GameView.Service.live ++ MenuView.Service.live

    val gameModes = ConfirmMode.Service.live ++ GameMode.Service.live ++ MenuMode.Service.live

    (parsers ++ views ++ (Random.live >>> OpponentAi.Service.live) ++ GameLogic.Service.live) >>>
      (gameModes ++ Console.live) >>>
      (Controller.Service.live ++ Terminal.Service.live) >>>
      RunLoop.Service.live
  }
}
