package com.example.tictactoe.view.confirm
import com.example.tictactoe.config.AppConfig
import com.example.tictactoe.domain.{ ConfirmAction, ConfirmFooterMessage }
import zio._

final case class ConfirmViewLive() extends ConfirmView {
  def header(action: ConfirmAction): UIO[String] = ZIO.succeed(action).map {
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
    ZIO.succeed(
      """Are you sure?
        |<yes> / <no>""".stripMargin
    )

  def footer(message: ConfirmFooterMessage): UIO[String] =
    ZIO
      .config(AppConfig.config.map(_.view.confirm))
      .map { config =>
        message match {
          case ConfirmFooterMessage.Empty          => ""
          case ConfirmFooterMessage.InvalidCommand => config.invalidCommandMessage
        }
      }
      .orDie
}
object ConfirmViewLive {
  val layer: ULayer[ConfirmView] = ZLayer.succeed(ConfirmViewLive())
}
