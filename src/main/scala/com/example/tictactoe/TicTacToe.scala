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
    val opponentAiNoDeps: ULayer[OpponentAi] = Random.live >>> OpponentAi.Service.live

    val confirmModeDeps: ULayer[ConfirmCommandParser with ConfirmView] =
      ConfirmCommandParser.Service.live ++ ConfirmView.Service.live
    val menuModeDeps: ULayer[MenuCommandParser with MenuView] =
      MenuCommandParser.Service.live ++ MenuView.Service.live
    val gameModeDeps: ULayer[GameCommandParser with GameView with GameLogic with OpponentAi] =
      GameCommandParser.Service.live ++ GameView.Service.live ++ GameLogic.Service.live ++ opponentAiNoDeps

    val confirmModeNoDeps: ULayer[ConfirmMode] = confirmModeDeps >>> ConfirmMode.Service.live
    val menuModeNoDeps: ULayer[MenuMode]       = menuModeDeps >>> MenuMode.Service.live
    val gameModeNoDeps: ULayer[GameMode]       = gameModeDeps >>> GameMode.Service.live

    val controllerDeps: ULayer[ConfirmMode with GameMode with MenuMode] =
      confirmModeNoDeps ++ gameModeNoDeps ++ menuModeNoDeps

    val controllerNoDeps: ULayer[Controller] = controllerDeps >>> Controller.Service.live
    val terminalNoDeps: ULayer[Terminal]     = Console.live >>> Terminal.Service.live

    val runLoopNoDeps = (controllerNoDeps ++ terminalNoDeps) >>> RunLoop.Service.live

    runLoopNoDeps
  }
}
