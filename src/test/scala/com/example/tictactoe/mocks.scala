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
import zio.mock._

object mocks {
  @mockable[Controller]
  object ControllerMock

  @mockable[ConfirmMode]
  object ConfirmModeMock

  @mockable[GameMode]
  object GameModeMock

  @mockable[MenuMode]
  object MenuModeMock

  @mockable[ConfirmCommandParser]
  object ConfirmCommandParserMock

  @mockable[GameCommandParser]
  object GameCommandParserMock

  @mockable[MenuCommandParser]
  object MenuCommandParserMock

  @mockable[ConfirmView]
  object ConfirmViewMock

  @mockable[GameView]
  object GameViewMock

  @mockable[MenuView]
  object MenuViewMock

  @mockable[GameLogic]
  object GameLogicMock

  @mockable[OpponentAi]
  object OpponentAiMock

  @mockable[Terminal]
  object TerminalMock
}
