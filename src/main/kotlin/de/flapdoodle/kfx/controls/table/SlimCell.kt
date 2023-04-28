package de.flapdoodle.kfx.controls.table

import com.sun.javafx.scene.NodeHelper
import com.sun.javafx.scene.traversal.Direction
import de.flapdoodle.kfx.events.handleEvent
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.hide
import de.flapdoodle.kfx.extensions.show
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.SkinBase
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.text.TextAlignment
import javafx.util.StringConverter

open class SlimCell<T: Any, C: Any>(
  val value: C?,
  val converter: StringConverter<C>,
  val editable: Boolean,
  val textAlignment: TextAlignment = TextAlignment.LEFT
) : Control() {

  private val skin = Skin(this)
  private var changeListener: ((C?) -> Unit)? = null

  init {
    isFocusTraversable = true
    cssClassName("slim-cell")
  }

  fun onChange(value: C?) {
    changeListener?.let {
      it(value)
    }
  }

  fun changeListener(listener: (C?) -> Unit) {
    changeListener = listener
  }

  override fun createDefaultSkin() = skin

  inner class Skin<T : Any, C : Any>(
    private val control: SlimCell<T, C>
  ) : SkinBase<SlimCell<T, C>>(control) {

    private val label = Label().apply {
      isWrapText = false
      prefWidth = Double.MAX_VALUE
      alignment = asPosition(control.textAlignment)
      text = control.converter.toString(control.value)
      if (control.editable) {
        control.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_RELEASED) {
          if (it.clickCount == 1) {
            control.requestFocus()
          }
          if (it.clickCount == 2) {
            _startEdit()
          }
          it.consume()
        }

        val logEvent = { it: KeyEvent ->
//          println("#############################")
//          println("event $it -> ${it.isConsumed} --> ${it.target} ? $control")
//          println("#############################")
        }

        control.handleEvent(KeyEvent.KEY_RELEASED) {
          matching { !it.isShortcutDown } then {
            consume { it.code == KeyCode.LEFT } by {
              logEvent(it)
              NodeHelper.traverse(control, Direction.LEFT)
            }
            consume { it.code == KeyCode.RIGHT } by {
              logEvent(it)
              NodeHelper.traverse(control, Direction.RIGHT)
            }
            consume { it.code == KeyCode.UP } by {
              logEvent(it)
              NodeHelper.traverse(control, Direction.UP)
            }
            consume { it.code == KeyCode.DOWN } by {
              logEvent(it)
              NodeHelper.traverse(control, Direction.DOWN)
            }

            consume { it.code == KeyCode.ENTER } by {
              logEvent(it)
              _startEdit()
            }
          }
        }

        control.focusedProperty().addListener { _, old, focused ->
          if (old!=focused) {
            if (focused) {
//              fireEvent(SmartEvents.CellFocused(control))
            } else {
              if (!editInProgress) {
//                fireEvent(SmartEvents.CellBlur(control))
              }
            }
          }
        }
      }
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
      converter = control.converter,
      commitEdit = {
//        control.value = it
//        label.text = control.converter.toString(it)
//        control.fireEvent(SmartEvents.EditDone(control))
        control.onChange(it)
        _editDone()
      },
      cancelEdit = this::_cancelEdit
    ).apply {
      isVisible = false
      isEditable = true
      focusedProperty().addListener { _, _, focused ->
        if (!focused) {
          _cancelEdit()
        }
      }
    }

    internal fun _editDone() {
      _cancelEdit()
    }

    var editInProgress: Boolean = false

    internal fun _cancelEdit() {
      editInProgress=false

      label.show()
      field.hide()
      field.text = label.text
    }

    internal fun _startEdit() {
//      RuntimeException("startEdit called").printStackTrace()
      editInProgress=true

      label.hide()
      field.show()
      field.requestFocus()
    }

    init {
      children.add(field)
      children.add(label)

      consumeMouseEvents(false)

//      control.prefWidthProperty().bind(control.column.widthProperty())
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