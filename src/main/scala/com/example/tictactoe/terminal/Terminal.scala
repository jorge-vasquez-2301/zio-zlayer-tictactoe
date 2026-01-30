package com.example.tictactoe.terminal

import zio._

trait Terminal {
  def getUserInput: UIO[String]
  def display(frame: String): UIO[Unit]
}

object Terminal {
  def getUserInput: ZIO[Terminal, Nothing, String] =
    ZIO.serviceWithZIO[Terminal](_.getUserInput)

  def display(frame: String): ZIO[Terminal, Nothing, Unit] =
    ZIO.serviceWithZIO[Terminal](_.display(frame))
}
