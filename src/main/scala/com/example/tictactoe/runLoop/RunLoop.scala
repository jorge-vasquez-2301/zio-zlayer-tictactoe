package com.example.tictactoe.runLoop

import com.example.tictactoe.domain.State
import zio._

trait RunLoop {
  def step(state: State): UIO[Option[State]]
}

object RunLoop {
  def step(state: State): ZIO[RunLoop, Nothing, Option[State]] =
    ZIO.serviceWithZIO[RunLoop](_.step(state))
}
