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
package de.flapdoodle.kfx.sampler

import de.flapdoodle.kfx.extensions.markAsContainer
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.Control

open class AbsolutePane : Control() {
    init {
        markAsContainer()
    }

    private val skin = AbsolutePaneSkin(this)
    override fun createDefaultSkin() = skin

    public override fun getChildren(): ObservableList<Node> {
        return super.getChildren()
    }

    override fun computePrefWidth(height: Double): Double {
        layout()

        val result = layoutBounds.width
        return if (java.lang.Double.isNaN(result) || result < 0) 0.0 else result
    }

    override fun computePrefHeight(width: Double): Double {
        layout()

        val result = layoutBounds.height
        return if (java.lang.Double.isNaN(result) || result < 0) 0.0 else result
    }

    override fun computeMinWidth(height: Double): Double {
        return prefWidth(height)
    }

    override fun computeMinHeight(width: Double): Double {
        return prefHeight(width)
    }
}