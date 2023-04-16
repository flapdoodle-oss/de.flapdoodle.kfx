/**
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.kfx.layout.virtual

import de.flapdoodle.kfx.events.SharedEventLock
import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.layout.backgrounds.Bounds
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

class PanZoomPanel(
    val sharedEventLock: SharedEventLock = SharedEventLock()
) : Region() {
    private val wrapper = Wrapper().markAsContainer()

    private val zoom: DoubleProperty = object : SimpleDoubleProperty(1.0) {
        override fun invalidated() {
            requestLayout()
        }
    }

    private val scrollX = ScrollBar()
    private val scrollY = ScrollBar()

    init {
        styleClass.addAll("pan-zoom-panel")
        stylesheets += javaClass.getResource("PanZoomPanel.css").toExternalForm();

        children.add(Bounds.childBoundsRectangle(wrapper).apply {
            styleClass.addAll("content-background")
        })

        wrapper.transforms.add(Scale().apply {
            xProperty().bind(zoom)
            yProperty().bind(zoom)
        })
        children.add(wrapper)

        scrollX.orientation = Orientation.HORIZONTAL
        scrollX.valueProperty().bindBidirectional(wrapper.layoutXProperty())
//        scrollX.styleClass.add("graph-editor-scroll-bar") //$NON-NLS-1$
        scrollY.orientation = Orientation.VERTICAL
        scrollY.valueProperty().bindBidirectional(wrapper.layoutYProperty())
        children.addAll(scrollX,scrollY)

        clip = Bounds.sizeRectangle(this)

        addEventHandler(MouseEvent.ANY, this::handleMouseEvent)

        addEventHandler(ZoomEvent.ANY, this::handleZoom)
        addEventHandler(ScrollEvent.SCROLL, this::handleScroll)
    }

    fun setContent(node: Node) {
        wrapper.setContent(node)
    }

    override fun layoutChildren() {
        super.layoutChildren()

        scrollX.setBounds(
            ScrollBounds.of(
                windowSize = width,
                itemSize = wrapper.containerlessBoundsInParent().width,
                itemOffset = zoom.get() * wrapper.containerlessBoundsInLocal().minX,
                currentItemOffset = wrapper.layoutX,
                false
            )
        )

        ScrollBounds.of(
            windowOffset = wrapper.layoutX,
            windowSize = width,
            zoom = zoom.get(),
            contentOffset = wrapper.containerlessBoundsInParent().minX,
            contentSize = wrapper.containerlessBoundsInParent().width
        )

        scrollY.setBounds(
            ScrollBounds.of(
                windowSize = height,
                itemSize =  wrapper.containerlessBoundsInParent().height,
                itemOffset = zoom.get() * wrapper.containerlessBoundsInLocal().minY,
                currentItemOffset = wrapper.layoutY
            )
        )

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
        println("PanZoomPanel.handleMouseEvent")
        when (event.eventType) {
            MouseEvent.MOUSE_PRESSED -> sharedEventLock.lock(this) {
                event.consume()

                cursor = Cursor.MOVE
                Action.Pan(
                    clickPosition = event.screenPosition,
                    posAtClick = wrapper.layoutPosition
                )
            }
            MouseEvent.MOUSE_DRAGGED -> sharedEventLock.ifLocked(this, Action::class.java) { current ->
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
            MouseEvent.MOUSE_CLICKED -> sharedEventLock.release(this, Action::class.java) {
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


    private fun handleScroll(pEvent: ScrollEvent) {
        // this intended for mouse-scroll events (event direct == false)
        // the event also gets synthesized from touch events, which we want to ignore as they are handled in handleZoom()
        if (!pEvent.isDirect && pEvent.touchCount <= 0) {

            if (pEvent.isControlDown) {
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
                        sharedEventLock.lock(this) {

                            if (pEvent.deltaY != 0.0) {
                                val direction = if (pEvent.deltaY > 1) ZoomDirection.In else ZoomDirection.Out
                                zoom(direction, pEvent.x, pEvent.y)
                            }
                            pEvent.consume()
                            Action.Zoom
                        }
                    }
                    ScrollEvent.SCROLL_FINISHED -> {
                        sharedEventLock.release(this, Action::class.java) {
                            pEvent.consume()
                        }
                    }
                }
            } else {
                sharedEventLock.ifUnlocked {
                    panTo(wrapper.layoutX + pEvent.deltaX, wrapper.layoutY + pEvent.deltaY)
                    pEvent.consume()
                }
            }
        }
    }

    private fun handleZoom(pEvent: ZoomEvent) {
        when (pEvent.eventType) {
            ZoomEvent.ZOOM_STARTED -> {
                sharedEventLock.lock(this) {
                    pEvent.consume()
                    Action.Zoom
                }
            }
            ZoomEvent.ZOOM_FINISHED -> {
                sharedEventLock.release(this, Action::class.java) {
                    pEvent.consume()
                }
            }
            ZoomEvent.ZOOM -> {
                sharedEventLock.ifLocked(this, Action::class.java) { action ->
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
        wrapper.layoutX = x
        wrapper.layoutY = y
    }

    fun setZoom(pZoom: Double) {
        setZoomAt(pZoom, wrapper.layoutX, wrapper.layoutY)
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
            panTo(wrapper.layoutX + f * pPivotX, wrapper.layoutY + f * pPivotY)
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



    class Wrapper : Region() {
        private var content: Node? = null

        init {
            isManaged = false
//            isMouseTransparent = true
            width = 10.0
            height = 10.0
        }

        fun setContent(node: Node) {
            removeContent()
            content = node
            children.addAll(node)
        }

        fun removeContent() {
            if (content!=null) children.remove(content)
            content=null
        }
    }

    override fun computePrefHeight(width: Double): Double {
        return wrapper.prefHeight(width)
    }

    override fun computePrefWidth(height: Double): Double {
        return wrapper.prefWidth(height)
    }
}