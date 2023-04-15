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

    addEventHandler(MouseEvent.ANY, this::handleMouseEventSimplified)
  }

  private fun handleMouseEventSimplified(event: MouseEvent) {
//    println("--> ${event.eventType} : $sharedLock")

    val target = event.target

    when (event.eventType) {
      MouseEvent.MOUSE_ENTERED_TARGET -> {
        if (target is Node) {
          sharedLock.tryLock(target) {
            event.consume()
            Node.Style.Active.enable(target)
            Action.Focus
          }
        }
      }
      MouseEvent.MOUSE_EXITED_TARGET -> {
        if (target is Node) {
          sharedLock.ifLocked(target, Action.Focus::class.java) {
            event.consume()
            cursor = null
            Node.Style.Active.disable(target)
            it.releaseLock()
          }
        }
      }
      MouseEvent.MOUSE_MOVED -> {
        sharedLock.ifLocked(Node::class.java, Action::class.java) {
          val nodeAction = bestAction(event.screenPosition)
          val action = nodeAction?.second
          cursor = when (action) {
            is Action.Move -> SizeMode.INSIDE.cursor()
            is Action.Resize -> action.sizeMode.cursor()
            else -> null
          }
        }
      }
      MouseEvent.MOUSE_PRESSED -> {
        sharedLock.ifLocked(Node::class.java, Action::class.java) {
          val nodeAction = bestAction(event.screenPosition)
          if (nodeAction != null) {
            event.consume()
            it.replaceLock(nodeAction.second)
          } else {
            println("no action found")
          }
        }
      }
      MouseEvent.MOUSE_DRAGGED -> {
        sharedLock.ifLocked(Node::class.java, Action::class.java) { lock ->
          val action = lock.value
          val active = lock.owner

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
              // Ignore Focus
            }
          }

        }
      }
      MouseEvent.MOUSE_RELEASED -> {
        sharedLock.ifLocked(Node::class.java, Action::class.java) { lock ->
          event.consume()
          cursor = null
          lock.replaceLock(Action.Focus)
        }
      }
    }
  }

  private fun bestAction(screenPosition: Point2D): Pair<Node,Action>? {
    val nodesAndBars = Nodes.hit(
      this,
      screenPosition,
      0.0,
      javafx.scene.Node::screenToLocal
    ).filter {
      it is Node || Markers.isDragBar(it)
    }.toList()

    val bestSizeMode = nodesAndBars.map { when {
      Markers.isDragBar(it) -> SizeMode.INSIDE
      it is Node -> {
        val targetLocalPosition = it.screenToLocal(screenPosition)
        val sizeMode = SizeMode.guess(targetLocalPosition, it.size)
        if (sizeMode != SizeMode.INSIDE) sizeMode else null
      }

      else -> null
    } }.firstOrNull()

    val matchingNode = nodesAndBars.filterIsInstance<Node>().firstOrNull()
    return if (matchingNode!=null && bestSizeMode!=null) {
      matchingNode to when (bestSizeMode) {
        SizeMode.INSIDE -> Action.Move(screenPosition, matchingNode.layoutPosition)
        else -> Action.Resize(screenPosition,bestSizeMode,LayoutBounds(matchingNode.layoutPosition, matchingNode.size))
      }
    } else null
  }

  sealed class Action {
    object Focus : Action()
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
