package com.example.tictactoe.terminal

import zio._
import zio.test.Assertion._
import zio.test._
import zio.test.mock.Expectation._
import zio.test.mock._

object TerminalSpec extends DefaultRunnableSpec {
  def spec = suite("Terminal")(
    test("getUserInput delegates to Console") {
      checkM(Gen.anyString) { input =>
        val consoleMock: ULayer[Has[Console]] = MockConsole.ReadLine(value(input))
        val result                            = Terminal.getUserInput.inject(consoleMock, TerminalLive.layer)
        assertM(result)(equalTo(input))
      }
    },
    test("display delegates to Console") {
      checkM(Gen.anyString) { frame =>
        val consoleMock: ULayer[Has[Console]] =
          MockConsole.Print(equalTo(TerminalLive.ansiClearScreen), unit) ++ MockConsole.PrintLine(equalTo(frame), unit)
        val result = Terminal.display(frame).inject(consoleMock, TerminalLive.layer)
        assertM(result)(isUnit)
      }
    }
  )
}
