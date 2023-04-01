package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.events.SharedEventLock
import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.graph.nodes.Movables
import de.flapdoodle.kfx.graph.nodes.SizeMode
import de.flapdoodle.kfx.layout.absolute.AbsolutePane
import de.flapdoodle.kfx.layout.virtual.PanZoomPanel
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane

class NodeEditor : AnchorPane() {
  val sharedEventLock = SharedEventLock()
  var focusedNode: Node? = null

  init {
    children.add(NodeView(sharedEventLock).withAnchors(all = 0.0).apply {
      setContent(Pane().apply {
        children.add(Node("one").apply {
          layoutPosition = Point2D(100.0, 50.0)
        })
        children.add(Node("two"))
      })
    })
    
    addEventHandler(MouseEvent.ANY, this::handleMouseEvent)
  }

  private fun handleMouseEvent(event: MouseEvent) {
    val target = event.target
    if (target is Node) {
      when (event.eventType) {
        MouseEvent.MOUSE_ENTERED_TARGET -> {
          sharedEventLock.lock(target) {
            event.consume()
            target.onFocus()

            Action.Focus
          }
        }
        MouseEvent.MOUSE_EXITED_TARGET -> {
          sharedEventLock.ifAnyLocked(Node::class.java, Action::class.java) { active, action ->
            if (action == Action.Focus) {
              sharedEventLock.release(active, Action::class.java) {
                event.consume()

                active.onBlur()
              }
            }
          }
        }
      }
    }

    sharedEventLock.ifAnyLocked(Node::class.java, Action::class.java) { active, action ->
      if (action == Action.Focus) {
        println("is focused")
        when (event.eventType) {
          MouseEvent.MOUSE_PRESSED -> {
            event.consume()

            val targetLocalPosition = active.parentToLocal(event.localPosition)
            val sizeMode = SizeMode.guess(targetLocalPosition, active.size)

            sharedEventLock.replaceLocked<Action>(active) {
              val newAction = if (sizeMode != null && sizeMode != SizeMode.INSIDE /*&& active.isResizeable()*/) {
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
              println("replace with $newAction")
              newAction
            }
          }
        }
      } else {
        println("or $action")
        when (event.eventType) {
          MouseEvent.MOUSE_DRAGGED -> {
            event.consume()

            when (action) {
              is Action.Move -> {
                val diff = event.screenPosition - action.clickPosition
                active.layoutPosition = action.layoutPosition + active.screenDeltaToLocal(diff)
              }

              is Action.Resize -> {
                val diff = event.screenPosition - action.clickPosition
                val fixedDiff = active.screenDeltaToLocal(diff)
                val resizedBounds = SizeMode.resize(action.sizeMode, action.layout, fixedDiff)
                active.layoutPosition = resizedBounds.layoutPosition
                active.resizeTo(resizedBounds.size.width, resizedBounds.size.height)
              }

              is Action.Focus -> {
                println("ignore...")
              }
            }
          }

          MouseEvent.MOUSE_RELEASED -> sharedEventLock.release(active, Action::class.java) {
            event.consume()

            cursor = null
          }
        }
      }
    }
  }

  sealed class Action {
    object Focus: Action()
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