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

import de.flapdoodle.kfx.bindings.mapToDouble
import javafx.beans.InvalidationListener
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.geometry.Bounds
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.ScrollBar
import javafx.scene.layout.*
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Scale

open class PanningWindow(
    sharedEventLock: SharedEventLock = SharedEventLock()
) : Region() {
    private val wrapper = Wrapper()
    private val panZoomHandler = PanZoomHandler(this, sharedEventLock)

    private val scrollX = ScrollBar()
    private val scrollY = ScrollBar()

    init {
        styleClass.addAll("panning-window")
        stylesheets += javaClass.getResource("PanningWindow.css").toExternalForm();

        val wrapperBounds: ReadOnlyObjectProperty<Bounds> = wrapper.boundsInParentProperty()

        wrapperBounds.addListener(InvalidationListener {
            requestLayout()
        })

        children.add(Rectangle().apply {
            styleClass.addAll("content-background")
            isManaged = false
            isMouseTransparent = true

            xProperty().bind(wrapperBounds.mapToDouble(Bounds::getMinX))
            yProperty().bind(wrapperBounds.mapToDouble(Bounds::getMinY))
            widthProperty().bind(wrapperBounds.mapToDouble(Bounds::getWidth))
            heightProperty().bind(wrapperBounds.mapToDouble(Bounds::getHeight))
        })

        val scale = Scale()
        scale.xProperty().bind(panZoomHandler.zoomProperty())
        scale.yProperty().bind(panZoomHandler.zoomProperty())
        wrapper.transforms.add(scale)
        children.add(wrapper)

        scrollX.orientation = Orientation.HORIZONTAL
        scrollX.valueProperty().bindBidirectional(panZoomHandler.translateXProperty())
//        scrollX.styleClass.add("graph-editor-scroll-bar") //$NON-NLS-1$
        scrollY.orientation = Orientation.VERTICAL
        scrollY.valueProperty().bindBidirectional(panZoomHandler.translateYProperty())
        children.addAll(scrollX,scrollY)

        val clip = Rectangle()
        clip.widthProperty().bind(widthProperty())
        clip.heightProperty().bind(heightProperty())
        setClip(clip)
    }

    fun setContent(node: Node) {
        wrapper.setContent(node)
    }

    override fun layoutChildren() {
        super.layoutChildren()

        scrollX.setBounds(
            ScrollBounds.of(
                windowSize = width,
                itemSize = wrapper.boundsInParent.width,
                itemOffset = panZoomHandler.zoom() * wrapper.boundsInLocal.minX,
                currentItemOffset = panZoomHandler.translateX()
            )
        )

        scrollY.setBounds(
            ScrollBounds.of(
                windowSize = height,
                itemSize =  wrapper.boundsInParent.height,
                itemOffset = panZoomHandler.zoom() * wrapper.boundsInLocal.minY,
                currentItemOffset = panZoomHandler.translateY()
            )
        )

        wrapper.relocate(panZoomHandler.translateX(), panZoomHandler.translateY())

        val w = scrollY.width
        val h = scrollX.height

        scrollX.resizeRelocate(0.0, snapPositionY(height - h), snapSizeX(width - w), h)
        scrollY.resizeRelocate(snapPositionX(width - w), 0.0, w, snapSizeY(height - h))
    }

    fun panTo(x: Double, y: Double) {
        panZoomHandler.panTo(x,y)
    }

    fun zoom(zoom: Double) {
        panZoomHandler.setZoom(zoom)
    }

    fun zoomAt(zoom: Double, x: Double, y: Double) {
        panZoomHandler.setZoomAt(zoom, x, y)
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
}