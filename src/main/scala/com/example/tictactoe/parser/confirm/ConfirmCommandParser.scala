package com.example.tictactoe.parser.confirm

import com.example.tictactoe.domain.{ AppError, ConfirmCommand }
import zio._
import zio.macros._

@accessible
trait ConfirmCommandParser {
  def parse(input: String): IO[AppError, ConfirmCommand]
}
