/*
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
package de.flapdoodle.kfx.layout.backgrounds

import de.flapdoodle.kfx.bindings.bindTo
import de.flapdoodle.kfx.bindings.mapToDouble
import de.flapdoodle.kfx.extensions.containerlessBoundsInParentProperty
import javafx.beans.InvalidationListener
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Region
import javafx.scene.shape.Rectangle

object Bounds {
    // TODO does it work?
    fun childBoundsRectangle(parent: Parent): Rectangle {
        val wrapperBounds: ReadOnlyProperty<Bounds> = parent.containerlessBoundsInParentProperty()

        val rect = Rectangle().apply {
//            styleClass.addAll("content-background")
            isManaged = false
            isMouseTransparent = true

            xProperty().bind(wrapperBounds.mapToDouble(Bounds::getMinX))
            yProperty().bind(wrapperBounds.mapToDouble(Bounds::getMinY))
            widthProperty().bind(wrapperBounds.mapToDouble(Bounds::getWidth))
            heightProperty().bind(wrapperBounds.mapToDouble(Bounds::getHeight))
        }

        wrapperBounds.addListener(InvalidationListener {
            rect.parent?.requestLayout()
        })

        return rect
    }

    fun boundsRectangle(node: Node): Rectangle {
        val wrapperBounds: ReadOnlyObjectProperty<Bounds> = node.boundsInParentProperty()

        val rect = Rectangle().apply {
//            styleClass.addAll("content-background")
            isManaged = false
            isMouseTransparent = true

            xProperty().bind(wrapperBounds.mapToDouble(Bounds::getMinX))
            yProperty().bind(wrapperBounds.mapToDouble(Bounds::getMinY))
            widthProperty().bind(wrapperBounds.mapToDouble(Bounds::getWidth))
            heightProperty().bind(wrapperBounds.mapToDouble(Bounds::getHeight))
        }

        wrapperBounds.addListener(InvalidationListener {
            rect.parent?.requestLayout()
        })

        return rect
    }

    fun sizeRectangle(region: Region): Rectangle = Rectangle().apply {
        widthProperty().bind(region.widthProperty())
        heightProperty().bind(region.heightProperty())
    }

  fun sizeRectangle(region: ObjectProperty<Bounds>): Rectangle {
    return Rectangle().apply {
      xProperty().bind(region.map(Bounds::getMinX))
      yProperty().bind(region.map(Bounds::getMinY))
      widthProperty().bind(region.map(Bounds::getWidth))
      heightProperty().bindTo(region.map(Bounds::getHeight))
    }
  }
}