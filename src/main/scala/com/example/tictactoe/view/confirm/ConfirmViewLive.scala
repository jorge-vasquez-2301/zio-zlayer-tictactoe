package com.example.tictactoe.view.confirm
import com.example.tictactoe.domain.{ ConfirmAction, ConfirmFooterMessage }
import zio._

final case class ConfirmViewLive() extends ConfirmView {
  def header(action: ConfirmAction): UIO[String] = UIO.succeed(action).map {
    case ConfirmAction.NewGame =>
      """[New game]
        |
        |This will discard current game progress.""".stripMargin
    case ConfirmAction.Quit =>
      """[Quit]
        |
        |This will discard current game progress.""".stripMargin
  }

  val content: UIO[String] =
    UIO.succeed(
      """Are you sure?
        |<yes> / <no>""".stripMargin
    )

  def footer(message: ConfirmFooterMessage): UIO[String] =
    UIO.succeed(message) map {
      case ConfirmFooterMessage.Empty          => ""
      case ConfirmFooterMessage.InvalidCommand => "Invalid command. Try again."
    }
}
object ConfirmViewLive {
  val layer: ULayer[Has[ConfirmView]] = (ConfirmViewLive.apply _).toLayer
}
