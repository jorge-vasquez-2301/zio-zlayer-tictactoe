package com.example.tictactoe.controller

import com.example.tictactoe.domain.{ AppError, State }
import zio.{ Has, IO, UIO, URIO, ZIO }

trait Controller {
  def process(input: String, state: State): IO[AppError, State]
  def render(state: State): UIO[String]
}
object Controller {
  def process(input: String, state: State): ZIO[Has[Controller], AppError, State] =
    ZIO.serviceWith[Controller](_.process(input, state))

  def render(state: State): URIO[Has[Controller], String] = ZIO.serviceWith[Controller](_.render(state))
}
