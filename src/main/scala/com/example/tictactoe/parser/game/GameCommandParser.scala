package com.example.tictactoe.parser.game

import com.example.tictactoe.domain.{ AppError, GameCommand }
import zio._
import zio.macros._

@accessible
trait GameCommandParser {
  def parse(input: String): IO[AppError, GameCommand]
}
