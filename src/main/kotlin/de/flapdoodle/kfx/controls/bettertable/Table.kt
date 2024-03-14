package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.transitions.DelayedAction
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.util.Duration

class Table<T: Any>(
  internal val rows: ReadOnlyObjectProperty<List<T>>,
  internal val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  internal val changeListener: CellChangeListener<T>
) : Region() {

  private var delayedAction: () -> Unit = {}
  private val delayedActionTrigger = DelayedAction(Duration.millis(700.0)) {
    delayedAction.invoke()
  }

  private val eventListener = TableEventListener<T> {
    when (it) {
      is TableEvent.CommitChange<T, out Any> -> {
        changeListener.onChange(it.row, it.asCellChange())
        onTableEvent(it.stopEvent())
        onTableEvent(TableEvent.Focus(it.row, it.column))
      }
      is TableEvent.NextCell<T, out Any> -> {
        val nextEvent = it.asFocusEvent(rows.value, columns.value)
        if (nextEvent!=null) {
          onTableEvent(nextEvent)
        }
      }
      is TableEvent.RequestEdit<T, out Any> -> {
        onTableEvent(TableEvent.StartEdit(it.row, it.column))
      }
      is TableEvent.RequestFocus<T, out Any> -> {
        onTableEvent(TableEvent.Focus(it.row, it.column))
      }
      is TableEvent.RequestInsertRow<T> -> {
        delayedActionTrigger.playFromStart()
        delayedAction = {
          onTableEvent(it.ok())
        }
      }
      is TableEvent.AbortInsertRow<T> -> {
        onTableEvent(it.ok())
      }
      else -> {
        throw IllegalArgumentException("not implemented: $it")
      }
    }
  }

  private val header = Header(columns)
  private val footer = Footer(columns, header::columnWidthProperty)
  private val _rows = Rows(rows, columns, eventListener, header::columnWidthProperty)

  private val scroll = ScrollPane().apply {
    hbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
    vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
    isFocusTraversable = false
    isPannable = true
    isFitToHeight = true
  }

  private val rowsWrapper = ScrollPane().apply {
    cssClassName("rows-scroll-pane")

//    val button = Button("hi").apply {
//      minHeight = 100.0
//      maxHeight = Double.MAX_VALUE
//      maxWidth = Double.MAX_VALUE
//    }

    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
    isFocusTraversable = false
    isPannable = true
    isFitToHeight = true
    content = _rows
  }

  init {
    bindCss("better-table")
    val content = WeightGridPane().apply {
      setRowWeight(0, 0.0)
      setRowWeight(1, 1.0)
      setRowWeight(2, 0.001)
      setRowWeight(3, 0.0)
    }

    WeightGridPane.setPosition(header, 0,0, HPos.CENTER)
    WeightGridPane.setPosition(rowsWrapper,0,1, HPos.CENTER)
    WeightGridPane.setPosition(footer, 0,3, HPos.CENTER)
    content.children.add(header)
    content.children.add(rowsWrapper)
    content.children.add(footer)

    scroll.content = content

    children.add(scroll)
  }

  internal fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
    println("event: $event")
    _rows.onTableEvent(event)
  }

  override fun layoutChildren() {
    val contentWidth = width - insets.left - insets.right
    val contentHeight = height - insets.top - insets.bottom
    layoutInArea(scroll, insets.left, insets.top, contentWidth, contentHeight, baselineOffset, HPos.CENTER, VPos.CENTER)
  }
}