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
package de.flapdoodle.kfx.layout.grid

import de.flapdoodle.kfx.extensions.constraint
import de.flapdoodle.kfx.types.AutoArray
import javafx.collections.ObservableList
import javafx.css.CssMetaData
import javafx.css.SimpleStyleableDoubleProperty
import javafx.css.Styleable
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Control

class WeightGridPane : Control() {

    companion object {
        fun setPosition(
            node: Node,
            column: Int,
            row: Int,
            horizontalPosition: HPos? = null,
            verticalPosition: VPos? = null
        ) {
            node.constraint[GridMap.Pos::class] = GridMap.Pos(column, row)
            node.constraint[HPos::class] = horizontalPosition
            node.constraint[VPos::class] = verticalPosition
        }

        fun updatePosition(
            node: Node,
            change: (GridMap.Pos) -> GridMap.Pos
        ) {
            val current = node.constraint[GridMap.Pos::class]
            require(current != null) { "no position found for $node" }
            node.constraint[GridMap.Pos::class] = change(current)
        }
    }

    internal val horizontalSpace = object : SimpleStyleableDoubleProperty(WeightGridPaneStyle.CSS_HSPACE, this, "hspace") {
        override fun invalidated() {
            requestLayout()
        }
    }

    internal val verticalSpace = object : SimpleStyleableDoubleProperty(WeightGridPaneStyle.CSS_VSPACE, this, "vspace") {
        override fun invalidated() {
            requestLayout()
        }
    }

    internal var rowWeights = AutoArray.empty<Double>()
    internal var columnWeights = AutoArray.empty<Double>()

    init {
        styleClass.addAll("weight-grid-pane")
        stylesheets += javaClass.getResource("WeightGridPane.css").toExternalForm();
    }

    private val skin = WeightGridPaneSkin(this)
    override fun createDefaultSkin() = skin

    fun setRowWeight(row: Int, weight: Double) {
        require(row >= 0) { "invalid row: $row" }
        require(weight >= 0.0) { "invalid weight: $weight" }

        rowWeights = rowWeights.set(row, weight)

        requestLayout()
    }

    fun setColumnWeight(column: Int, weight: Double) {
        require(column >= 0) { "invalid column: $column" }
        require(weight >= 0.0) { "invalid weight: $weight" }

        columnWeights = columnWeights.set(column, weight)

        requestLayout()
    }

    fun horizontalSpaceProperty() = horizontalSpace
    fun verticalSpaceProperty() = verticalSpace

//  override fun getUserAgentStylesheet(): String {
//    //return Style().base64URL.toExternalForm()
//    return stylesheets.joinToString(separator = ";") + Style().base64URL.toExternalForm()
//  }

    public override fun getChildren(): ObservableList<Node> {
        return super.getChildren()
    }

    override fun getControlCssMetaData(): List<CssMetaData<out Styleable, *>> {
        return WeightGridPaneStyle.CONTROL_CSS_META_DATA
    }

}