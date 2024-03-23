package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.controls.bettertable.events.TableEvent
import de.flapdoodle.kfx.controls.bettertable.events.TableRequestEventListener
import de.flapdoodle.kfx.extensions.*
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.SkinBase
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.AnchorPane

class Cell<T : Any, C : Any>(
  val column: Column<T, C>,
  val row: T,
  val value: C?
) : Control() {

  private val skin = Skin(this)

  init {
    isFocusTraversable = true
    cssClassName("cell")
    Styles.Readonly.set(this, !column.editable)
  }

  fun setEventListener(eventListener: TableRequestEventListener<T>) {
    skin.setEventListener(eventListener)
  }

  fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
    skin.onTableEvent(event)
  }

  override fun createDefaultSkin() = skin

  fun columnSize() = skin.columnSize()

  inner class Skin<T : Any, C : Any>(
    private val control: Cell<T, C>
  ) : SkinBase<Cell<T, C>>(control) {

    private lateinit var eventListener: TableRequestEventListener<T>

    private val label = Label().apply {
      isWrapText = false
//      prefWidth = Double.MAX_VALUE
      alignment = Cells.asPosition(control.column.textAlignment)
      text = control.column.converter.toString(control.value)
    }

    private val field = Cells.createTextField(value = control.value,
      converter = control.column.converter,
      commitEdit = { it: C? ->
        eventListener.fireEvent(TableEvent.CommitChange(control.row, control.column, it))
      },
      cancelEdit = {
        eventListener.fireEvent(TableEvent.AbortChange(control.row, control.column))
      }
    ).apply {
      isVisible = false
      isEditable = true
      focusedProperty().addListener { _, old, focused ->
        if (isVisible) {
          if (!focused) {
            eventListener.fireEvent(TableEvent.EditLostFocus(control.row, control.column))
          }
        }
      }
    }

    val wrapper = AnchorPane().apply {
      cssClassName("background")
    }

//    var hasFocusAfterInitFired = false

    init {
      wrapper.children.add(field.withAnchors(all = 0.0))
      wrapper.children.add(label.withAnchors(all = 0.0))
      children.add(wrapper)

      // TODO notwendig?
      consumeMouseEvents(false)

      focusedProperty().addListener { _, old, focused ->
        if (!old && focused) {
          // got focus from somewhere
//          if (!hasFocusAfterInitFired) {
            eventListener.fireEvent(TableEvent.HasFocus(control.row, control.column))
//            hasFocusAfterInitFired = true
//          }
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
      control.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_RELEASED) {
        if (it.clickCount == 1) {
          eventListener.fireEvent(TableEvent.RequestFocus(control.row, control.column))
        }
        if (it.clickCount == 2) {
          if (column.editable) {
            eventListener.fireEvent(TableEvent.RequestEdit(control.row, control.column))
          }
        }
        it.consume()
      }

      control.addEventFilter(KeyEvent.KEY_RELEASED) {
        if (!it.isShortcutDown && it.code == KeyCode.TAB) {
          it.consume()
        }
      }
      control.addEventHandler(KeyEvent.KEY_RELEASED) {
        if (!it.isShortcutDown) {
          val direction = when (it.code) {
            KeyCode.LEFT -> TableEvent.Direction.LEFT
            KeyCode.RIGHT -> TableEvent.Direction.RIGHT
            KeyCode.UP -> TableEvent.Direction.UP
            KeyCode.DOWN -> TableEvent.Direction.DOWN
            KeyCode.TAB -> TableEvent.Direction.NEXT
            else -> null
          }
          if (direction != null) {
            it.consume()
            if (field.isVisible) {
              // just w
            } else {
              eventListener.fireEvent(TableEvent.NextCell(control.row, control.column, direction))
            }
          } else {
            if (it.code == KeyCode.ENTER) {
              it.consume()
              if (column.editable) {
                eventListener.fireEvent(TableEvent.RequestEdit(control.row, control.column))
              }
            }
            if (it.code == KeyCode.DELETE) {
              it.consume()
              eventListener.fireEvent(TableEvent.DeleteRow(control.row))
            }
          }
        }
      }

    }

    fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
      when (event) {
        is TableEvent.Focus<T, out Any> -> {
          if (event.row == control.row && event.column == column) {
            control.requestFocus()
          }
        }

        is TableEvent.Blur<T, out Any> -> {
          if (event.row == control.row && event.column == column) {
            control.blur()
          }
        }

        is TableEvent.StartEdit<T, out Any> -> {
          if (event.row == control.row && event.column == column) {
            _startEdit()
          }
        }

        is TableEvent.StopEdit<T, out Any> -> {
          if (event.row == control.row && event.column == column) {
            _cancelEdit()
          }
        }

        else -> {
          println("ignore: $event")
        }
      }
    }

    fun columnSize(): ColumnSize {
      control.applyCss()

      val labelWidth = label.prefWidth(height)
      val fieldWidth = if (field.isVisible) field.prefWidth(height) else field.minWidth
      val width = java.lang.Double.max(labelWidth, fieldWidth) + insets.left + insets.right

      val minLabelWidth = label.minWidth(height)
      val minFieldWidth = if (field.isVisible) field.minWidth(height) else field.minWidth
      val minWidth = java.lang.Double.max(minLabelWidth, minFieldWidth) + insets.left + insets.right

      return ColumnSize(minWidth, width)
    }
  }
}