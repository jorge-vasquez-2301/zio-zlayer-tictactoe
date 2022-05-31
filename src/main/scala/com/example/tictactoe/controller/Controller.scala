package com.example.tictactoe.controller

import com.example.tictactoe.domain.State
import zio._
import zio.macros._

@accessible
trait Controller {
  def process(input: String, state: State): UIO[Option[State]]
  def render(state: State): UIO[String]
}
