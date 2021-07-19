package com.example.tictactoe.mode.menu

import com.example.tictactoe.domain._
import com.example.tictactoe.parser.menu.MenuCommandParser
import com.example.tictactoe.view.menu.MenuView
import zio._

final case class MenuModeLive(menuCommandParser: MenuCommandParser, menuView: MenuView) extends MenuMode {
  def process(input: String, state: State.Menu): UIO[State] =
    menuCommandParser
      .parse(input)
      .map {
        case MenuCommand.NewGame =>
          val newGameState =
            State.Game(
              Map.empty,
              Player.Human,
              Player.Ai,
              Piece.Cross,
              GameResult.Ongoing,
              GameFooterMessage.Empty
            )
          state.game match {
            case Some(_) =>
              State.Confirm(ConfirmAction.NewGame, newGameState, state, ConfirmFooterMessage.Empty)
            case None => newGameState
          }
        case MenuCommand.Resume =>
          state.game match {
            case Some(gameState) => gameState
            case None            => state.copy(footerMessage = MenuFooterMessage.InvalidCommand)
          }
        case MenuCommand.Quit =>
          state.game match {
            case Some(_) =>
              State.Confirm(ConfirmAction.Quit, State.Shutdown, state, ConfirmFooterMessage.Empty)
            case None => State.Shutdown
          }
      }
      .orElse(UIO.succeed(state.copy(footerMessage = MenuFooterMessage.InvalidCommand)))

  def render(state: State.Menu): UIO[String] =
    for {
      header  <- menuView.header
      content <- menuView.content(state.game.nonEmpty)
      footer  <- menuView.footer(state.footerMessage)
    } yield List(header, content, footer).mkString("\n\n")
}
object MenuModeLive {
  val layer: URLayer[Has[MenuCommandParser] with Has[MenuView], Has[MenuMode]] = (MenuModeLive(_, _)).toLayer
}
