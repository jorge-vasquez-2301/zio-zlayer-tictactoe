package com.example.tictactoe.mode.game

import com.example.tictactoe.domain.State
import zio._
import zio.macros._

@accessible
trait GameMode {
  def process(input: String, state: State.Game): UIO[State]
  def render(state: State.Game): UIO[String]
}
