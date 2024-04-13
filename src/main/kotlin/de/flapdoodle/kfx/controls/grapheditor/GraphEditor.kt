package de.flapdoodle.kfx.controls.grapheditor

import de.flapdoodle.kfx.controls.grapheditor.commands.Command
import de.flapdoodle.kfx.controls.grapheditor.events.Event
import de.flapdoodle.kfx.controls.grapheditor.events.EventListener
import de.flapdoodle.kfx.controls.grapheditor.hints.NodeConnectionHint
import de.flapdoodle.kfx.controls.grapheditor.types.IsSelectable
import de.flapdoodle.kfx.controls.grapheditor.types.VertexSlotId
import de.flapdoodle.kfx.controls.virtual.VirtualView
import de.flapdoodle.kfx.events.SharedLock
import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.types.ColoredAngleAtPoint2D
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventTarget
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color

class GraphEditor(
  private val eventListener: EventListener = EventListener { editor, event ->
    if (event is Event.ConnectTo) {
      editor.addEdge(Edge(event.start, event.end))
    }
    true
  }
) : AnchorPane() {
  private val sharedLock = SharedLock<javafx.scene.Node>()
  private val registry = Registry()
  private val nodeConnectionHint = NodeConnectionHint().apply {
    isVisible = false
  }
  private val layers = Layers(registry).apply {
    addHints(nodeConnectionHint)
  }
  private val view = VirtualView(layers,Layers::boundingBoxProperty,{
    layers.visibleArea().bind(it)
  }, sharedLock).withAnchors(all = 0.0)

//  private val view = View(sharedLock, registry).withAnchors(all = 0.0)
//  private var askForClick = SimpleBooleanProperty(false)
  private val currentCommand = SimpleObjectProperty<Command>(null)

  init {
    children.add(view)
    addEventFilter(MouseEvent.ANY, this::filterMouseEvents)
    currentCommand.subscribe { command ->
      when (command) {
        is Command.AskForPosition -> cursor = Cursor.CROSSHAIR
        is Command.PanTo -> {
          view.panTo(command.position.x, command.position.y)
          command.onSuccess()
        }
        else -> {
          cursor = null
        }
      }
    }
  }

  fun addVertex(vararg list: Vertex) {
    layers.addVertex(*list)
  }

  fun removeVertex(vararg list: Vertex) {
    layers.removeVertex(listOf(*list))
  }

  fun addEdge(vararg list: Edge) {
    layers.addEdge(*list)
  }

  fun removeEdge(vararg list: Edge) {
    layers.removeEdges(listOf(*list))
  }

  fun execute(command: Command) {
    if (command is Command.Abort) {
      currentCommand.value = null
    } else {
      currentCommand.value = command
    }
  }

  private fun filterMouseEvents(event: MouseEvent) {
    val target = event.target

    val command = currentCommand.value
    when (command) {
      is Command.AskForPosition -> {
        if (event.eventType == MouseEvent.MOUSE_RELEASED) {
          val layerLocal = layers.screenToLocal(event.screenPosition)
          //eventListener.onEvent(this, Event.Click(layerLocal))
          command.onSuccess(layerLocal)
          currentCommand.value = null
        }
        event.consume()
      }
      else -> {
        when (event.eventType) {
          MouseEvent.MOUSE_ENTERED_TARGET -> focus(target)
          MouseEvent.MOUSE_EXITED_TARGET -> blur(target)
          MouseEvent.MOUSE_MOVED -> updateCursor(event)

          MouseEvent.MOUSE_PRESSED -> onMousePressed(event)
          MouseEvent.MOUSE_DRAGGED -> onMouseDragged(event)
          MouseEvent.MOUSE_RELEASED -> onMouseReleased(event)
        }
      }
    }
  }

  private fun onMousePressed(event: MouseEvent) {
    when (val elementAndAction = guessAction(event.screenPosition)) {
      is ElementAction.VertexAndAction -> {
        sharedLock.tryIfLock(elementAndAction.vertex) {
          event.consume()
          val action = elementAndAction.action
          if (action is VertexAction.Connect) {
            if (eventListener.onEvent(this, Event.TryToConnect(action.source))) {
              val position = registry.scenePositionOf(action.source)!!
              nodeConnectionHint.apply {
                isVisible = true
                start(position)
                end(position.copy(angle = position.angle - 180))
              }
              action
            } else
              null
          } else
            action
        }
      }

      is ElementAction.EdgeAndAction -> {
        sharedLock.tryLock(elementAndAction.edge) {
          event.consume()
          elementAndAction.action
        }
      }

      else -> {

      }
    }
  }

  private fun onMouseReleased(event: MouseEvent) {
    sharedLock.ifLocked(Vertex::class.java, VertexAction::class.java) { lock ->
      event.consume()
      val action = lock.value
      if (action is VertexAction.Connect) {
        nodeConnectionHint.isVisible = false

        if (action.destination != null) {
          eventListener.onEvent(this, Event.ConnectTo(action.source, action.destination))
//          view.layers().addEdge(Edge("blob", action.source, action.destination))
        }
      } else {
        val mouseClickedWithoutMovement = when(action) {
          is VertexAction.Move -> action.clickPosition == event.screenPosition
          is VertexAction.Resize -> action.clickPosition == event.screenPosition
          is VertexAction.Select -> true
          else -> false
        }
        if (mouseClickedWithoutMovement) {
          if (event.clickCount == 2 && lock.owner.focused()) {
            lock.owner.toFront()
          }
          if (event.clickCount == 1 && lock.owner.focused()) {
            IsSelectable.select(event.isShiftDown, lock.owner, layers.vertices())
          }
        }
      }

      cursor = null
      lock.releaseLock()
    }
    sharedLock.ifLocked(Edge::class.java, EdgeAction::class.java) { lock ->
      event.consume()
      if (event.clickCount == 1 && lock.value is EdgeAction.Select) {
        IsSelectable.select(event.isShiftDown, lock.owner, layers.edges())
      }

  //          cursor = null
      lock.releaseLock()
    }
  }

  private fun onMouseDragged(event: MouseEvent) {
    sharedLock.ifLocked(Vertex::class.java, VertexAction::class.java) { lock ->
      event.consume()
      val action = lock.value
      val active = lock.owner

      when (action) {
        is VertexAction.Move -> {
          val diff = event.screenPosition - action.clickPosition
          active.layoutPosition = action.layoutPosition + active.screenDeltaToLocal(diff)
        }

        is VertexAction.Resize -> {
          val diff = event.screenPosition - action.clickPosition
          val fixedDiff = active.screenDeltaToLocal(diff)
          val resizedBounds = SizeMode.resize(action.sizeMode, action.layout, fixedDiff)

          active.resizeTo(resizedBounds)
        }

        is VertexAction.Connect -> {
          nodeConnectionHint
            .end(ColoredAngleAtPoint2D(event.scenePosition, Point2DMath.angle(action.clickPosition, event.screenPosition) - 180, Color.BLACK))

          val nextBestGuess = guessAction(event.screenPosition)
          if (nextBestGuess != null && nextBestGuess is ElementAction.VertexAndAction) {
            val nextAction = nextBestGuess.action
            var destination: VertexSlotId? = null
            if (nextAction is VertexAction.Connect) {
              if (eventListener.onEvent(this, Event.TryToConnectTo(action.source, nextAction.source))) {
                val position = registry.scenePositionOf(nextAction.source)!!
                nodeConnectionHint.end(position)
                destination = nextAction.source
              }
            }
            lock.replaceLock(action.copy(destination = destination))
          }
        }

        is VertexAction.Select -> {
          // ignore
        }
      }
    }
    sharedLock.ifLocked(Edge::class.java, EdgeAction::class.java) { lock ->
      event.consume()
      when (lock.value) {
        is EdgeAction.Select -> {
          println("do not select ${lock.owner}")
          lock.releaseLock()
        }
      }
    }
  }

  private fun focus(target: EventTarget?) {
    sharedLock.ifUnlocked {
      when (target) {
        is Vertex -> target.focus()
        is Edge -> target.focus()
      }
    }
  }

  private fun blur(target: EventTarget?) {
    sharedLock.ifUnlocked {
      when (target) {
        is Vertex -> target.blur()
        is Edge -> target.blur()
      }
    }
  }

  private fun updateCursor(event: MouseEvent) {
    sharedLock.ifUnlocked {
      cursor = when (val elementAndAction = guessAction(event.screenPosition)) {
        is ElementAction.VertexAndAction -> {
          when (elementAndAction.action) {
            is VertexAction.Move -> SizeMode.INSIDE.cursor()
            is VertexAction.Resize -> elementAndAction.action.sizeMode.cursor()
            is VertexAction.Connect -> Cursor.CROSSHAIR
            is VertexAction.Select -> null
          }
        }

        is ElementAction.EdgeAndAction -> {
          Cursor.CLOSED_HAND
        }

        else -> {
          null
        }
      }
    }
  }

  private fun guessAction(screenPosition: Point2D): ElementAction? {
    val allPicks = pickScreen(screenPosition).toList()
    val nodesAndMarkers = allPicks
      .filter {
        it is Vertex || it is Edge || Markers.isDragBar(it) || Markers.nodeSlot(it) != null
      }

    val matchingVertex = nodesAndMarkers.filterIsInstance<Vertex>().firstOrNull()
    val matchingContent = allPicks.filter { Markers.isContent(it) }.firstOrNull()
//    val firstPickIsSomethingDifferent = if (firstNode != null) !nodesAndMarkers.contains(firstNode) else false

//    println("firstNode: $matchingContent")
//    println("matchingVertex: $matchingVertex")
//    println("--> something different: $firstPickIsSomethingDifferent")

    if (matchingVertex!=null && matchingContent==null) {
      val bestSizeMode = nodesAndMarkers.map {
        when {
          Markers.isDragBar(it) -> SizeMode.INSIDE
          it is Vertex -> {
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
          ElementAction.VertexAndAction(matchingVertex, VertexAction.Connect(screenPosition, matchingVertex.layoutPosition, nodeSlotId))
        }
        bestSizeMode != null -> {
          when (bestSizeMode) {
            SizeMode.INSIDE -> ElementAction.VertexAndAction(matchingVertex, VertexAction.Move(screenPosition, matchingVertex.layoutPosition))
            else -> ElementAction.VertexAndAction(matchingVertex, VertexAction.Resize(screenPosition,bestSizeMode,LayoutBounds(matchingVertex.layoutPosition, matchingVertex.size)))
          }
        }
        else -> ElementAction.VertexAndAction(matchingVertex, VertexAction.Select(screenPosition))
      }
    } else {
      val matchingEdge = nodesAndMarkers.filterIsInstance<Edge>().firstOrNull()

      return matchingEdge?.let { ElementAction.EdgeAndAction(it, EdgeAction.Select(screenPosition)) }
    }
  }

  sealed class ElementAction {
    data class VertexAndAction(val vertex: Vertex, val action: VertexAction) : ElementAction()
    data class EdgeAndAction(val edge: Edge, val action: EdgeAction) : ElementAction()
  }
  
  sealed class VertexAction {
    data class Move(
      val clickPosition: Point2D,
      val layoutPosition: Point2D
    ) : VertexAction()

    data class Resize(
      val clickPosition: Point2D,
      val sizeMode: SizeMode,
      val layout: LayoutBounds
    ) : VertexAction()

    data class Connect(
      val clickPosition: Point2D,
      val layoutPosition: Point2D,
      val source: VertexSlotId,
      val destination: VertexSlotId? = null
    ) : VertexAction()

    data class Select(
      val clickPosition: Point2D
    ) : VertexAction()
  }

  sealed class EdgeAction {
    data class Select(
      val clickPosition: Point2D
    ) : EdgeAction()
  }

}
