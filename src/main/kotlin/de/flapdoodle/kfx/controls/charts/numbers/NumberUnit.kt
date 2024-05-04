package de.flapdoodle.kfx.controls.charts.numbers

interface NumberUnit<T: Number> {
  fun unitsBetween(min: T, max: T): Int
  fun firstUnit(value: T): T
  fun next(value: T, offset: Int): T
}