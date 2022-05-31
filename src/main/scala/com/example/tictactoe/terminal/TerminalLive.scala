package com.example.tictactoe.terminal

import zio._

final case class TerminalLive() extends Terminal {
  import TerminalLive._

  override val getUserInput: UIO[String]         = Console.readLine.orDie
  override def display(frame: String): UIO[Unit] = (Console.print(ansiClearScreen) *> Console.printLine(frame)).orDie
}
object TerminalLive {
  final val ansiClearScreen = "\u001b[H\u001b[2J"

  val layer: ULayer[Terminal] = ZLayer.succeed(TerminalLive())
}
