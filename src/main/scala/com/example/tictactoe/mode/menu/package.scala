package com.example.tictactoe.mode

import com.example.tictactoe.domain._
import com.example.tictactoe.parser.menu.MenuCommandParser
import com.example.tictactoe.view.menu.MenuView
import zio._

package object menu {
  type MenuMode = Has[MenuMode.Service]
  object MenuMode {
    trait Service {
      def process(input: String, state: State.Menu): UIO[State]
      def render(state: State.Menu): UIO[String]
    }
    object Service {
      val live: ZLayer[MenuCommandParser with MenuView, Nothing, MenuMode] = ZLayer.fromFunction { env =>
        val menuCommandParserService = env.get[MenuCommandParser.Service]
        val menuViewService          = env.get[MenuView.Service]
        new Service {
          override def process(input: String, state: State.Menu): UIO[State] =
            menuCommandParserService
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
                    case Some(_) => State.Confirm(ConfirmAction.Quit, State.Shutdown, state, ConfirmFooterMessage.Empty)
                    case None    => State.Shutdown
                  }
              }
              .orElse(UIO.succeed(state.copy(footerMessage = MenuFooterMessage.InvalidCommand)))

          override def render(state: State.Menu): UIO[String] =
            for {
              header  <- menuViewService.header
              content <- menuViewService.content(state.game.nonEmpty)
              footer  <- menuViewService.footer(state.footerMessage)
            } yield List(header, content, footer).mkString("\n\n")
        }
      }
    }

    // accessors
    def process(input: String, state: State.Menu): URIO[MenuMode, State] =
      ZIO.accessM(_.get.process(input, state))

    def render(state: State.Menu): URIO[MenuMode, String] = ZIO.accessM(_.get.render(state))
  }
}
