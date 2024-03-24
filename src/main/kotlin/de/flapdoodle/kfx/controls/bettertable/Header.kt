package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.bindings.Values
import de.flapdoodle.kfx.bindings.defaultIfNull
import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.bindings.valueOf
import de.flapdoodle.kfx.controls.bettertable.events.TableEvent
import de.flapdoodle.kfx.controls.bettertable.events.TableRequestEventListener
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.StackLikeRegion
import de.flapdoodle.kfx.layout.splitpane.SplitPane
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections

class Header<T : Any>(
  internal val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  private val eventListener: TableRequestEventListener<T>,
  private val headerColumnFactory: HeaderColumnFactory<T>
) : StackLikeRegion() {

  private val headerColumns = SimpleObjectProperty<List<HeaderColumn<T>>>(emptyList())
  private val splitPane = SplitPane(headerColumns) { node ->
    eventListener.fireEvent(TableEvent.RequestResizeColumn((node).column))
  }
  private val columnWidthMap = FXCollections.observableHashMap<Column<T, out Any>, ReadOnlyDoubleProperty>()

  init {
    cssClassName("header")
    isFocusTraversable = false
    columns.addListener { observable, oldValue, newValue ->
      require(newValue.toSet().size == newValue.size) { "column added more than once" }
    }

    children.add(splitPane)
    headerColumns.syncWith(columns) {
      headerColumnFactory.headerColumn(it)
    }
    columnWidthMap.syncWith(headerColumns, { it.column }) { it.widthProperty() }
  }

  internal fun columnWidthProperty(column: Column<T, out Any>): ObservableValue<Number> {
    return columnWidthMap.valueOf(column)
      .defaultIfNull(Values.constant(1.0))
  }

  fun setColumnSize(column: Column<T, out Any>, columnSize: ColumnSize) {
    headerColumns.value.forEach {
      val c = it as HeaderColumn<T>
      if (c.column == column) {
        splitPane.setSize(c, columnSize.min, columnSize.preferred)
      }
    }
  }
}