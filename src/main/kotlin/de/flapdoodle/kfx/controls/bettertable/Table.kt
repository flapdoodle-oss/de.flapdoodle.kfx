package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.collections.Diff
import de.flapdoodle.kfx.controls.bettertable.events.*
import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.layout.StackLikeRegion
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Region

class Table<T: Any>(
  internal val rows: ObservableValue<List<T>>,
  internal val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  internal val changeListener: TableChangeListener<T>,
  headerColumnFactory: HeaderColumnFactory<T> = HeaderColumnFactory.Default(),
  cellFactory: CellFactory<T> = CellFactory.Default(),
  footerColumnFactory: FooterColumnFactory<T> = FooterColumnFactory.Default(),
  stateFactory: (EventContext<T>) -> State<T> = { DefaultState(it) }
) : StackLikeRegion() {

  private val eventContext = EventContext(rows,columns,changeListener) {
    onTableEvent(it)
  }
  private val eventListener = StateEventListener(stateFactory(eventContext))

  private val header = Header(columns, eventListener, headerColumnFactory)
  private val _rows = Rows(rows, columns, cellFactory, eventListener, header::columnWidthProperty)
  private val footer = Footer(columns, header::columnWidthProperty, footerColumnFactory)

  private val scroll = ScrollPane().apply {
    hbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
    vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
    isFocusTraversable = false
    isPannable = true
    isFitToHeight = true
  }

  private val rowsWrapper = ScrollPane().apply {
    cssClassName("rows-scroll-pane")

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

    // TODO nicht ganz klar, warum das bei _rows funktioniert, aber nicht oberhalb
    _rows.onAttach {
      columns.value.forEach {
        eventListener.fireEvent(TableEvent.RequestResizeColumn(it))
      }
    }.onDetach {

    }

//    onBindToParent {
//      columns.addChangeListener { _, oldValue, newValue ->
//        Diff.between(oldValue, newValue).added.forEach {
//          eventListener.fireEvent(TableEvent.RequestResizeColumn(it))
//        }
//      }
//    }

    columns.addListener { _, oldValue, newValue ->
      Diff.between(oldValue, newValue).added.forEach {
        eventListener.fireEvent(TableEvent.RequestResizeColumn(it))
      }
    }

    rows.addListener { observable, oldValue, newValue ->
      if (newValue.isEmpty()) {
        eventListener.fireEvent(TableEvent.EmptyRows())
      }
    }
  }

  internal fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
//    println("table event: $event")
    when (event) {
      is TableEvent.ResizeColumn<T, out Any> -> {
        val columnSize = _rows.columnSize(event.column)
//        val columnSize = _rows.preferredColumnSize(event.column)
        header.setColumnSize(event.column, columnSize)
      }
      else -> {
        _rows.onTableEvent(event)
      }
    }
  }

//  override fun layoutChildren() {
//    val contentWidth = width - insets.left - insets.right
//    val contentHeight = height - insets.top - insets.bottom
//    layoutInArea(scroll, insets.left, insets.top, contentWidth, contentHeight, baselineOffset, HPos.CENTER, VPos.CENTER)
//  }
}