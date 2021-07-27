package com.example.tictactoe.terminal

import zio._

final case class TerminalLive(console: Console) extends Terminal {
  import TerminalLive._

  override val getUserInput: UIO[String]         = console.readLine.orDie
  override def display(frame: String): UIO[Unit] = (console.print(ansiClearScreen) *> console.printLine(frame)).orDie
}
object TerminalLive {
  final val ansiClearScreen = "\u001b[H\u001b[2J"

  val layer: URLayer[Has[Console], Has[Terminal]] = (TerminalLive(_)).toLayer
}
