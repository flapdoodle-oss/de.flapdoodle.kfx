package de.flapdoodle.kfx.controls.table

import de.flapdoodle.kfx.bindings.Values
import de.flapdoodle.kfx.bindings.defaultIfNull
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.bindings.valueOf
import de.flapdoodle.kfx.layout.splitpane.BetterSplitPane
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.layout.StackPane

class SlimHeader<T : Any>(
  private val columns: ObservableList<out Column<T, out Any>>
) : Control() {

  private val skin = Skin(this)

  init {
    isFocusTraversable = false
    cssClassName("slim-header")
  }

  override fun createDefaultSkin() = skin

  fun columnWidthProperty(column: Column<T, out Any>): ObservableValue<Number> {
    return skin.columnWidthProperty(column)
  }

  inner class Skin<T : Any>(
    private val src: SlimHeader<T>
  ) : SkinBase<SlimHeader<T>>(src) {
    private val header = BetterSplitPane()
    private val headerColumns = FXCollections.observableArrayList<HeaderColumn<T>>()
    private val columnWidthMap = FXCollections.observableHashMap<Column<T, out Any>, ReadOnlyDoubleProperty>()

    init {
      children.add(header)
      headerColumns.syncWith(src.columns) {
        HeaderColumn(it)
      }
      header.nodes().syncWith(headerColumns) { it }
      columnWidthMap.syncWith(headerColumns, { it.column }) { it.widthProperty() }
    }

    internal fun columnWidthProperty(column: Column<T, out Any>): ObservableValue<Number> {
      return columnWidthMap.valueOf(column)
        .defaultIfNull(Values.constant(1.0))
    }
  }

  inner class HeaderColumn<T: Any>(
    internal val column: Column<T, out Any>
  ) : StackPane() {
    init {
      isFocusTraversable = true
      
      children.add(column.header())
      cssClassName("slim-header-column")
    }
  }

}