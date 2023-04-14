package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.events.SharedLock
import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.extensions.Nodes
import de.flapdoodle.kfx.graph.nodes.SizeMode
import de.flapdoodle.kfx.nodeeditor.Node.Style.disable
import de.flapdoodle.kfx.nodeeditor.Node.Style.enable
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.geometry.Point2D
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane

class NodeEditor : AnchorPane() {
  val sharedLock = SharedLock<javafx.scene.Node>()

  init {
    children.add(NodeView(sharedLock).withAnchors(all = 0.0).apply {
      getViewChildren().addAll(Node("one").apply {
        layoutPosition = Point2D(100.0, 50.0)
      }, Node("two").apply {
        content = Button("Helloooo")
      })
    })

    addEventHandler(MouseEvent.ANY, this::handleMouseEvent)
  }

  private fun handleMouseEvent(event: MouseEvent) {
//    if (event.eventType == MouseEvent.MOUSE_ENTERED_TARGET || event.eventType == MouseEvent.MOUSE_EXITED_TARGET ) {
//      println("--> ${event.eventType}  ${event.target}")
//    }

    if (event.eventType == MouseEvent.MOUSE_MOVED) {
      println("------------------")
      Nodes.hit(
        this,
        Point2D(event.screenX, event.screenY),
        1.0,
        javafx.scene.Node::screenToLocal
      ).filter { it is Node || Markers.isDragBar(it) }
        .forEach {
          println("node -> $it")
        }
    }

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
          sharedLock.ifLocked(Node::class.java, Action.Focus::class.java) { lock ->
            if (target is javafx.scene.Node && Markers.isDragBar(target)) {
              cursor = SizeMode.INSIDE.cursor()
              lock.replaceLock(Action.Focus(SizeMode.INSIDE))
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
          sharedLock.ifLocked(Node::class.java, Action.Focus::class.java) {
            if (target is javafx.scene.Node && Markers.isDragBar(target)) {
              cursor = null
              it.replaceLock(Action.Focus())
            }
          }
        }
      }
    }

    sharedLock.ifLocked(Node::class.java, Action::class.java) { lock ->
      val action = lock.value
      val active = lock.owner
      if (action is Action.Focus) {
//        println("action -> $action")
        when (event.eventType) {
          MouseEvent.MOUSE_PRESSED -> {
            if (action.sizeMode!=null) {
              cursor = action.sizeMode.cursor()

              val newAction = when(action.sizeMode) {
                SizeMode.INSIDE -> Action.Move(
                  clickPosition = event.screenPosition,
                  layoutPosition = active.layoutPosition
                )
                else -> Action.Resize(
                  clickPosition = event.screenPosition,
                  sizeMode = action.sizeMode,
                  layout = LayoutBounds(active.layoutPosition, active.size)
                )
              }

              event.consume()
              lock.replaceLock(newAction)
            }
          }

          MouseEvent.MOUSE_MOVED -> {

//            val picked = Nodes.pick(this@NodeEditor, event.sceneX, event.sceneY)
//            if (picked!=null) {
//              println("node -> $picked")
//            }

            val targetLocalPosition = active.screenToLocal(event.screenPosition)
            val sizeMode = SizeMode.guess(targetLocalPosition, active.size)
            if (sizeMode != SizeMode.INSIDE) {
              if (sizeMode!=null) {
                cursor = sizeMode.cursor()
                lock.replaceLock(Action.Focus(sizeMode))
              }
            } else {
              if (action.sizeMode!=SizeMode.INSIDE) {
                cursor = null
                lock.replaceLock(Action.Focus())
              }
            }
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

              else -> {
                println(".. should not happen")
              }
            }
          }

          MouseEvent.MOUSE_RELEASED -> {
            event.consume()
            cursor = null

            lock.replaceLock(Action.Focus())
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
