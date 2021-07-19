package com.example.tictactoe.runLoop

import com.example.tictactoe.controller.Controller
import com.example.tictactoe.domain.{ AppError, State }
import com.example.tictactoe.terminal.Terminal
import zio._

final case class RunLoopLive(controller: Controller, terminal: Terminal) extends RunLoop {
  override def step(state: State): IO[AppError, State] =
    for {
      _         <- controller.render(state).flatMap(terminal.display)
      input     <- if (state == State.Shutdown) UIO.succeed("") else terminal.getUserInput
      nextState <- controller.process(input, state)
    } yield nextState
}
object RunLoopLive {
  val layer: URLayer[Has[Controller] with Has[Terminal], Has[RunLoop]] = (RunLoopLive(_, _)).toLayer
}
