package de.flapdoodle.kfx.controls.bettertable

data class ColumnSize(val min: Double, val preferred: Double) {
  init {
    require(min<=preferred) {"invalid arguments: $min > $preferred"}
  }
}