package de.flapdoodle.kfx.controls.table

import de.flapdoodle.kfx.extensions.bindCss
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.ScrollPane
import javafx.scene.control.SkinBase
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

class SlimTable<T: Any>(
  internal val rows: ObservableList<T>,
  internal val columns: ObservableList<out Column<T, out Any>>,
  internal val changeListener: CellChangeListener<T>
) : Control() {
  init {
    isFocusTraversable = false
    bindCss("slim-table")
  }

  private val skin = Skin(this)

  override fun createDefaultSkin() = skin
  fun columns() = columns

  inner class Skin<T : Any>(
    private val control: SlimTable<T>
  ) : SkinBase<SlimTable<T>>(control) {

    private val header = SlimHeader(control.columns)
    private val rowsPane = SlimRows(control.rows, control.columns, header::columnWidthProperty, control.changeListener).apply {
      VBox.setVgrow(this, Priority.ALWAYS)
    }
    private val footer = SlimFooter(control.columns, header::columnWidthProperty)

    private val scroll = ScrollPane().apply {
      hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
      content = rowsPane
    }

    private val all = VBox().apply {
      children.add(header)
      children.add(scroll)
      children.add(footer)
    }

    init {
      children.add(all)
    }
  }

}