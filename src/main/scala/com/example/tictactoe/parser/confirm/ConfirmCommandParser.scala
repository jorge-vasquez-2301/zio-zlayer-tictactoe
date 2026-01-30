package com.example.tictactoe.parser.confirm

import com.example.tictactoe.domain.{ AppError, ConfirmCommand }
import zio._

trait ConfirmCommandParser {
  def parse(input: String): IO[AppError, ConfirmCommand]
}

object ConfirmCommandParser {
  def parse(input: String): ZIO[ConfirmCommandParser, AppError, ConfirmCommand] =
    ZIO.serviceWithZIO[ConfirmCommandParser](_.parse(input))
}
