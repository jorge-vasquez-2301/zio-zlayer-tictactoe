package com.example.tictactoe.mode.confirm

import com.example.tictactoe.domain.State
import zio.{ Has, UIO, URIO, ZIO }

trait ConfirmMode {
  def process(input: String, state: State.Confirm): UIO[State]
  def render(state: State.Confirm): UIO[String]
}
object ConfirmMode {
  def process(input: String, state: State.Confirm): URIO[Has[ConfirmMode], State] =
    ZIO.serviceWith[ConfirmMode](_.process(input, state))

  def render(state: State.Confirm): URIO[Has[ConfirmMode], String] = ZIO.serviceWith[ConfirmMode](_.render(state))
}
