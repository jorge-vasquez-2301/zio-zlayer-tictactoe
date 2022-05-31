package com.example.tictactoe.parser.menu

import com.example.tictactoe.domain.{ AppError, MenuCommand }
import zio._
import zio.macros._

@accessible
trait MenuCommandParser {
  def parse(input: String): IO[AppError, MenuCommand]
}
