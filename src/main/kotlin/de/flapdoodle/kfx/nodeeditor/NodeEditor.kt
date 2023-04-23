package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.events.SharedLock
import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.graph.nodes.SizeMode
import de.flapdoodle.kfx.nodeeditor.types.NodeSlotId
import de.flapdoodle.kfx.types.AngleAtPoint2D
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane

class NodeEditor : AnchorPane() {
  private val sharedLock = SharedLock<javafx.scene.Node>()
  private val nodeRegistry = NodeRegistry()
  private val view = NodeView(sharedLock, nodeRegistry).withAnchors(all = 0.0)
  init {
    children.add(view)
    addEventFilter(MouseEvent.ANY, this::filterMouseEvents)
  }

  fun layers() = view.layers()

  private fun filterMouseEvents(event: MouseEvent) {
    val target = event.target

//    event.pickResult.intersectedNode?.let {
//      println("--> $it")
//    }
//    if (target is javafx.scene.Node) {
//      val connection = Markers.connection(target)
//      if (connection != null) {
//        println("found connection: $connection")
//      }
//    }

    when (event.eventType) {
      MouseEvent.MOUSE_ENTERED_TARGET -> sharedLock.ifUnlocked {
        if (target is Node) {
          Node.Style.Active.enable(target)
        }
        if (target is NodeConnection) {
          NodeConnection.Style.Focused.enable(target)
        }
      }
      MouseEvent.MOUSE_EXITED_TARGET -> sharedLock.ifUnlocked {
        if (target is Node) {
          Node.Style.Active.disable(target)
        }
        if (target is NodeConnection) {
          NodeConnection.Style.Focused.disable(target)
        }
      }
      MouseEvent.MOUSE_MOVED -> sharedLock.ifUnlocked {
        val nodeAction = bestAction(event.screenPosition)
        val action = nodeAction?.second
        cursor = when (action) {
          is Action.Move -> SizeMode.INSIDE.cursor()
          is Action.Resize -> action.sizeMode.cursor()
          is Action.Connect -> Cursor.CROSSHAIR
          else -> null
        }
      }
      MouseEvent.MOUSE_PRESSED -> {
        val nodeAction = bestAction(event.screenPosition)
        if (nodeAction != null) {
          sharedLock.tryLock(nodeAction.first) {
            event.consume()
            val action = nodeAction.second
            if (action is Action.Connect) {
              val hint = view.nodeConnectionHint()
              val position = nodeRegistry.scenePositionOf(action.source)!!
              hint.isVisible = true
              hint.start(position)
              hint.end(position.copy(angle = position.angle-180))
              action
            } else
              action
          }
        }
      }
      MouseEvent.MOUSE_DRAGGED -> sharedLock.ifLocked(Node::class.java, Action::class.java) { lock ->
        event.consume()
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
          is Action.Connect -> {
            val angle = Point2DMath.angle(action.clickPosition, event.screenPosition)
            view.nodeConnectionHint().end(AngleAtPoint2D(event.scenePosition, angle - 180))
            val nextBestNodeAndAction = bestAction(event.screenPosition)
            if (nextBestNodeAndAction!=null) {
              val nextAction = nextBestNodeAndAction.second
              if (nextAction is Action.Connect) {
                val position = nodeRegistry.scenePositionOf(nextAction.source)!!
                view.nodeConnectionHint().end(position)
                lock.replaceLock(action.copy(destination = nextAction.source))
              } else {
                lock.replaceLock(action.copy(destination = null))
              }
            }
          }
        }
      }
      MouseEvent.MOUSE_RELEASED -> sharedLock.ifLocked(Node::class.java, Action::class.java) { lock ->
        event.consume()
        val action = lock.value
        if (action is Action.Connect) {
          view.nodeConnectionHint().isVisible = false
          
          if (action.destination != null) {
            layers().addConnections(NodeConnection("blob", action.source, action.destination))
          }
        }

        cursor = null
        lock.releaseLock()
      }
    }
  }

  private fun bestAction(screenPosition: Point2D): Pair<Node,Action>? {
    val nodesAndMarkers = pickScreen(screenPosition)
      .filter {
        it is Node || Markers.isDragBar(it) || Markers.nodeSlot(it) != null
      }.toList()

    val bestSizeMode = nodesAndMarkers.map { when {
      Markers.isDragBar(it) -> SizeMode.INSIDE
      it is Node -> {
        val targetLocalPosition = it.screenToLocal(screenPosition)
        val sizeMode = SizeMode.guess(targetLocalPosition, it.size)
        if (sizeMode != SizeMode.INSIDE) sizeMode else null
      }

      else -> null
    } }.firstOrNull()

    val nodeSlotId = nodesAndMarkers.map(Markers::nodeSlot).firstOrNull()
    val matchingNode = nodesAndMarkers.filterIsInstance<Node>().firstOrNull()

    return if (matchingNode!=null) {
      when {
        nodeSlotId != null -> {
          matchingNode to Action.Connect(screenPosition, matchingNode.layoutPosition, nodeSlotId)
        }
        bestSizeMode != null -> {
          matchingNode to when (bestSizeMode) {
            SizeMode.INSIDE -> Action.Move(screenPosition, matchingNode.layoutPosition)
            else -> Action.Resize(screenPosition,bestSizeMode,LayoutBounds(matchingNode.layoutPosition, matchingNode.size))
          }
        }
        else -> null
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

    data class Connect(
      val clickPosition: Point2D,
      val layoutPosition: Point2D,
      val source: NodeSlotId,
      val destination: NodeSlotId? = null
    ) : Action()
  }

}
