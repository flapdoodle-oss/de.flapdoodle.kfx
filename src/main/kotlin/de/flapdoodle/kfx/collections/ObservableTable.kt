package de.flapdoodle.kfx.collections

import javafx.collections.FXCollections
import javafx.collections.ObservableMap


class ObservableTable {
  val cells = FXCollections.observableHashMap<Cell<Any>, Any>()

  
  data class Column<T>(val name: String, val type: Class<T>)
  data class Cell<T>(val row: Int, val column: Column<T>)
}