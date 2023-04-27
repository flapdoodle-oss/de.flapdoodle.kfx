package de.flapdoodle.kfx.controls.table

import de.flapdoodle.kfx.Registration
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.splitpane.BetterSplitPane
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.layout.StackPane

class SlimHeader<T : Any>(
  private val columns: ObservableList<out Column<T, out Any>>
) : Control() {

  private val skin = SlimHeaderSkin(this)

  init {
    isFocusTraversable = false
    cssClassName("slim-header")
  }

  internal fun columnsChanged() {
    skin.columnsChanged()
  }

  override fun createDefaultSkin() = skin

  fun addColumnWidthChangeListener(listener: (Map<Column<T, out Any>, ReadOnlyDoubleProperty>) -> Unit): Registration {
    return skin.addColumnWidthChangeListener(listener)
  }

  class SlimHeaderSkin<T : Any>(
    private val src: SlimHeader<T>
  ) : SkinBase<SlimHeader<T>>(src) {
    private val header = BetterSplitPane()
    private val columnWidthMap = SimpleObjectProperty<Map<Column<T, out Any>, ReadOnlyDoubleProperty>>()

    internal fun columnsChanged() {
      val columns = src.columns.map { HeaderColumn(it) }
      header.nodes().setAll(columns)
      columnWidthMap.value = columns.map { it.column to it.widthProperty() }.toMap()
    }

    init {
      children.add(header)
    }

    internal fun addColumnWidthChangeListener(listener: (Map<Column<T, out Any>, ReadOnlyDoubleProperty>) -> Unit): Registration {
      val delegate = ChangeListener { _, _, map ->
        listener(map)
      }
      columnWidthMap.addListener(delegate)
      return Registration {
        columnWidthMap.removeListener(delegate)
      }
    }
  }

  class HeaderColumn<T: Any>(
    internal val column: Column<T, out Any>
  ) : StackPane() {
    init {
      children.add(column.header())
      cssClassName("slim-header-column")
    }
  }

}