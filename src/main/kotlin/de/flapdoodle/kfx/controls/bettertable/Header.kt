package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.bindings.*
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.splitpane.BetterSplitPane
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.layout.StackPane

class Header<T : Any>(
  internal val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
): Control() {

  private val skin = Skin(this)

  init {
    cssClassName("header")

    isFocusTraversable = false
  }

  override fun createDefaultSkin() = skin

  fun columnWidthProperty(column: Column<T, out Any>): ObservableValue<Number> {
    return skin.columnWidthProperty(column)
  }

  inner class Skin<T : Any>(
    private val src: Header<T>
  ) : SkinBase<Header<T>>(src) {
    private val splitPane = BetterSplitPane()
    private val headerColumns = FXCollections.observableArrayList<HeaderColumn<T>>()
    private val columnWidthMap = FXCollections.observableHashMap<Column<T, out Any>, ReadOnlyDoubleProperty>()

    init {
      children.add(splitPane)
      headerColumns.syncWith(src.columns) {
        HeaderColumn(it)
      }
      splitPane.nodes().syncWith(headerColumns) { it }
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
      cssClassName("header-column")
    }
  }

}