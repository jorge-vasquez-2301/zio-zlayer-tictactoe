package com.example.tictactoe.terminal

import zio.test._

object TerminalSpec extends ZIOSpecDefault {
  def spec =
    suite("Terminal")(
      test("getUserInput delegates to Console") {
        check(Gen.string) { input =>
          for {
            _      <- TestConsole.feedLines(input)
            result <- Terminal.getUserInput
          } yield assertTrue(result == input)
        }
      },
      test("display delegates to Console") {
        check(Gen.string) { frame =>
          for {
            result <- Terminal.display(frame)
          } yield assertTrue(result == ())
        }
      }
    ).provideLayer(TerminalLive.layer) @@ TestAspect.silent
}
