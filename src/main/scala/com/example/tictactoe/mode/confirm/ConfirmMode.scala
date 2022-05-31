package com.example.tictactoe.mode.confirm

import com.example.tictactoe.domain.State
import zio._
import zio.macros._

@accessible
trait ConfirmMode {
  def process(input: String, state: State.Confirm): UIO[State]
  def render(state: State.Confirm): UIO[String]
}
