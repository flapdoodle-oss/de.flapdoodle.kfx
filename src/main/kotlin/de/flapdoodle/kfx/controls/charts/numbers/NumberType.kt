package de.flapdoodle.kfx.controls.charts.numbers

interface NumberType<T: Number> {
  fun units(): List<NumberUnit<T>>
}