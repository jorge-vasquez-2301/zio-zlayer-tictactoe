package com.example.tictactoe.mode.game

import com.example.tictactoe.domain.State
import zio._

trait GameMode {
  def process(input: String, state: State.Game): UIO[State]
  def render(state: State.Game): UIO[String]
}

object GameMode {
  def process(input: String, state: State.Game): ZIO[GameMode, Nothing, State] =
    ZIO.serviceWithZIO[GameMode](_.process(input, state))

  def render(state: State.Game): ZIO[GameMode, Nothing, String] =
    ZIO.serviceWithZIO[GameMode](_.render(state))
}
