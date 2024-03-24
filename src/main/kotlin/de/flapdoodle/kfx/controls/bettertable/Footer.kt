package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.StackLikeRegion
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.layout.HBox

class Footer<T : Any>(
  private val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  private val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>,
  private val footerColumnFactory: FooterColumnFactory<T>?
) : StackLikeRegion() {

  private val footer = HBox()

  init {
    isFocusTraversable = false
    cssClassName("footer")

    ObservableLists.syncWith(columns, footer.children) {
      val footerColumn = footerColumnFactory?.footerColumn(it) ?: FooterColumn(it)
      footerColumn.prefWidthProperty().bind(columnWidthProperties(it))
      footerColumn
    }
    children.add(footer)
  }
}