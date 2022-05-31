package com.example.tictactoe.mode.menu

import com.example.tictactoe.domain.State
import zio._
import zio.macros._

@accessible
trait MenuMode {
  def process(input: String, state: State.Menu): UIO[State]
  def render(state: State.Menu): UIO[String]
}
