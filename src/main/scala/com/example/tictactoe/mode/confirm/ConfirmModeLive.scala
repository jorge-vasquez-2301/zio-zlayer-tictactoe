package com.example.tictactoe.mode.confirm

import com.example.tictactoe.domain.{ ConfirmCommand, ConfirmFooterMessage, State }
import com.example.tictactoe.parser.confirm.ConfirmCommandParser
import com.example.tictactoe.view.confirm.ConfirmView
import zio._

final case class ConfirmModeLive(confirmCommandParser: ConfirmCommandParser, confirmView: ConfirmView)
    extends ConfirmMode {
  def process(input: String, state: State.Confirm): UIO[State] =
    confirmCommandParser
      .parse(input)
      .map {
        case ConfirmCommand.Yes => state.confirmed
        case ConfirmCommand.No  => state.declined
      }
      .orElse(ZIO.succeed(state.copy(footerMessage = ConfirmFooterMessage.InvalidCommand)))
  def render(state: State.Confirm): UIO[String] =
    for {
      header  <- confirmView.header(state.action)
      content <- confirmView.content
      footer  <- confirmView.footer(state.footerMessage)
    } yield List(header, content, footer).mkString("\n\n")
}
object ConfirmModeLive {
  val layer: URLayer[Has[ConfirmCommandParser] with Has[ConfirmView], Has[ConfirmMode]] =
    (ConfirmModeLive(_, _)).toLayer
}
