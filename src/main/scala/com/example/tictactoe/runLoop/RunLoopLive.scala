package com.example.tictactoe.runLoop

import com.example.tictactoe.controller.Controller
import com.example.tictactoe.domain.{ AppError, State }
import com.example.tictactoe.terminal.Terminal
import zio._

final case class RunLoopLive(controller: Controller, terminal: Terminal) extends RunLoop {
  override def step(state: State): UIO[Option[State]] =
    for {
      _         <- controller.render(state).flatMap(terminal.display)
      input     <- if (state == State.Shutdown) ZIO.succeed("") else terminal.getUserInput
      nextState <- controller.process(input, state)
    } yield nextState
}
object RunLoopLive {
  val layer: URLayer[Controller with Terminal, RunLoop] = ZLayer.fromFunction(RunLoopLive(_, _))
}
