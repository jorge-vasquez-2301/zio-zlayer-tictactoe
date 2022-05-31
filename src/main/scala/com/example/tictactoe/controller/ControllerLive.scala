package com.example.tictactoe.controller

import com.example.tictactoe.domain.State
import com.example.tictactoe.mode.confirm.ConfirmMode
import com.example.tictactoe.mode.game.GameMode
import com.example.tictactoe.mode.menu.MenuMode
import zio._

final case class ControllerLive(confirmMode: ConfirmMode, gameMode: GameMode, menuMode: MenuMode) extends Controller {
  def process(input: String, state: State): UIO[Option[State]] =
    state match {
      case s: State.Confirm => confirmMode.process(input, s).asSome
      case s: State.Game    => gameMode.process(input, s).asSome
      case s: State.Menu    => menuMode.process(input, s).asSome
      case State.Shutdown   => ZIO.none
    }

  def render(state: State): UIO[String] =
    state match {
      case s: State.Confirm => confirmMode.render(s)
      case s: State.Game    => gameMode.render(s)
      case s: State.Menu    => menuMode.render(s)
      case State.Shutdown   => ZIO.succeed("Shutting down...")
    }
}
object ControllerLive {
  val layer: URLayer[ConfirmMode with GameMode with MenuMode, Controller] =
    ZLayer.fromFunction(ControllerLive(_, _, _))
}
