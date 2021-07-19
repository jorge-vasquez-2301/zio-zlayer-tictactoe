package com.example.tictactoe.mode.menu

import com.example.tictactoe.domain.State
import zio.{ Has, UIO, URIO, ZIO }

trait MenuMode {
  def process(input: String, state: State.Menu): UIO[State]
  def render(state: State.Menu): UIO[String]
}
object MenuMode {
  def process(input: String, state: State.Menu): URIO[Has[MenuMode], State] =
    ZIO.serviceWith[MenuMode](_.process(input, state))

  def render(state: State.Menu): URIO[Has[MenuMode], String] = ZIO.serviceWith[MenuMode](_.render(state))
}
