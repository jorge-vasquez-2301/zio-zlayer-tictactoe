package com.example.tictactoe.terminal

import zio._
import zio.console._
import zio.magic._
import zio.test.Assertion._
import zio.test._
import zio.test.mock.Expectation._
import zio.test.mock._

object TerminalSpec extends DefaultRunnableSpec {
  def spec = suite("Terminal")(
    testM("getUserInput delegates to Console") {
      checkM(Gen.anyString) { input =>
        val consoleMock: ULayer[Has[Console.Service]] = MockConsole.GetStrLn(value(input))
        val result                                    = Terminal.getUserInput.inject(consoleMock, TerminalLive.layer)
        assertM(result)(equalTo(input))
      }
    },
    testM("display delegates to Console") {
      checkM(Gen.anyString) { frame =>
        val consoleMock: ULayer[Has[Console.Service]] =
          MockConsole.PutStr(equalTo(TerminalLive.ansiClearScreen), unit) ++ MockConsole.PutStrLn(equalTo(frame), unit)
        val result = Terminal.display(frame).inject(consoleMock, TerminalLive.layer)
        assertM(result)(isUnit)
      }
    }
  )
}
