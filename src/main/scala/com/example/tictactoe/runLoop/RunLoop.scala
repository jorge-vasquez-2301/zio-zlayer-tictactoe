package com.example.tictactoe.runLoop

import com.example.tictactoe.domain.State
import zio._

import zio.macros._

@accessible
trait RunLoop {
  def step(state: State): UIO[Option[State]]
}
