package de.flapdoodle.kfx.controls.bettertable

import javafx.scene.text.TextAlignment
import javafx.util.StringConverter

open class Column<T: Any, C: Any>(
  open val label: String,
  open val property: ColumnProperty<T, C>,
  open val editable: Boolean,
  open val textAlignment: TextAlignment = TextAlignment.LEFT
)