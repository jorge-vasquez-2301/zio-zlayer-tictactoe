package com.example.tictactoe.mode.confirm

import com.example.tictactoe.domain.State
import zio._

trait ConfirmMode {
  def process(input: String, state: State.Confirm): UIO[State]
  def render(state: State.Confirm): UIO[String]
}

object ConfirmMode {
  def process(input: String, state: State.Confirm): ZIO[ConfirmMode, Nothing, State] =
    ZIO.serviceWithZIO[ConfirmMode](_.process(input, state))

  def render(state: State.Confirm): ZIO[ConfirmMode, Nothing, String] =
    ZIO.serviceWithZIO[ConfirmMode](_.render(state))
}
