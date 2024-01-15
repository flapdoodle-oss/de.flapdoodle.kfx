package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.events.SharedLock
import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.graph.nodes.SizeMode
import de.flapdoodle.kfx.nodeeditor.types.NodeSlotId
import de.flapdoodle.kfx.types.AngleAtPoint2D
import de.flapdoodle.kfx.types.ColoredAngleAtPoint2D
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.event.EventTarget
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color

class NodeEditor : AnchorPane() {
  private val sharedLock = SharedLock<javafx.scene.Node>()
  private val nodeRegistry = NodeRegistry()
  private val view = NodeView(sharedLock, nodeRegistry).withAnchors(all = 0.0)
  init {
    children.add(view)
    addEventFilter(MouseEvent.ANY, this::filterMouseEvents)
    addEventFilter(KeyEvent.ANY, this::filterKeyEvents)
  }

  fun layers() = view.layers()

  private fun filterMouseEvents(event: MouseEvent) {
    val target = event.target

    when (event.eventType) {
      MouseEvent.MOUSE_ENTERED_TARGET -> focus(target)
      MouseEvent.MOUSE_EXITED_TARGET -> blur(target)
      MouseEvent.MOUSE_MOVED -> updateCursor(event)

      MouseEvent.MOUSE_PRESSED -> onMousePressed(event)
      MouseEvent.MOUSE_DRAGGED -> onMouseDragged(event)
      MouseEvent.MOUSE_RELEASED -> onMouseReleased(event)
    }
  }

  private fun filterKeyEvents(event: KeyEvent) {
    when (event.eventType) {
      KeyEvent.KEY_RELEASED -> sharedLock.ifUnlocked {
//        println("event--> code: ${event.code}, char: ${event.character}")
        if (event.code==KeyCode.DELETE) {
          val selected = layers().allConnections().filter { it -> it.isSelected() }
          layers().removeConnections(selected)
        }
      }
    }
  }

  private fun onMousePressed(event: MouseEvent) {
    when (val elementAndAction = guessAction(event.screenPosition)) {
      is ElementAction.NodeAndAction -> {
        sharedLock.tryLock(elementAndAction.node) {
          event.consume()
          val action = elementAndAction.action
          if (action is NodeAction.Connect) {
            val position = nodeRegistry.scenePositionOf(action.source)!!
            view.nodeConnectionHint().apply {
              isVisible = true
              start(position)
              end(position.copy(angle = position.angle - 180))
            }
            action
          } else
            action
        }
      }

      is ElementAction.ConnectionAndAction -> {
        sharedLock.tryLock(elementAndAction.nodeConnection) {
          event.consume()
          elementAndAction.action
        }
      }

      else -> {

      }
    }
  }

  private fun onMouseReleased(event: MouseEvent) {
    sharedLock.ifLocked(Node::class.java, NodeAction::class.java) { lock ->
      event.consume()
      val action = lock.value
      if (action is NodeAction.Connect) {
        view.nodeConnectionHint().isVisible = false

        if (action.destination != null) {
          layers().addConnections(NodeConnection("blob", action.source, action.destination))
        }
      }

      cursor = null
      lock.releaseLock()
    }
    sharedLock.ifLocked(NodeConnection::class.java, ConnectionAction::class.java) { lock ->
      event.consume()
      println("select ${lock.owner}")
      lock.owner.toggleSelect()

  //          cursor = null
      lock.releaseLock()
    }
  }

