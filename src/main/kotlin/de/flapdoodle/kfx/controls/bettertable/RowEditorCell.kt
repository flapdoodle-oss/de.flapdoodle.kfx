package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.controls.bettertable.events.TableEvent
import de.flapdoodle.kfx.controls.bettertable.events.TableRequestEventListener
import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.layout.StackLikeRegion
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.AnchorPane

class RowEditorCell<T : Any, C : Any>(
  val column: Column<T, C>,
  var row: T,
  value: C?
) : StackLikeRegion() {

  private lateinit var eventListener: TableRequestEventListener<T>

  private val label = Label().apply {
    isFocusTraversable = true

    isWrapText = false
//      prefWidth = Double.MAX_VALUE
    alignment = Cells.asPosition(column.textAlignment)
    text = column.converter.toString(value)
    isVisible = !column.editable
  }

  private val field = Cells.createTextField(value = value,
    converter = column.converter,
    commitEdit = { it: C? ->
      eventListener.fireEvent(TableEvent.CommitChange(row, column, it))
    },
    cancelEdit = {
      eventListener.fireEvent(TableEvent.AbortChange(row, column))
    }
  ).apply {
    isVisible = column.editable
//    isEditable = true
  }

  val wrapper = AnchorPane().apply {
    cssClassName("background")
  }


  init {
    isFocusTraversable = !column.editable

    cssClassName("cell")
    Styles.Readonly.set(this, !column.editable)

    wrapper.children.add(field.withAnchors(all = 0.0))
    wrapper.children.add(label.withAnchors(all = 0.0))
    children.add(wrapper)

    focusedProperty().addListener { _, old, focused ->
      if (!focused) {
        eventListener.fireEvent(TableEvent.LostFocus(row, column))
      }
    }
    
    field.focusedProperty().addListener { _, old, focused ->
      if (isVisible) {
        if (!focused) {
          eventListener.fireEvent(TableEvent.LostFocus(row, column))
        } else {
          eventListener.fireEvent(TableEvent.HasFocus(row, column))
        }
      }
    }
  }

  private fun _cancelEdit() {
    if (column.editable) {
      label.show()
      field.hide()
      field.text = label.text
    }
  }

  private fun _startEdit() {
    if (column.editable) {
      label.hide()
      field.show()
      field.requestFocus()
    }
  }

  fun setEventListener(eventListener: TableRequestEventListener<T>) {
    this.eventListener = eventListener
    label.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_RELEASED) {
      it.consume()
      if (it.clickCount == 1) {
        eventListener.fireEvent(TableEvent.RequestFocus(row, column))
      }
//      if (it.clickCount == 2) {
//        if (column.editable) {
//          eventListener.fireEvent(TableEvent.RequestEdit(row, column))
//        }
//      }
    }

//    addEventFilter(KeyEvent.KEY_PRESSED) {
//      if (!it.isShortcutDown && it.code == KeyCode.TAB) {
//        it.consume()
//      }
//    }
//    addEventFilter(KeyEvent.KEY_RELEASED) {
//      if (!it.isShortcutDown && it.code == KeyCode.TAB) {
//        it.consume()
//        eventListener.fireEvent(TableEvent.NextCell(row, column, if (it.isShiftDown) TableEvent.Direction.PREV else TableEvent.Direction.NEXT))
//      }
//    }
//    field.addEventFilter(KeyEvent.KEY_RELEASED) {
//      if (!it.isShortcutDown && it.code == KeyCode.TAB) {
//        it.consume()
//        println("----------------> field tab released")
//        eventListener.fireEvent(TableEvent.NextCell(row, column, TableEvent.Direction.NEXT))
//      }
//    }
    field.addEventFilter(KeyEvent.ANY) {
      if (!it.isShortcutDown && it.code == KeyCode.TAB) {
        it.consume()
        if (it.eventType == KeyEvent.KEY_RELEASED) {
          eventListener.fireEvent(TableEvent.UpdateChange(row, column, column.converter.fromString(field.text)))
          eventListener.fireEvent(TableEvent.NextCell(row, column, if (it.isShiftDown) TableEvent.Direction.PREV else TableEvent.Direction.NEXT))
        }
      }
    }
    
    label.addEventHandler(KeyEvent.KEY_RELEASED) {
      if (!it.isShortcutDown) {
        val direction = when (it.code) {
          KeyCode.LEFT -> TableEvent.Direction.LEFT
          KeyCode.RIGHT -> TableEvent.Direction.RIGHT
          KeyCode.UP -> TableEvent.Direction.UP
          KeyCode.DOWN -> TableEvent.Direction.DOWN
          KeyCode.TAB -> if (it.isShiftDown) TableEvent.Direction.PREV else TableEvent.Direction.NEXT
          else -> null
        }
        if (direction != null) {
          it.consume()
          eventListener.fireEvent(TableEvent.NextCell(row, column, direction))
        }
      }
    }

  }

  fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
    when (event) {
      is TableEvent.UpdateInsertRow<T> -> {
        row = event.row
        val value = column.converter.toString(column.property(row))
        label.text = value
        field.text = value
      }

      is TableEvent.Focus<T, out Any> -> {
        if (event.row == row && event.column == column) {
          if (column.editable) {
            field.requestFocus()
          } else {
            requestFocus()
          }
        }
      }

      is TableEvent.Blur<T, out Any> -> {
        if (event.row == row && event.column == column) {
          blur()
        }
      }

//      is TableEvent.StartEdit<T, out Any> -> {
//        if (event.row == row && event.column == column) {
//          _startEdit()
//        }
//      }
//
//      is TableEvent.StopEdit<T, out Any> -> {
//        if (event.row == row && event.column == column) {
//          _cancelEdit()
//        }
//      }

      else -> {
        println("ignore: $event")
      }
    }
  }

  fun columnSize(): ColumnSize {
    applyCss()

    val labelWidth = label.prefWidth(height)
    val fieldWidth = if (field.isVisible) field.prefWidth(height) else field.minWidth
    val width = java.lang.Double.max(labelWidth, fieldWidth) + insets.left + insets.right

    val minLabelWidth = label.minWidth(height)
    val minFieldWidth = if (field.isVisible) field.minWidth(height) else field.minWidth
    val minWidth = java.lang.Double.max(minLabelWidth, minFieldWidth) + insets.left + insets.right

    return ColumnSize(minWidth, width)
  }
}