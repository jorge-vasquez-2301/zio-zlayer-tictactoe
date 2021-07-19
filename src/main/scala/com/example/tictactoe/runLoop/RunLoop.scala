package com.example.tictactoe.runLoop

import com.example.tictactoe.domain.{ AppError, State }
import zio.{ Has, IO, ZIO }

trait RunLoop {
  def step(state: State): IO[AppError, State]
}
object RunLoop {
  def step(state: State): ZIO[Has[RunLoop], AppError, State] = ZIO.serviceWith[RunLoop](_.step(state))
}
