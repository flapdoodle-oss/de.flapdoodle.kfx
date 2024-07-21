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
package de.flapdoodle.kfx.layout.grid

import com.sun.javafx.scene.layout.ScaledMath
import de.flapdoodle.kfx.bindings.css.NumberCssMetaData
import de.flapdoodle.kfx.extensions.constraint
import de.flapdoodle.kfx.css.cssClassName
import de.flapdoodle.kfx.extensions.heightLimits
import de.flapdoodle.kfx.extensions.widthLimits
import de.flapdoodle.kfx.logging.Logging
import de.flapdoodle.kfx.types.AutoArray
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.css.CssMetaData
import javafx.css.Styleable
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.layout.Region
import kotlin.math.max

open class WeightGridPane : Region() {

  private val logger = Logging.logger(WeightGridPane::class)

  internal val horizontalSpace = HORIZONTAL_SPACE.asProperty(0.0) {
    requestLayout()
  }

  internal val verticalSpace = VERTICAL_SPACE.asProperty(0.0) {
    requestLayout()
  }

  internal var rowWeights = AutoArray.empty<Double>()
  internal var columnWeights = AutoArray.empty<Double>()

  init {
    cssClassName("weight-grid-pane")
  }

  fun rowWeight(row: Int, weight: Double) {
    require(row >= 0) { "invalid row: $row" }
    require(weight >= 0.0) { "invalid weight: $weight" }

    rowWeights = rowWeights.set(row, weight)

    requestLayout()
  }

  fun rowWeights(vararg weights: Double) {
    weights.forEachIndexed(::rowWeight)
  }

  @Deprecated(message = "api change", replaceWith = ReplaceWith("rowWeight(a, b)"))
  fun setRowWeight(row: Int, weight: Double) {
    rowWeight(row, weight)
  }

  fun columnWeight(column: Int, weight: Double) {
    require(column >= 0) { "invalid column: $column" }
    require(weight >= 0.0) { "invalid weight: $weight" }

    columnWeights = columnWeights.set(column, weight)

    requestLayout()
  }

  fun columnWeights(vararg weights: Double) {
    weights.forEachIndexed(::columnWeight)
  }

  @Deprecated(message = "api change", replaceWith = ReplaceWith("columnWeight(a, b)"))
  fun setColumnWeight(column: Int, weight: Double) {
    columnWeight(column, weight)
  }

  fun horizontalSpaceProperty() = horizontalSpace
  fun verticalSpaceProperty() = verticalSpace

  public override fun getChildren(): ObservableList<Node> {
    return super.getChildren()
  }

  public fun add(
    node: Node,
    column: Int,
    row: Int,
    horizontalPosition: HPos? = null,
    verticalPosition: VPos? = null
  ): Boolean {
    setPosition(node, column, row, horizontalPosition, verticalPosition)
    return children.add(node)
  }

  private var gridMap: GridMap<Node> = GridMap()

  init {
    children.addListener(ListChangeListener {
      gridMap = gridMap()
      updateState()
    })

    needsLayoutProperty().addListener { observable, oldValue, newValue ->
      gridMap = gridMap()
    }
  }

  private fun gridMap(): GridMap<Node> {
    return GridMap(children
      .filter { it.isManaged }
      .map { it: Node ->
        (it.constraint[GridMap.Pos::class]
          ?: GridMap.Pos(0, 0)) to it
      }.toMap()
    )
  }

  private fun updateState() {
    requestLayout()
  }

  private fun verticalSpace(): Double = limit(verticalSpace.value)
  private fun horizontalSpace(): Double = limit(horizontalSpace.value)

  private fun limit(value: Double, min: Double = 0.0, max: Double = Double.MAX_VALUE): Double {
    require(min <= max) { "min ($min) > max ($max)" }
    return if (min > value) min else if (max < value) max else value
  }

  private fun <T : Any> List<T>.sumWithSpaceBetween(space: Double, selector: (T) -> Double): Double {
    return sumOf(selector) + if (isEmpty()) 0.0 else (size - 1) * space
  }

  private fun <T : Any> List<T>.sumWithSpaceAfter(space: Double, selector: (T) -> Double): Double {
    return sumOf(selector) + size * space
  }

  private fun columnSizes() = gridMap.mapColumns { index, list ->
    val limits = list.map { it.widthLimits() }
    val min = limits.map { it.first }.maxOrNull() ?: 0.0
    val max = Math.max(min, limits.map { it.second }.maxOrNull() ?: Double.MAX_VALUE)

//      require(max >= min) { "invalid min/max for $list -> $min ? $max" }
    WeightedSize(columnWeights.get(index) ?: 1.0, min, max)
  }


  private fun rowSizes() = gridMap.mapRows { index, list ->
    val limits = list.map { it.heightLimits() }
    val min = limits.map { it.first }.maxOrNull() ?: 0.0
    val max = Math.max(min, limits.map { it.second }.maxOrNull() ?: Double.MAX_VALUE)

//      require(max >= min) { "invalid min/max for $list -> $min ? $max" }
    WeightedSize(rowWeights.get(index) ?: 1.0, min, max)
  }

