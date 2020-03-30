package com.example.tictactoe

import com.example.tictactoe.controller.Controller
import com.example.tictactoe.domain.State
import com.example.tictactoe.terminal.Terminal
import zio._

package object runLoop {
  type RunLoop = Has[RunLoop.Service]
  object RunLoop {
    trait Service {
      def step(state: State): IO[Unit, State]
    }
    object Service {
      val live: ZLayer[Controller with Terminal, Nothing, RunLoop] = ZLayer.fromFunction { env =>
        val controllerService = env.get[Controller.Service]
        val terminalService   = env.get[Terminal.Service]
        new Service {
          override def step(state: State): IO[Unit, State] =
            for {
              frame     <- controllerService.render(state)
              _         <- terminalService.display(frame)
              input     <- if (state == State.Shutdown) UIO.succeed("") else terminalService.getUserInput
              nextState <- controllerService.process(input, state)
            } yield nextState
        }
      }
    }

    def step(state: State): ZIO[RunLoop, Unit, State] = ZIO.accessM(_.get.step(state))
  }
}
