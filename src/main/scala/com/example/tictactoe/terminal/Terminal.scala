package com.example.tictactoe.terminal

import zio.{ Has, UIO, URIO, ZIO }

trait Terminal {
  def getUserInput: UIO[String]
  def display(frame: String): UIO[Unit]
}
object Terminal {
  val getUserInput: URIO[Has[Terminal], String]         = ZIO.serviceWith[Terminal](_.getUserInput)
  def display(frame: String): URIO[Has[Terminal], Unit] = ZIO.serviceWith[Terminal](_.display(frame))
}
