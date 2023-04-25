package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.bindings.mapToDouble
import de.flapdoodle.kfx.events.SharedLock
import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.layout.backgrounds.Bounds
import de.flapdoodle.kfx.layout.virtual.ScrollBounds
import de.flapdoodle.kfx.layout.virtual.bind
import de.flapdoodle.kfx.nodeeditor.hints.NodeConnectionHint
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Orientation
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.ScrollBar
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.input.ZoomEvent
import javafx.scene.layout.Region
import javafx.scene.transform.Scale

class NodeView(
  val sharedEventLock: SharedLock<Node> = SharedLock(),
  nodeRegistry: NodeRegistry
) : Region() {

  private val layers = Layers(nodeRegistry)

  private val zoom: DoubleProperty = object : SimpleDoubleProperty(1.0) {
    override fun invalidated() {
      requestLayout()
    }
  }

  private val scrollX = ScrollBar()
  private val scrollY = ScrollBar()

  private val nodeBoundingBoxProperty = layers.boundingBoxProperty()
  private val zoomedBounds = nodeBoundingBoxProperty.and(zoom.mapToDouble()).map(BoundingBoxes::multiply)
  private val scrollXBounds = widthProperty().and(zoomedBounds).and(layers.layoutXProperty()).map(ScrollBounds.Companion::widthOf)
  private val scrollYBounds = heightProperty().and(zoomedBounds).and(layers.layoutYProperty()).map(ScrollBounds.Companion::heightOf)
  private val nodeConnectionHint = NodeConnectionHint().apply {
    isVisible = false
  }

  init {
    styleClass.addAll("node-view")
    stylesheets += javaClass.getResource("NodeView.css").toExternalForm()

    if (false) {
      layers.addHints(BoundingBoxes.bindRectangle(layers.boundingBoxProperty()).apply {
        styleClass.addAll("content-background-union")
      })
      layers.addHints(BoundingBoxes.bindRectangle(layers.nodes().boundingBoxProperty()).apply {
        styleClass.addAll("content-background")
      })
      layers.addHints(BoundingBoxes.bindRectangle(layers.connections().boundingBoxProperty()).apply {
        styleClass.addAll("content-background")
      })
    }
    layers.addHints(nodeConnectionHint)

    layers.transforms.add(Scale().apply {
      xProperty().bind(zoom)
      yProperty().bind(zoom)
    })
    children.add(layers)

    scrollX.orientation = Orientation.HORIZONTAL
    scrollX.valueProperty().bindBidirectional(layers.layoutXProperty())
    scrollX.styleClass.add("graph-editor-scroll-bar") //$NON-NLS-1$
    scrollX.bind(scrollXBounds)
    scrollY.orientation = Orientation.VERTICAL
    scrollY.valueProperty().bindBidirectional(layers.layoutYProperty())
    scrollY.styleClass.add("graph-editor-scroll-bar") //$NON-NLS-1$
    scrollY.bind(scrollYBounds)

    children.addAll(scrollX,scrollY)

    clip = Bounds.sizeRectangle(this)

    addEventHandler(MouseEvent.ANY, this::handleMouseEvent)

    addEventHandler(ZoomEvent.ANY, this::handleZoom)
    addEventHandler(ScrollEvent.SCROLL, this::handleScroll)
  }

  fun layers() = layers

  fun nodeConnectionHint() = nodeConnectionHint

  override fun layoutChildren() {
    super.layoutChildren()

    val w = scrollY.width
    val h = scrollX.height

    scrollX.resizeRelocate(0.0, snapPositionY(height - h), snapSizeX(width - w), h)
    scrollY.resizeRelocate(snapPositionX(width - w), 0.0, w, snapSizeY(height - h))
  }

  fun zoom(zoom: Double) {
    setZoom(zoom)
  }

  fun zoomAt(zoom: Double, x: Double, y: Double) {
    setZoomAt(zoom, x, y)
  }


  private fun handleMouseEvent(event: MouseEvent) {
//    println("NodeView.handleMouseEvent")
    when (event.eventType) {
      MouseEvent.MOUSE_PRESSED -> {
        sharedEventLock.tryLock(this) {
          event.consume()

          cursor = Cursor.MOVE
          Action.Pan(
            clickPosition = event.screenPosition,
            posAtClick = layers.layoutPosition
          )
        }
      }
      MouseEvent.MOUSE_DRAGGED -> sharedEventLock.ifLocked(this, Action::class.java) {
        val current = it.value
        when (current) {
          is Action.Pan -> {
            event.consume()

            val delta = event.screenPosition - current.clickPosition
            val newPosition = current.posAtClick + delta
            panTo(newPosition.x, newPosition.y)
          }
          else -> {

          }
        }
      }
      MouseEvent.MOUSE_RELEASED,
      MouseEvent.MOUSE_CLICKED -> {
        sharedEventLock.tryRelease(this, Action::class.java) { it: Action ->
          when (it) {
            is Action.Pan -> {
              event.consume()

              cursor = null
            }

            else -> {

            }
          }
        }
      }
    }
  }


  private fun handleScroll(pEvent: ScrollEvent) {
    // this intended for mouse-scroll events (event direct == false)
    // the event also gets synthesized from touch events, which we want to ignore as they are handled in handleZoom()
    if (!pEvent.isDirect && pEvent.touchCount <= 0) {

      if (pEvent.isControlDown) {
        val local = layers.screenToLocal(Point2D(pEvent.screenX, pEvent.screenY))
//        println("zoom at ${pEvent.x},${pEvent.y} -> (screen: ${pEvent.screenX}, ${pEvent.screenY}) local: ${local.x},${local.y}) on ${wrapper.boundsInParent}")
        when (pEvent.eventType) {
          ScrollEvent.SCROLL -> {
            sharedEventLock.ifUnlocked {
              if (pEvent.deltaY != 0.0) {
                val direction = if (pEvent.deltaY > 1) ZoomDirection.In else ZoomDirection.Out
                zoom(direction, pEvent.x, pEvent.y)
              }
              pEvent.consume()
            }
          }
          ScrollEvent.SCROLL_STARTED -> {
            sharedEventLock.tryLock(this) {

              if (pEvent.deltaY != 0.0) {
                val direction = if (pEvent.deltaY > 1) ZoomDirection.In else ZoomDirection.Out
                zoom(direction, pEvent.x, pEvent.y)
              }
              pEvent.consume()
              Action.Zoom
            }
          }
          ScrollEvent.SCROLL_FINISHED -> {
            sharedEventLock.tryRelease(this, Action::class.java) { it: Action ->
              pEvent.consume()
            }
          }
        }
      } else {
        sharedEventLock.ifUnlocked {
          panTo(layers.layoutX + pEvent.deltaX, layers.layoutY + pEvent.deltaY)
          pEvent.consume()
        }
      }
    }
  }

  private fun handleZoom(pEvent: ZoomEvent) {
    when (pEvent.eventType) {
      ZoomEvent.ZOOM_STARTED -> {
        sharedEventLock.tryLock(this) {
          pEvent.consume()
          Action.Zoom
        }
      }
      ZoomEvent.ZOOM_FINISHED -> {
        sharedEventLock.tryRelease(this, Action::class.java) { it: Action ->
          pEvent.consume()
        }
      }
      ZoomEvent.ZOOM -> {
        sharedEventLock.ifLocked(this, Action::class.java) {
          val action = it.value
          when (action) {
            is Action.Zoom -> {
              val newZoomLevel: Double = zoom.get() * pEvent.zoomFactor
              setZoomAt(newZoomLevel, pEvent.x, pEvent.y)
              pEvent.consume()
            }
            else -> {

            }
          }
        }
      }
    }
  }


  fun panTo(x: Double, y: Double) {
    layers.layoutX = x
    layers.layoutY = y
  }

  fun setZoom(pZoom: Double) {
    setZoomAt(pZoom, layers.layoutX, layers.layoutY)
  }

  enum class ZoomDirection {
    In, Out
  }

  fun zoom(direction: ZoomDirection, pPivotX: Double, pPivotY: Double) {
    val modifier = if (direction == ZoomDirection.In) 0.06 else -0.06
    val lastZoom = zoom.get()
    val newZoom = lastZoom + modifier
    setZoomAt(newZoom, pPivotX, pPivotY)
  }

  fun setZoomAt(pZoom: Double, pPivotX: Double, pPivotY: Double) {
    val oldZoomLevel: Double = zoom.get()
    val newZoomLevel = constrainZoom(pZoom)
    if (newZoomLevel != oldZoomLevel) {
      val f = newZoomLevel / oldZoomLevel - 1
      zoom.set(newZoomLevel)
      val pivotInWrapperSpace = layers.parentToLocal(Point2D(pPivotX, pPivotY))
      val diffX = - f * pivotInWrapperSpace.x
      val diffY = - f * pivotInWrapperSpace.y
//      println("pan diff: $diffX,$diffY - ${wrapper.layoutX},${wrapper.layoutY}")
//      println("$pivotInWrapperSpace -> ${wrapper.layoutX},${wrapper.layoutY}")
      panTo(layers.layoutX + diffX, layers.layoutY + diffY)
    }
  }

  private fun constrainZoom(pZoom: Double): Double {
    val zoom = Math.round(pZoom * 100.0) / 100.0
    if (zoom <= 1.02 && zoom >= 0.98) {
      return 1.0
    }
    val ret =
      Math.min(Math.max(zoom, 0.5), 1.5)
    return ret
  }


  sealed class Action {
    data class Pan(val clickPosition: Point2D, val posAtClick: Point2D) : Action()
    object Zoom : Action()
  }

  override fun computePrefHeight(width: Double): Double {
    return layers.prefHeight(width)
  }

  override fun computePrefWidth(height: Double): Double {
    return layers.prefWidth(height)
  }
}