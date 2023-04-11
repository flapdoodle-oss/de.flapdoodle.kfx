package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.events.SharedLock
import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.graph.nodes.SizeMode
import de.flapdoodle.kfx.nodeeditor.Node.Style.disable
import de.flapdoodle.kfx.nodeeditor.Node.Style.enable
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane

class NodeEditor : AnchorPane() {
  val sharedLock = SharedLock<javafx.scene.Node>()

  init {
    children.add(NodeView(sharedLock).withAnchors(all = 0.0).apply {
      getViewChildren().addAll(Node("one").apply {
        layoutPosition = Point2D(100.0, 50.0)
      }, Node("two"))
    })

    addEventHandler(MouseEvent.ANY, this::handleMouseEvent)
  }

  private fun handleMouseEvent(event: MouseEvent) {
//    if (event.eventType == MouseEvent.MOUSE_ENTERED_TARGET || event.eventType == MouseEvent.MOUSE_EXITED_TARGET ) {
//      println("--> ${event.eventType}  ${event.target}")
//    }
    val target = event.target
    when (event.eventType) {
      MouseEvent.MOUSE_ENTERED_TARGET -> {
        if (target is Node) {
          sharedLock.tryLock(target) {
            event.consume()
            Node.Style.Active.enable(target)

            Action.Focus()
          }
        } else {
          sharedLock.ifLocked(Node::class.java, Action.Focus::class.java) { active: Node, action: Action.Focus ->
            if (target is javafx.scene.Node && Markers.isDragBar(target)) {
              cursor = SizeMode.INSIDE.cursor()
              replaceLock(Action.Focus(SizeMode.INSIDE))
            }
          }
        }
      }

      MouseEvent.MOUSE_EXITED_TARGET -> {
        if (target is Node) {
          sharedLock.tryRelease(target, Action.Focus::class.java) {
            event.consume()
            cursor = null
            Node.Style.Active.disable(target)
          }
        } else {
          sharedLock.ifLocked(Node::class.java, Action.Focus::class.java) { active: Node, action: Action.Focus ->
            if (target is javafx.scene.Node && Markers.isDragBar(target)) {
              cursor = null
              replaceLock(Action.Focus())
            }
          }
        }
      }
    }

    sharedLock.ifLocked(Node::class.java, Action::class.java) { active: Node, action: Action ->
      if (action is Action.Focus) {
//        println("action -> $action")
        when (event.eventType) {
          MouseEvent.MOUSE_PRESSED -> {
            event.consume()

            val targetLocalPosition = active.screenToLocal(event.screenPosition)
            val sizeMode = SizeMode.guess(targetLocalPosition, active.size)

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

            replaceLock(newAction)
          }

          MouseEvent.MOUSE_MOVED -> {
            val targetLocalPosition = active.screenToLocal(event.screenPosition)
            val sizeMode = SizeMode.guess(targetLocalPosition, active.size)
            if (sizeMode != SizeMode.INSIDE) {
              if (sizeMode!=null) {
                cursor = sizeMode.cursor()
                replaceLock(Action.Focus(sizeMode))
              }
            } else {
              if (action.sizeMode!=SizeMode.INSIDE) {
                cursor = null
              }
            }
//
//            if (sizeMode!=SizeMode.INSIDE && action.sizeMode==null) {
//              val replacement = Action.Focus(sizeMode)
//              if (sizeMode != null) {
//                cursor = sizeMode.cursor()
//              } else {
//                cursor = null
//              }
//
//              replaceLock(replacement)
//            }
          }
        }
      } else {
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

                active.resizeTo(resizedBounds)
              }

              is Action.Focus -> {
                println("ignore...")
              }
            }
          }

          MouseEvent.MOUSE_RELEASED -> {
            event.consume()
            cursor = null

            replaceLock(Action.Focus())
          }
        }
      }
    }
  }

  sealed class Action {
    data class Focus(val sizeMode: SizeMode? = null) : Action()
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
