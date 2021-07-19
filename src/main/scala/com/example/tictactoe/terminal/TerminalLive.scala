package com.example.tictactoe.terminal

import zio._
import zio.console._

final case class TerminalLive(console: Console.Service) extends Terminal {
  import TerminalLive._

  override val getUserInput: UIO[String]         = console.getStrLn.orDie
  override def display(frame: String): UIO[Unit] = (console.putStr(ansiClearScreen) *> console.putStrLn(frame)).orDie
}
object TerminalLive {
  final val ansiClearScreen = "\u001b[H\u001b[2J"

  val layer: URLayer[Has[Console.Service], Has[Terminal]] = (TerminalLive(_)).toLayer
}
