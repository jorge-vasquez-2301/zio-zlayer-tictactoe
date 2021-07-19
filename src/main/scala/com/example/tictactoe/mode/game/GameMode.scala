package com.example.tictactoe.mode.game

import com.example.tictactoe.domain.State
import zio.{ Has, UIO, URIO, ZIO }

trait GameMode {
  def process(input: String, state: State.Game): UIO[State]
  def render(state: State.Game): UIO[String]
}
object GameMode {
  def process(input: String, state: State.Game): URIO[Has[GameMode], State] =
    ZIO.serviceWith[GameMode](_.process(input, state))

  def render(state: State.Game): URIO[Has[GameMode], String] = ZIO.serviceWith[GameMode](_.render(state))
}