  private fun onMouseDragged(event: MouseEvent) {
    sharedLock.ifLocked(Node::class.java, NodeAction::class.java) { lock ->
      event.consume()
      val action = lock.value
      val active = lock.owner

      when (action) {
        is NodeAction.Move -> {
          val diff = event.screenPosition - action.clickPosition
          active.layoutPosition = action.layoutPosition + active.screenDeltaToLocal(diff)
        }

        is NodeAction.Resize -> {
          val diff = event.screenPosition - action.clickPosition
          val fixedDiff = active.screenDeltaToLocal(diff)
          val resizedBounds = SizeMode.resize(action.sizeMode, action.layout, fixedDiff)

          active.resizeTo(resizedBounds)
        }

        is NodeAction.Connect -> {
          view.nodeConnectionHint()
            .end(ColoredAngleAtPoint2D(event.scenePosition, Point2DMath.angle(action.clickPosition, event.screenPosition) - 180, Color.BLACK))

          val nextBestGuess = guessAction(event.screenPosition)
          if (nextBestGuess != null && nextBestGuess is ElementAction.NodeAndAction) {
            val nextAction = nextBestGuess.action
            if (nextAction is NodeAction.Connect) {
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
    sharedLock.ifLocked(NodeConnection::class.java, ConnectionAction::class.java) { lock ->
      event.consume()
      when (lock.value) {
        is ConnectionAction.Select -> {
          println("do not select ${lock.owner}")
          lock.releaseLock()
        }
      }
    }
  }

  private fun focus(target: EventTarget?) {
    sharedLock.ifUnlocked {
      when (target) {
        is Node -> target.focus()
        is NodeConnection -> target.focus()
      }
    }
  }

  private fun blur(target: EventTarget?) {
    sharedLock.ifUnlocked {
      when (target) {
        is Node -> target.blur()
        is NodeConnection -> target.blur()
      }
    }
  }

  private fun updateCursor(event: MouseEvent) {
    sharedLock.ifUnlocked {
      cursor = when (val elementAndAction = guessAction(event.screenPosition)) {
        is ElementAction.NodeAndAction -> {
          when (elementAndAction.action) {
            is NodeAction.Move -> SizeMode.INSIDE.cursor()
            is NodeAction.Resize -> elementAndAction.action.sizeMode.cursor()
            is NodeAction.Connect -> Cursor.CROSSHAIR
          }
        }

        is ElementAction.ConnectionAndAction -> {
          Cursor.CLOSED_HAND
        }

        else -> {
          null
        }
      }
    }
  }

  private fun guessAction(screenPosition: Point2D): ElementAction? {
    val nodesAndMarkers = pickScreen(screenPosition)
      .filter {
        it is Node || it is NodeConnection || Markers.isDragBar(it) || Markers.nodeSlot(it) != null
      }.toList()

    val matchingNode = nodesAndMarkers.filterIsInstance<Node>().firstOrNull()

    if (matchingNode!=null) {
      val bestSizeMode = nodesAndMarkers.map {
        when {
          Markers.isDragBar(it) -> SizeMode.INSIDE
          it is Node -> {
            val targetLocalPosition = it.screenToLocal(screenPosition)
            val sizeMode = SizeMode.guess(targetLocalPosition, it.size)
            if (sizeMode != SizeMode.INSIDE) sizeMode else null
          }

          else -> null
        }
      }.firstOrNull()

      val nodeSlotId = nodesAndMarkers.map(Markers::nodeSlot).firstOrNull()

      return when {
        nodeSlotId != null -> {
          ElementAction.NodeAndAction(matchingNode, NodeAction.Connect(screenPosition, matchingNode.layoutPosition, nodeSlotId))
        }
        bestSizeMode != null -> {
          when (bestSizeMode) {
            SizeMode.INSIDE -> ElementAction.NodeAndAction(matchingNode, NodeAction.Move(screenPosition, matchingNode.layoutPosition))
            else -> ElementAction.NodeAndAction(matchingNode, NodeAction.Resize(screenPosition,bestSizeMode,LayoutBounds(matchingNode.layoutPosition, matchingNode.size)))
          }
        }
        else -> null
      }
    } else {
      val matchingNodeConnection = nodesAndMarkers.filterIsInstance<NodeConnection>().firstOrNull()

      return matchingNodeConnection?.let { ElementAction.ConnectionAndAction(it, ConnectionAction.Select(screenPosition)) }
    }
  }

  sealed class ElementAction {
    data class NodeAndAction(val node: Node, val action: NodeAction) : ElementAction()
    data class ConnectionAndAction(val nodeConnection: NodeConnection, val action: ConnectionAction) : ElementAction()
  }
  
  sealed class NodeAction {
    data class Move(
      val clickPosition: Point2D,
      val layoutPosition: Point2D
    ) : NodeAction()

    data class Resize(
      val clickPosition: Point2D,
      val sizeMode: SizeMode,
      val layout: LayoutBounds
    ) : NodeAction()

    data class Connect(
      val clickPosition: Point2D,
      val layoutPosition: Point2D,
      val source: NodeSlotId,
      val destination: NodeSlotId? = null
    ) : NodeAction()
  }

  sealed class ConnectionAction {
    data class Select(
      val clickPosition: Point2D
    ) : ConnectionAction()
  }

}
