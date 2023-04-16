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

class NodeEditor : AnchorPane() {
  private val sharedLock = SharedLock<javafx.scene.Node>()
  private val view = NodeView(sharedLock).withAnchors(all = 0.0)
  init {
//    children.add(view.apply {
//      layers().nodes().children.addAll(Node("one").apply {
//        layoutPosition = Point2D(100.0, 50.0)
//      }, Node("two").apply {
//        content = Button("Helloooo")
//      })
//    })
    children.add(view)
    addEventFilter(MouseEvent.ANY, this::filterMouseEvents)
  }

  fun layers() = view.layers()

  private fun filterMouseEvents(event: MouseEvent) {
    val target = event.target

    when (event.eventType) {
      MouseEvent.MOUSE_ENTERED_TARGET -> sharedLock.ifUnlocked {
        if (target is Node) {
          Node.Style.Active.enable(target)
        }
      }
      MouseEvent.MOUSE_EXITED_TARGET -> sharedLock.ifUnlocked {
        if (target is Node) {
          Node.Style.Active.disable(target)
        }
      }
      MouseEvent.MOUSE_MOVED -> sharedLock.ifUnlocked {
        val nodeAction = bestAction(event.screenPosition)
        val action = nodeAction?.second
        cursor = when (action) {
          is Action.Move -> SizeMode.INSIDE.cursor()
          is Action.Resize -> action.sizeMode.cursor()
          else -> null
        }
      }
      MouseEvent.MOUSE_PRESSED -> {
        val nodeAction = bestAction(event.screenPosition)
        if (nodeAction != null) {
          sharedLock.tryLock(nodeAction.first) {
            event.consume()
            nodeAction.second
          }
        }
      }
      MouseEvent.MOUSE_DRAGGED -> sharedLock.ifLocked(Node::class.java, Action::class.java) { (active, action) ->
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
        }
      }
      MouseEvent.MOUSE_RELEASED -> sharedLock.ifLocked(Node::class.java, Action::class.java) { lock ->
        event.consume()

        cursor = null
        lock.releaseLock()
      }
    }
  }

  private fun bestAction(screenPosition: Point2D): Pair<Node,Action>? {
    val nodesAndBars = pickScreen(screenPosition)
      .filter {
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
