package de.flapdoodle.kfx.controls.table

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.extensions.cssClassName
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane

class SlimFooter<T : Any>(
  private val columns: ObservableList<out Column<T, out Any>>,
  private val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>
) : Control() {

  private val skin = Skin(this)

  init {
    isFocusTraversable = false
    cssClassName("slim-footer")
  }

  override fun createDefaultSkin() = skin

  inner class Skin<T : Any>(
    private val src: SlimFooter<T>
  ) : SkinBase<SlimFooter<T>>(src) {
    private val footer = HBox()
    init {
      ObservableLists.syncWith(src.columns, footer.children) {
        FooterColumn(it).apply {
          prefWidthProperty().bind(src.columnWidthProperties(it))
        }
      }
      children.add(footer)
    }
  }

  inner class FooterColumn<T: Any>(
    internal val column: Column<T, out Any>
  ) : StackPane() {

    init {
      isFocusTraversable = true
      cssClassName("slim-header-column")

      if (column.footer!=null) {
        children.add(column.footer.invoke())
      }
    }
  }
}