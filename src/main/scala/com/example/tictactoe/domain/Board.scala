package com.example.tictactoe.domain

import zio._

object Board {

  sealed abstract class Field(val value: Int)
  object Field {
    final case object NorthWest extends Field(1)
    final case object North     extends Field(2)
    final case object NorthEast extends Field(3)
    final case object West      extends Field(4)
    final case object Center    extends Field(5)
    final case object East      extends Field(6)
    final case object SouthWest extends Field(7)
    final case object South     extends Field(8)
    final case object SouthEast extends Field(9)

    def make(value: Int): Option[Field] = value match {
      case 1 => Some(NorthWest)
      case 2 => Some(North)
      case 3 => Some(NorthEast)
      case 4 => Some(West)
      case 5 => Some(Center)
      case 6 => Some(East)
      case 7 => Some(SouthWest)
      case 8 => Some(South)
      case 9 => Some(SouthEast)
      case _ => None
    }

    val all: List[Field] = List(
      NorthWest,
      North,
      NorthEast,
      West,
      Center,
      East,
      SouthWest,
      South,
      SouthEast
    )
  }

  val wins: UIO[Set[Set[Field]]] = {
    val horizontalWins = Set(
      Set(1, 2, 3),
      Set(4, 5, 6),
      Set(7, 8, 9)
    )

    val verticalWins = Set(
      Set(1, 4, 7),
      Set(2, 5, 8),
      Set(3, 6, 9)
    )

    val diagonalWins = Set(
      Set(1, 5, 9),
      Set(3, 5, 7)
    )

    ZIO
      .foreach(horizontalWins ++ verticalWins ++ diagonalWins)(
        ZIO.foreach(_)(value => ZIO.from(Field.make(value))).map(_.toSet)
      )
      .map(_.toSet)
      .orDieWith(_ => new IllegalStateException)
  }
}
