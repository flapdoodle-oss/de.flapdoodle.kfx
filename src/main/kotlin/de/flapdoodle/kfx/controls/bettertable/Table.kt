package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Region

class Table<T: Any>(
  internal val rows: ReadOnlyObjectProperty<List<T>>,
  internal val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  internal val changeListener: CellChangeListener<T>
) : Region() {

  private val header = Header(columns)
  private val footer = Footer(columns, header::columnWidthProperty)
  private val _rows = Rows(rows, columns, header::columnWidthProperty, changeListener)

  private val scroll = ScrollPane().apply {
    hbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
    vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
    isPannable = true
    isFitToHeight = true
  }

  private val rowsWrapper = ScrollPane().apply {
//    val button = Button("hi").apply {
//      minHeight = 100.0
//      maxHeight = Double.MAX_VALUE
//      maxWidth = Double.MAX_VALUE
//    }

    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
    isPannable = true
    content = _rows
  }

  init {
    bindCss("better-table")
    val content = WeightGridPane().apply {
      setRowWeight(0, 0.0)
      setRowWeight(1, 1.0)
      setRowWeight(2, 0.0)
    }

    WeightGridPane.setPosition(header, 0,0, HPos.CENTER)
    WeightGridPane.setPosition(rowsWrapper,0,1, HPos.CENTER)
    WeightGridPane.setPosition(footer, 0,2, HPos.CENTER)
    content.children.add(header)
    content.children.add(rowsWrapper)
    content.children.add(footer)

    scroll.content = content

    children.add(scroll)
  }

  override fun layoutChildren() {
    val contentWidth = width - insets.left - insets.right
    val contentHeight = height - insets.top - insets.bottom
    layoutInArea(scroll, insets.left, insets.top, contentWidth, contentHeight, baselineOffset, HPos.CENTER, VPos.CENTER)
  }
}