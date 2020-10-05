package com.example.tictactoe

import com.example.tictactoe.controller.Controller
import com.example.tictactoe.gameLogic.GameLogic
import com.example.tictactoe.mode.confirm.ConfirmMode
import com.example.tictactoe.mode.game.GameMode
import com.example.tictactoe.mode.menu.MenuMode
import com.example.tictactoe.opponentAi.OpponentAi
import com.example.tictactoe.parser.confirm.ConfirmCommandParser
import com.example.tictactoe.parser.game.GameCommandParser
import com.example.tictactoe.parser.menu.MenuCommandParser
import com.example.tictactoe.terminal.Terminal
import com.example.tictactoe.view.confirm.ConfirmView
import com.example.tictactoe.view.game.GameView
import com.example.tictactoe.view.menu.MenuView
import zio.test.mock._

object mocks {
  @mockable[Controller.Service]
  object ControllerMock

  @mockable[ConfirmMode.Service]
  object ConfirmModeMock

  @mockable[GameMode.Service]
  object GameModeMock

  @mockable[MenuMode.Service]
  object MenuModeMock

  @mockable[ConfirmCommandParser.Service]
  object ConfirmCommandParserMock

  @mockable[GameCommandParser.Service]
  object GameCommandParserMock

  @mockable[MenuCommandParser.Service]
  object MenuCommandParserMock

  @mockable[ConfirmView.Service]
  object ConfirmViewMock

  @mockable[GameView.Service]
  object GameViewMock

  @mockable[MenuView.Service]
  object MenuViewMock

  @mockable[GameLogic.Service]
  object GameLogicMock

  @mockable[OpponentAi.Service]
  object OpponentAiMock

  @mockable[Terminal.Service]
  object TerminalMock
}
