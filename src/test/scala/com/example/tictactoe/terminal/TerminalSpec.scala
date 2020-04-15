package com.example.tictactoe.terminal

import zio._
import zio.console._
import zio.test._
import zio.test.Assertion._
import zio.test.mock.Expectation._
import zio.test.mock._

object TerminalSpec extends DefaultRunnableSpec {
  def spec = suite("Terminal")(
    testM("getUserInput delegates to Console") {
      checkM(Gen.anyString) { input =>
        val consoleMock: ULayer[Console] = MockConsole.GetStrLn returns value(input)
        val env: ULayer[Terminal]        = consoleMock >>> Terminal.live
        val result                       = Terminal.getUserInput.provideLayer(env)
        assertM(result)(equalTo(input))
      }
    },
    testM("display delegates to Console") {
      checkM(Gen.anyString) { frame =>
        val consoleMock: ULayer[Console] =
          (MockConsole.PutStr(equalTo(Terminal.ansiClearScreen)) returns unit) ++
            (MockConsole.PutStrLn(equalTo(frame)) returns unit)
        val env: ULayer[Terminal] = consoleMock >>> Terminal.live
        val result                = Terminal.display(frame).provideLayer(env)
        assertM(result)(isUnit)
      }
    }
  )
}
