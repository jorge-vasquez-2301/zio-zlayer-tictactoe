package com.example.tictactoe

import com.example.tictactoe.domain.{ AppError, IllegalStateError, State }
import com.example.tictactoe.mode.confirm.ConfirmMode
import com.example.tictactoe.mode.game.GameMode
import com.example.tictactoe.mode.menu.MenuMode
import zio._

package object controller {
  type Controller = Has[Controller.Service]
  object Controller {
    trait Service {
      def process(input: String, state: State): IO[AppError, State]
      def render(state: State): UIO[String]
    }
    val live: URLayer[ConfirmMode with GameMode with MenuMode, Controller] =
      ZLayer.fromServices[ConfirmMode.Service, GameMode.Service, MenuMode.Service, Controller.Service] {
        (confirmModeService, gameModeService, menuModeService) =>
          new Service {
            override def process(input: String, state: State): IO[AppError, State] =
              state match {
                case s: State.Confirm => confirmModeService.process(input, s)
                case s: State.Game    => gameModeService.process(input, s)
                case s: State.Menu    => menuModeService.process(input, s)
                case State.Shutdown   => ZIO.fail(IllegalStateError)
              }

            override def render(state: State): UIO[String] =
              state match {
                case s: State.Confirm => confirmModeService.render(s)
                case s: State.Game    => gameModeService.render(s)
                case s: State.Menu    => menuModeService.render(s)
                case State.Shutdown   => UIO.succeed("Shutting down...")
              }
          }
      }

    // accessors
    def process(input: String, state: State): ZIO[Controller, AppError, State] =
      ZIO.accessM(_.get.process(input, state))

    def render(state: State): URIO[Controller, String] = ZIO.accessM(_.get.render(state))
  }
}
