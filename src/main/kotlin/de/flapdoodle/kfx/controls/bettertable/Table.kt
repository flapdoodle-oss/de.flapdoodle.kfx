package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.controls.bettertable.events.*
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Region

class Table<T: Any>(
  internal val rows: ReadOnlyObjectProperty<List<T>>,
  internal val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  internal val changeListener: CellChangeListener<T>,
  stateFactory: (EventContext<T>) -> State<T> = { DefaultState(it) }
) : Region() {

//  private var delayedAction: () -> Unit = {}
//  private val delayedActionTrigger = DelayedAction(Duration.millis(700.0)) {
//    delayedAction.invoke()
//  }
//  private val delayAction = DelayAction(Duration.millis(700.0))

  private val eventContext = EventContext(rows,columns,changeListener) {
    onTableEvent(it)
  }
  private val eventListener = StateEventListener(stateFactory(eventContext))

//  private val eventListener = TableRequestEventListener<T> { event ->
//    when (event) {
//      is TableEvent.CommitChange<T, out Any> -> {
//        changeListener.onChange(event.row, event.asCellChange())
//        onTableEvent(event.stopEvent())
//        onTableEvent(TableEvent.Focus(event.row, event.column))
//      }
//      is TableEvent.NextCell<T, out Any> -> {
//        val nextEvent = event.asFocusEvent(rows.value, columns.value)
//        if (nextEvent!=null) {
//          onTableEvent(nextEvent)
//        }
//      }
//      is TableEvent.RequestEdit<T, out Any> -> {
//        onTableEvent(TableEvent.StartEdit(event.row, event.column))
//      }
//      is TableEvent.RequestFocus<T, out Any> -> {
//        onTableEvent(TableEvent.Focus(event.row, event.column))
//      }
//      is TableEvent.RequestInsertRow<T> -> {
//        delayAction.call { onTableEvent(event.ok()) }
//      }
//      is TableEvent.AbortInsertRow<T> -> {
//        delayAction.stop()
//        onTableEvent(event.ok())
//      }
//      else -> {
//        throw IllegalArgumentException("not implemented: $event")
//      }
//    }
//  }

  private val header = Header(columns, eventListener)
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
//    println("event: $event")
    when (event) {
      is TableEvent.ResizeColumn<T, out Any> -> {
        val columnSize = _rows.preferredColumnSize(event.column)
        header.setColumnSize(event.column, columnSize)
      }
      else -> {
        _rows.onTableEvent(event)
      }
    }
  }

  override fun layoutChildren() {
    val contentWidth = width - insets.left - insets.right
    val contentHeight = height - insets.top - insets.bottom
    layoutInArea(scroll, insets.left, insets.top, contentWidth, contentHeight, baselineOffset, HPos.CENTER, VPos.CENTER)
  }
}