  override fun computeMinWidth(height: Double): Double {
    val ret = columnSizes().sumWithSpaceBetween(horizontalSpace()) { it.min }
    logger.debug { "computeMinWidth: $ret + insets.." }
    return ret + insets.left + insets.right
  }

  override fun computeMinHeight(width: Double): Double {
    val ret = rowSizes().sumWithSpaceBetween(verticalSpace()) { it.min }
    logger.debug { "computeMinHeight: $ret + insets.." }
    return ret + insets.top + insets.bottom
  }

  override fun computePrefWidth(height: Double): Double {
    val ret = gridMap.mapColumns { _, list ->
      list.map {
        val w = max(it.prefWidth(-1.0), it.minWidth(-1.0))
        logger.debug { "computePrefWidth: $it = $w" }
        w
      }.maxOrNull() ?: 0.0
    }.sumWithSpaceBetween(horizontalSpace()) { it }
    return ret + insets.left + insets.right
  }

  override fun computePrefHeight(width: Double): Double {
    val ret = gridMap.mapRows { _, list ->
      list.map {
        val h = max(it.prefHeight(-1.0), it.minHeight(-1.0))
        logger.debug { "computePrefHeight: $it = $h" }
        h
      }.maxOrNull() ?: 0.0
    }.sumWithSpaceBetween(verticalSpace()) { it }
    return ret + insets.top + insets.bottom
  }

  override fun layoutChildren() {
    layoutChildren(layoutX, layoutY, width, height)
  }

  private fun layoutChildren(_contentX: Double, _contentY: Double, _contentWidth: Double, _contentHeight: Double) {
    val top = insets.top
    val right = insets.right
    val left = insets.left
    val bottom = insets.bottom

    var contentX = _contentX + left
    var contentY = _contentY + top
    val contentWidth = _contentWidth - left - right
    val contentHeight = _contentHeight - top - bottom

    contentX = left
    contentY = top
//      println("hspace: ${horizontalSpace.value}")
    val columnSizes = columnSizes()
    val rowSizes = rowSizes()

    val hSpaces = if (columnSizes.isEmpty()) 0.0 else (columnSizes.size - 1) * horizontalSpace()
    val vSpaces = if (rowSizes.isEmpty()) 0.0 else (rowSizes.size - 1) * verticalSpace()

    val colWidths = WeightedSize.distribute(contentWidth - hSpaces, columnSizes)
    val rowHeights = WeightedSize.distribute(contentHeight - vSpaces, rowSizes)

    logger.debug {
      "grid: $contentX, $contentY -> $contentWidth,$contentHeight\n" +
          "-------------------------\n" +
          "columns: $columnSizes\n" +
          "rows: $rowSizes\n" +
          "widths: $colWidths\n" +
          "heights: $rowHeights\n" +
          "-------------------------"
    }

    gridMap.rows().forEachIndexed { r_idx, r ->
      gridMap.columns().forEachIndexed { c_idx, c ->
        val node = gridMap[GridMap.Pos(c, r)]
        if (node != null && node.isManaged) {
          val areaX = contentX + colWidths.subList(0, c_idx).sumWithSpaceAfter(horizontalSpace()) { it }
          val areaY = contentY + rowHeights.subList(0, r_idx).sumWithSpaceAfter(verticalSpace()) { it }

          val areaW = colWidths[c_idx]
          val areaH = rowHeights[r_idx]

          val hPos = node.constraint[HPos::class] ?: HPos.CENTER
          val vPos = node.constraint[VPos::class] ?: VPos.CENTER

          logger.debug { "layoutInArea $node: $areaX, $areaY, $areaW, $areaH" }
          layoutInArea(node, snappedToPixel(areaX), snappedToPixel(areaY), snappedToPixel(areaW), snappedToPixel(areaH), -1.0, hPos, vPos)
        }
      }
    }
  }

  fun snappedToPixel(value: Double): Double {
    return if (isSnapToPixel) ScaledMath.ceil(value, 1.0) else value
  }

  override fun getCssMetaData(): List<CssMetaData<out Styleable, *>> {
    return STYLEABLES
  }

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
      node.parent?.requestLayout()
    }

    fun updatePosition(
      node: Node,
      change: (GridMap.Pos) -> GridMap.Pos
    ) {
      val current = node.constraint[GridMap.Pos::class]
      require(current != null) { "no position found for $node" }
      node.constraint[GridMap.Pos::class] = change(current)
      node.parent?.requestLayout()
    }

    val HORIZONTAL_SPACE: NumberCssMetaData<WeightGridPane> = NumberCssMetaData("horizontal-space", WeightGridPane::horizontalSpace)
    val VERTICAL_SPACE: NumberCssMetaData<WeightGridPane> = NumberCssMetaData("vertical-space", WeightGridPane::verticalSpace)

    val STYLEABLES = emptyList<CssMetaData<out Styleable, *>>() + Region.getClassCssMetaData() + HORIZONTAL_SPACE + VERTICAL_SPACE
  }
}
