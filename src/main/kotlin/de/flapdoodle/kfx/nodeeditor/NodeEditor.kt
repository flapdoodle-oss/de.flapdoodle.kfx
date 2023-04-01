package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.events.SharedEventLock
import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.graph.nodes.Movables
import de.flapdoodle.kfx.graph.nodes.SizeMode
import de.flapdoodle.kfx.layout.absolute.AbsolutePane
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane

class NodeEditor : Pane() {
  val sharedEventLock = SharedEventLock()
  var focusedNode: Node? = null

  init {
    children.add(Node("one").apply {
//      layoutX = 100.0
//      layoutY = 50.0
      layoutPosition = Point2D(100.0, 50.0)
    })
    children.add(Node("two"))

    addEventHandler(MouseEvent.ANY, this::handleMouseEvent)
  }

  private fun handleMouseEvent(event: MouseEvent) {
    val target = event.target

    if (target is Node) {
      when (event.eventType) {
        MouseEvent.MOUSE_ENTERED_TARGET -> sharedEventLock.ifUnlocked {
          focusedNode = target
          target.onFocus()
        }

        MouseEvent.MOUSE_EXITED_TARGET -> sharedEventLock.ifUnlocked {
          target.onBlur()
          focusedNode = null
        }
      }
    }

//    println("event: ${event.eventType}")
//    println("focusedNode: $focusedNode")
//    println("sharedLock: ${sharedEventLock.current}")
    
    focusedNode.let { active ->
      if (active!=null) {
        when (event.eventType) {
          MouseEvent.MOUSE_PRESSED -> sharedEventLock.lock(active) {
            event.consume()

            val targetLocalPosition = active.parentToLocal(event.localPosition)
            val sizeMode = SizeMode.guess(targetLocalPosition, active.size)
            if (sizeMode != null && sizeMode != SizeMode.INSIDE /*&& active.isResizeable()*/) {
              cursor = sizeMode.cursor()
              Action.Resize(
                clickPosition = event.screenPosition,
                sizeMode = sizeMode,
                layout = LayoutBounds(active.layoutPosition, active.size)
              )
            } else {
              cursor = SizeMode.INSIDE.cursor()
              Action.Move(
                clickPosition = event.screenPosition,
                layoutPosition = active.layoutPosition
              )
            }
          }
          MouseEvent.MOUSE_DRAGGED -> sharedEventLock.ifLocked(active, Action::class.java) {
            event.consume()

            when (it) {
              is Action.Move -> {
                val diff = event.screenPosition - it.clickPosition
                active.layoutPosition = it.layoutPosition + active.screenDeltaToLocal(diff)
              }
              is Action.Resize -> {
                val diff = event.screenPosition - it.clickPosition
                val fixedDiff = active.screenDeltaToLocal(diff)
                val resizedBounds = SizeMode.resize(it.sizeMode, it.layout, fixedDiff)
                active.layoutPosition = resizedBounds.layoutPosition
                active.resizeTo(resizedBounds.size.width, resizedBounds.size.height)
              }
            }
          }
          MouseEvent.MOUSE_RELEASED -> sharedEventLock.release(active, Action::class.java) {
            event.consume()

            cursor = null
          }
          MouseEvent.MOUSE_MOVED -> sharedEventLock.ifUnlocked {
            val targetLocalPosition = active.parentToLocal(event.localPosition)
            val sizeMode = SizeMode.guess(targetLocalPosition, active.size)
            if (sizeMode != null && sizeMode != SizeMode.INSIDE /*&& active.isResizeable()*/) {
              cursor = sizeMode.cursor()
            } else {
              cursor = null
            }
          }
        }
      } else {
        when (event.eventType) {
          MouseEvent.MOUSE_MOVED -> {
            cursor = null
          }
        }
      }
    }
  }

  sealed class Action {
    data class Move(
      val clickPosition: Point2D,
      val layoutPosition: Point2D
    ) : Action()

    data class Resize(
      val clickPosition: Point2D,
      val sizeMode: SizeMode,
      val layout: LayoutBounds
    ) : Action()
  }

}