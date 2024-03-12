package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.bindings.Subscriptions
import de.flapdoodle.kfx.extensions.PseudoClassWrapper
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.hide
import de.flapdoodle.kfx.extensions.show
import de.flapdoodle.kfx.transitions.DelayedAction
import de.flapdoodle.kfx.transitions.DelayedMouseAction
import javafx.animation.Animation
import javafx.animation.ScaleTransition
import javafx.animation.Transition
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ObservableValue
import javafx.css.PseudoClass
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.util.Duration

class Row<T : Any>(
  internal val eventListener: TableEventListener<T>,
  internal val columns: ReadOnlyObjectProperty<List<Column<T, out Any>>>,
  internal val value: T,
//  internal val index: Int,
  internal val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>
) : Control() {

  object Style {
    val Even = PseudoClassWrapper<Node>(PseudoClass.getPseudoClass("even"))
  }

  private val skin = Skin(this)

  init {
    isFocusTraversable = false

//    if (index % 2 == 0) {
//      Style.Even.enable(this)
//    }
  }

  override fun createDefaultSkin(): javafx.scene.control.Skin<*> {
    return skin
  }

  fun setIndex(index: Int) {
    skin.setIndex(index)
  }

  fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
    skin.onTableEvent(event)
  }

  class Skin<T : Any>(
    private val control: Row<T>
  ) : SkinBase<Row<T>>(control) {
    private val rowContainer = HBox()
    private val insertRowOnTopContainer = HBox()
    private val insertRowBottomContainer = HBox()
    private val all = VBox()

    init {
      insertRowOnTopContainer.cssClassName("row-insert-top")
      insertRowOnTopContainer.hide()
      rowContainer.cssClassName("row")
      insertRowBottomContainer.cssClassName("row-insert-bottom")
      insertRowBottomContainer.hide()

      all.children.add(insertRowOnTopContainer)
      all.children.add(rowContainer)
      all.children.add(insertRowBottomContainer)
      children.add(all)

      DelayedMouseAction.delay(all, Duration.millis(700.0), {
        insertRowOnTopContainer.show()
        insertRowBottomContainer.show()
      }, {
        insertRowOnTopContainer.hide()
        insertRowBottomContainer.hide()
      })

      insertRowOnTopContainer.addEventHandler(MouseEvent.MOUSE_RELEASED) {
        println("insert new row above")
      }

      insertRowBottomContainer.addEventHandler(MouseEvent.MOUSE_RELEASED) {
        println("insert new row below")
      }



      ObservableLists.syncWith(control.columns, rowContainer.children) {
        cell(it, control.value, control.columnWidthProperties(it)).apply {
//          property[Row::class] = control
          setEventListener(control.eventListener)
        }
      }
    }

    fun setIndex(index: Int) {
      Style.Even.set(rowContainer, index % 2 == 0)
    }

    internal fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
      rowContainer.children.forEach {
        (it as Cell<T, out Any>).onTableEvent(event)
      }
    }

    private fun <C : Any> cell(c: Column<T, C>, value: T, width: ObservableValue<Number>): Cell<T, C> {
      return c.cell(value).apply {
//        property[Column::class] = c
        prefWidthProperty().bind(width)
        setColumn(c)
      }
    }

  }
}
