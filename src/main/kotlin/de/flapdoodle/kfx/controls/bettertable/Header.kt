package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.bindings.Values
import de.flapdoodle.kfx.bindings.defaultIfNull
import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.bindings.valueOf
import de.flapdoodle.kfx.controls.bettertable.events.TableEvent
import de.flapdoodle.kfx.controls.bettertable.events.TableRequestEventListener
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.splitpane.BetterSplitPane
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.scene.control.Control
import javafx.scene.control.SkinBase

class Header<T : Any>(
  internal val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  private val eventListener: TableRequestEventListener<T>,
  private val headerColumnFactory: HeaderColumnFactory<T>
) : Control() {

  private val skin = Skin(this)

  init {
    cssClassName("header")
    isFocusTraversable = false
    columns.addListener { observable, oldValue, newValue ->
      require(newValue.toSet().size == newValue.size) { "column added more than once" }
    }
  }

  override fun createDefaultSkin() = skin

  fun columnWidthProperty(column: Column<T, out Any>): ObservableValue<Number> {
    return skin.columnWidthProperty(column)
  }

  fun setColumnSize(column: Column<T, out Any>, columnSize: Double) {
    skin.setColumnSize(column, columnSize)
  }

  inner class Skin<T : Any>(
    private val src: Header<T>
  ) : SkinBase<Header<T>>(src) {
    private val splitPane = BetterSplitPane() { node ->
      if (node is HeaderColumn<out Any>) {
        src.eventListener.fireEvent(TableEvent.RequestResizeColumn((node as HeaderColumn<T>).column))
      }
    }
    private val headerColumns = FXCollections.observableArrayList<HeaderColumn<T>>()
    private val columnWidthMap = FXCollections.observableHashMap<Column<T, out Any>, ReadOnlyDoubleProperty>()

    init {
      children.add(splitPane)
      headerColumns.syncWith(src.columns) {
        src.headerColumnFactory.headerColumn(it)
      }
      splitPane.nodes().syncWith(headerColumns) { it }
      columnWidthMap.syncWith(headerColumns, { it.column }) { it.widthProperty() }
    }

    internal fun columnWidthProperty(column: Column<T, out Any>): ObservableValue<Number> {
      return columnWidthMap.valueOf(column)
        .defaultIfNull(Values.constant(1.0))
    }

    fun setColumnSize(column: Column<T, out Any>, columnSize: Double) {
      headerColumns.forEach {
        val c = it as HeaderColumn<T>
        if (c.column==column) {
          splitPane.setPaneSize(c, columnSize)
        }
      }
    }
  }
}