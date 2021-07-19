package com.example.tictactoe.controller

import com.example.tictactoe.domain.{ AppError, IllegalStateError, State }
import com.example.tictactoe.mode.confirm.ConfirmMode
import com.example.tictactoe.mode.game.GameMode
import com.example.tictactoe.mode.menu.MenuMode
import zio._

final case class ControllerLive(confirmMode: ConfirmMode, gameMode: GameMode, menuMode: MenuMode) extends Controller {
  def process(input: String, state: State): IO[AppError, State] =
    state match {
      case s: State.Confirm => confirmMode.process(input, s)
      case s: State.Game    => gameMode.process(input, s)
      case s: State.Menu    => menuMode.process(input, s)
      case State.Shutdown   => ZIO.fail(IllegalStateError)
    }

  def render(state: State): UIO[String] =
    state match {
      case s: State.Confirm => confirmMode.render(s)
      case s: State.Game    => gameMode.render(s)
      case s: State.Menu    => menuMode.render(s)
      case State.Shutdown   => UIO.succeed("Shutting down...")
    }
}
object ControllerLive {
  val layer: URLayer[Has[ConfirmMode] with Has[GameMode] with Has[MenuMode], Has[Controller]] =
    (ControllerLive(_, _, _)).toLayer
}
