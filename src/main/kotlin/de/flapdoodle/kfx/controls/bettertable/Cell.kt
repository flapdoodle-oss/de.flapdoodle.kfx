/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.controls.bettertable.events.TableEvent
import de.flapdoodle.kfx.controls.bettertable.events.TableRequestEventListener
import de.flapdoodle.kfx.controls.fields.DefaultFieldFactoryLookup
import de.flapdoodle.kfx.controls.fields.FieldFactoryLookup
import de.flapdoodle.kfx.controls.labels.ValidatedLabel
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.layout.StackLikeRegion
import de.flapdoodle.kfx.logging.Logging
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane

class Cell<T : Any, C : Any>(
  val column: Column<T, C>,
  val row: T,
  val value: C?,
  val eventListener: TableRequestEventListener<T>,
  fieldFactoryLookup: FieldFactoryLookup = DefaultFieldFactoryLookup()
) : StackLikeRegion() {

  private val logger = Logging.logger(Cell::class)

  private val label = ValidatedLabel(column.property.converter).apply {
    isWrapText = false
    alignment = Cells.asPosition(column.textAlignment)
    set(value)
  }
//  private val label = Label().apply {
//    isWrapText = false
////      prefWidth = Double.MAX_VALUE
//    alignment = Cells.asPosition(column.textAlignment)
//    text = column.property.converter.toString(value)
//  }

  private val field = fieldFactoryLookup.fieldFactory(column.property.type)
    .inputFor(value = value,
      commitEdit = { it: C?, error: String? ->
        eventListener.fireEvent(TableEvent.CommitChange(row, column, it, error))
      },
      cancelEdit = {
        eventListener.fireEvent(TableEvent.AbortChange(row, column))
      }
    ).apply {
      isVisible = false
      isManaged = column.editable
//      isEditable = true
      control.focusedProperty().addListener { _, old, focused ->
        if (isVisible) {
          if (!focused) {
            eventListener.fireEvent(TableEvent.LostFocus(row, column))
          }
        }
      }
    }

  val wrapper = AnchorPane().apply {
    cssClassName("background")
  }


  init {
    isFocusTraversable = true
    cssClassName("cell")
    Styles.Readonly.set(this, !column.editable)

    wrapper.children.add(label.withAnchors(all = 0.0))
    wrapper.children.add(field.withAnchors(all = 0.0))
    children.add(wrapper)

    // TODO notwendig?
//    consumeMouseEvents(false)

    focusedProperty().addListener { _, old, focused ->
      if (!old && focused) {
        // got focus from somewhere
//          if (!hasFocusAfterInitFired) {
        eventListener.fireEvent(TableEvent.HasFocus(row, column))
//            hasFocusAfterInitFired = true
//          }
      }
    }

    addEventHandler(MouseEvent.ANY) {
      if (it.eventType==MouseEvent.MOUSE_PRESSED && it.clickCount == 1) {
        it.consume()
        eventListener.fireEvent(TableEvent.RequestFocus(row, column))
      }
      if (it.eventType==MouseEvent.MOUSE_RELEASED && it.clickCount == 2) {
        it.consume()
        if (column.editable) {
          eventListener.fireEvent(TableEvent.RequestEdit(row, column))
        }
      }
    }

//    addEventFilter(KeyEvent.KEY_RELEASED) {
//      if (!it.isShortcutDown && it.code == KeyCode.TAB) {
//        it.consume()
//      }
//    }
    field.addEventFilter(KeyEvent.ANY) {
      if (!it.isShortcutDown && it.code == KeyCode.TAB) {
        it.consume()
        if (it.eventType == KeyEvent.KEY_RELEASED) {
          eventListener.fireEvent(TableEvent.NextCell(row, column, if (it.isShiftDown) TableEvent.Direction.PREV else TableEvent.Direction.NEXT))
        }
      }
    }

    addEventFilter(KeyEvent.ANY) {
      if (!it.isShortcutDown && it.code == KeyCode.TAB) {
        it.consume()
        if (it.eventType == KeyEvent.KEY_RELEASED) {
          eventListener.fireEvent(TableEvent.NextCell(row, column, if (it.isShiftDown) TableEvent.Direction.PREV else TableEvent.Direction.NEXT))
        }
      }
    }

    addEventHandler(KeyEvent.KEY_RELEASED) {
      // with datepicker events for datepicker apears here, don't know why
      if (!it.isShortcutDown && it.target == this) {
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
          if (field.isVisible) {
            // just w
          } else {
            eventListener.fireEvent(TableEvent.NextCell(row, column, direction))
          }
        } else {
          if (it.code == KeyCode.ENTER) {
            it.consume()
            if (column.editable) {
              eventListener.fireEvent(TableEvent.RequestEdit(row, column))
            }
          }
          if (it.code == KeyCode.DELETE) {
            it.consume()
            eventListener.fireEvent(TableEvent.DeleteRow(row))
          }
          if (it.code == KeyCode.INSERT) {
            it.consume()
            eventListener.fireEvent(TableEvent.RequestInsertRow(row, if (it.isShiftDown) TableEvent.InsertPosition.ABOVE else TableEvent.InsertPosition.BELOW))
          }
        }
      }
    }
  }

  private fun _cancelEdit() {
    if (column.editable) {
      field.value = value
      field.isVisible = false // hide()
//      label.show()
//      field.text = label.text
    }
  }

  private fun _startEdit() {
    if (column.editable) {
//      label.hide()
      field.isVisible = true // show()
      field.control.requestFocus()
    }
  }

//  fun setEventListener(eventListener: TableRequestEventListener<T>) {
//    this.eventListener = eventListener
//
//  }
//
  fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
    when (event) {
      is TableEvent.Focus<T, out Any> -> {
        if (event.row == row && event.column == column) {
          requestFocus()
        }
      }

//      is TableEvent.Blur<T, out Any> -> {
//        if (event.row == row && event.column == column) {
//          blur()
//        }
//      }

      is TableEvent.StartEdit<T, out Any> -> {
        if (event.row == row && event.column == column) {
          _startEdit()
        }
      }

      is TableEvent.StopEdit<T, out Any> -> {
        if (event.row == row && event.column == column) {
          _cancelEdit()
        }
      }

      else -> {
        logger.info { "$this: ignore: $event" }
      }
    }
  }

  fun columnSize(): ColumnSize {
    applyCss()

    val labelWidth = label.prefWidth(height)
    val fieldWidth = if (field.isVisible) field.prefWidth(height) else field.minWidth
    val width = java.lang.Double.max(labelWidth, fieldWidth) + insets.left + insets.right

    val minLabelWidth = label.minWidth(height)
    val minFieldWidth = if (field.isVisible || true) field.minWidth(height) else field.minWidth
    val minWidth = java.lang.Double.max(minLabelWidth, minFieldWidth) + insets.left + insets.right

    return ColumnSize(minWidth, java.lang.Double.max(width, minWidth))
  }
}