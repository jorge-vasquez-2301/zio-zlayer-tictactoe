package com.example.tictactoe.controller

import com.example.tictactoe.domain.State
import zio._

trait Controller {
  def process(input: String, state: State): UIO[Option[State]]
  def render(state: State): UIO[String]
}

object Controller {
  def process(input: String, state: State): ZIO[Controller, Nothing, Option[State]] =
    ZIO.serviceWithZIO[Controller](_.process(input, state))

  def render(state: State): ZIO[Controller, Nothing, String] =
    ZIO.serviceWithZIO[Controller](_.render(state))
}
