package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.controls.bettertable.events.TableEvent
import de.flapdoodle.kfx.controls.bettertable.events.TableRequestEventListener
import de.flapdoodle.kfx.extensions.*
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.SkinBase
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.text.TextAlignment
import javafx.util.StringConverter

class Cell<T: Any, C: Any>(
  val column: Column<T, C>,
  val row: T,
  val value: C?
): Control() {

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

  fun preferredColumnSize(): Double {
    return skin.preferredColumnSize()
  }

  inner class Skin<T : Any, C : Any>(
    private val control: Cell<T, C>
  ) : SkinBase<Cell<T, C>>(control) {

    private lateinit var column: Column<T, C>
    private lateinit var eventListener: TableRequestEventListener<T>

    private val label = Label().apply {
      isWrapText = false
//      prefWidth = Double.MAX_VALUE
      alignment = asPosition(control.column.textAlignment)
      text = control.column.converter.toString(control.value)
    }

    private fun asPosition(textAlignment: TextAlignment): Pos {
      return when (textAlignment) {
        TextAlignment.RIGHT -> Pos.CENTER_RIGHT
        TextAlignment.LEFT -> Pos.CENTER_LEFT
        TextAlignment.CENTER -> Pos.CENTER
        TextAlignment.JUSTIFY -> Pos.CENTER
      }
    }

    private val field = createTextField(
      value = control.value,
      converter = control.column.converter,
      commitEdit = {
//        control.value = it
//        label.text = control.converter.toString(it)
//        control.fireEvent(SmartEvents.EditDone(control))
//        control.onChange(it)
//        _editDone()
        eventListener.fireEvent(TableEvent.CommitChange(control.row, column, it))
      },
      cancelEdit = {
        eventListener.fireEvent(TableEvent.AbortChange(control.row, column))
//        this._cancelEdit()
      }
    ).apply {
      isVisible = false
      isEditable = true
      focusedProperty().addListener { _, old, focused ->
        if (!focused) {
          if (isVisible) {
            eventListener.fireEvent(TableEvent.EditLostFocus(control.row, column))
          }
        }
      }
    }

//    internal fun _editDone() {
//      _cancelEdit()
//    }

//    var editInProgress: Boolean = false

    internal fun _cancelEdit() {
//      editInProgress=false
      if (column.editable) {
        label.show()
        field.hide()
        field.text = label.text
      }
    }

    internal fun _startEdit() {
//      RuntimeException("startEdit called").printStackTrace()
//      editInProgress=true
      if (column.editable) {
        label.hide()
        field.show()
        field.requestFocus()
      }
    }

    val wrapper = AnchorPane().apply {
      cssClassName("background")
    }

    init {
      wrapper.children.add(field.withAnchors(all = 0.0))
      wrapper.children.add(label.withAnchors(all = 0.0))
      children.add(wrapper)

      consumeMouseEvents(false)
//      onAttach { println("attach $column") }.onDetach {
//
//      }
    }

    fun setColumn(column: Column<T, C>) {
      this.column = column
    }

    fun setEventListener(eventListener: TableRequestEventListener<T>) {
      this.eventListener = eventListener
      control.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_RELEASED) {
        if (it.clickCount == 1) {
          eventListener.fireEvent(TableEvent.RequestFocus(control.row, column))
        }
        if (it.clickCount == 2) {
          if (column.editable) {
            eventListener.fireEvent(TableEvent.RequestEdit(control.row, column))
          }
        }
        it.consume()
      }

      control.addEventFilter(KeyEvent.KEY_RELEASED) {
        if (!it.isShortcutDown && it.code==KeyCode.TAB) {
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
          if (direction!=null) {
            it.consume()
            if (field.isVisible) {
              // just w
            } else {
              eventListener.fireEvent(TableEvent.NextCell(control.row, column, direction))
            }
          } else {
            if (it.code == KeyCode.ENTER) {
              it.consume()
              eventListener.fireEvent(TableEvent.RequestEdit(control.row, column))
            }
          }
        }
      }

//      control.handleEvent(KeyEvent.KEY_RELEASED) {
//        matching { !it.isShortcutDown } then {
//          consume { it.code == KeyCode.LEFT } by {
//            NodeHelper.traverse(control, Direction.LEFT, TraversalMethod.DEFAULT)
//          }
//          consume { it.code == KeyCode.RIGHT } by {
//            NodeHelper.traverse(control, Direction.RIGHT, TraversalMethod.DEFAULT)
//          }
//          consume { it.code == KeyCode.UP } by {
//            NodeHelper.traverse(control, Direction.UP, TraversalMethod.DEFAULT)
//          }
//          consume { it.code == KeyCode.DOWN } by {
//            NodeHelper.traverse(control, Direction.DOWN, TraversalMethod.DEFAULT)
//          }
//
//          consume { it.code == KeyCode.ENTER } by {
//            _startEdit()
//          }
//        }
//      }

    }

    fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
      when (event) {
        is TableEvent.Focus<T,out Any> -> {
          if (event.row==control.row && event.column == column) {
            control.requestFocus()
          }
        }
        is TableEvent.Blur<T,out Any> -> {
          if (event.row==control.row && event.column == column) {
            // focus another element to remove focus from this control
            val temp = Label()
            children.add(temp)
            temp.requestFocus()
            children.remove(temp)
          }
        }
        is TableEvent.StartEdit<T,out Any> -> {
          if (event.row==control.row && event.column == column) {
            _startEdit()
          }
        }
        is TableEvent.StopEdit<T,out Any> -> {
          if (event.row==control.row && event.column == column) {
            _cancelEdit()
          }
        }
        else -> {
          println("ignore: $event")
        }
      }
    }

    override fun computeMinWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
      return java.lang.Double.max(label.minWidth(height), field.minWidth(height))
    }

    override fun computeMinHeight(width: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
      return java.lang.Double.max(label.minHeight(width), field.minHeight(width))
    }

//    override fun computePrefWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
//      var base = if (label.isVisible) label.prefWidth(height) * 2.0 else field.prefWidth(height)
//      base = label.prefWidth(height) * 2.0
//      return base + leftInset + rightInset
//    }

    override fun computePrefHeight(width: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
      return java.lang.Double.max(label.prefHeight(width), field.prefHeight(width)) + topInset + bottomInset
    }

    fun preferredColumnSize(): Double {
      val labelWidth = label.prefWidth(height)
      val fieldWidth = if (field.isVisible) field.prefWidth(height) else field.minWidth
      return java.lang.Double.max(labelWidth, fieldWidth) + insets.left + insets.right
    }

  }

  companion object {
    fun <T : Any> createTextField(
      value: T?,
      converter: StringConverter<T>,
      commitEdit: (T?) -> Unit,
      cancelEdit: () -> Unit
    ): TextField {
      val textField = TextField()
      textField.text = converter.toString(value)

//      textField.onAction = EventHandler { event: ActionEvent ->
//        event.consume()
//        commitEdit(converter.fromString(textField.text))
//      }
      textField.onKeyReleased = EventHandler { t: KeyEvent ->
        if (t.code == KeyCode.ENTER) {
          t.consume()
          commitEdit(converter.fromString(textField.text))
        }
        if (t.code == KeyCode.ESCAPE) {
          t.consume()
          cancelEdit()
        }
      }
      return textField
    }
  }

}