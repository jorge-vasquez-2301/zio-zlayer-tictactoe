package com.example.tictactoe

import com.example.tictactoe.controller.Controller
import com.example.tictactoe.domain.{ AppError, State }
import com.example.tictactoe.terminal.Terminal
import zio._
import zio.macros.accessible

package object runLoop {
  type RunLoop = Has[RunLoop.Service]

  @accessible
  object RunLoop {
    trait Service {
      def step(state: State): IO[AppError, State]
    }
    val live: URLayer[Controller with Terminal, RunLoop] =
      ZLayer.fromServices[Controller.Service, Terminal.Service, RunLoop.Service] {
        (controllerService, terminalService) =>
          new Service {
            override def step(state: State): IO[AppError, State] =
              for {
                _         <- controllerService.render(state).flatMap(terminalService.display)
                input     <- if (state == State.Shutdown) UIO.succeed("") else terminalService.getUserInput
                nextState <- controllerService.process(input, state)
              } yield nextState
          }
      }
  }
}
