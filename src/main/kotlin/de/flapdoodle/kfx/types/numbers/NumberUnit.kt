package de.flapdoodle.kfx.types.numbers

interface NumberUnit<T: Number> {
  fun unitsBetween(min: T, max: T): Int
  fun firstUnit(value: T): T
  fun next(value: T, offset: Int): T
}