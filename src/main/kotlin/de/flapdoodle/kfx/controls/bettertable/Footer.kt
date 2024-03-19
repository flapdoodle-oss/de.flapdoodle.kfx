package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.extensions.cssClassName
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.layout.HBox

class Footer<T : Any>(
  private val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  private val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>,
  private val footerColumnFactory: FooterColumnFactory<T>?
) : Control() {

  private val skin = Skin(this)

  init {
    isFocusTraversable = false
    cssClassName("footer")
  }

  override fun createDefaultSkin() = skin

  inner class Skin<T : Any>(
    private val src: Footer<T>
  ) : SkinBase<Footer<T>>(src) {
    private val footer = HBox()
    init {
      ObservableLists.syncWith(src.columns, footer.children) {
        val footerColumn = src.footerColumnFactory?.footerColumn(it) ?: FooterColumn(it)
        footerColumn.prefWidthProperty().bind(src.columnWidthProperties(it))
        footerColumn
      }
      children.add(footer)
    }
  }
}