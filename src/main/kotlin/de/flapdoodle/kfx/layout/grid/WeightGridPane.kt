package de.flapdoodle.kfx.layout.grid

import com.sun.javafx.scene.layout.ScaledMath
import de.flapdoodle.kfx.extensions.constraint
import de.flapdoodle.kfx.extensions.heightLimits
import de.flapdoodle.kfx.extensions.widthLimits
import de.flapdoodle.kfx.types.AutoArray
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.css.SimpleStyleableDoubleProperty
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node

class WeightGridPane : javafx.scene.layout.Region() {

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
  }

  internal val horizontalSpace = object : SimpleStyleableDoubleProperty(WeightGridControlStyle.CSS_HSPACE, this, "hspace") {
    override fun invalidated() {
      requestLayout()
    }
  }

  internal val verticalSpace = object : SimpleStyleableDoubleProperty(WeightGridControlStyle.CSS_VSPACE, this, "vspace") {
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

//  private val skin = WeightGridPaneSkin(this)
//  override fun createDefaultSkin() = skin

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

//  override fun getControlCssMetaData(): List<CssMetaData<out Styleable, *>> {
//    return WeightGridControlStyle.CONTROL_CSS_META_DATA
//  }


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
    val width = columnSizes().sumWithSpaceBetween(horizontalSpace()) { it.min }
    return width + insets.left + insets.right
  }

  override fun computeMinHeight(width: Double): Double {
    val ret = rowSizes().sumWithSpaceBetween(verticalSpace()) { it.min }
    return ret + insets.top + insets.bottom
  }

  override fun computePrefWidth(height: Double): Double {
    val ret = gridMap.mapColumns { _, list ->
      list.map { it.prefWidth(-1.0) }.maxOrNull() ?: 0.0
    }.sumWithSpaceBetween(horizontalSpace()) { it }
    return ret + insets.left + insets.right
  }

  override fun computePrefHeight(width: Double): Double {
    val ret = gridMap.mapRows { _, list ->
      list.map { it.prefHeight(-1.0) }.maxOrNull() ?: 0.0
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
//    println("grid: $contentX, $contentY -> $contentWidth,$contentHeight")
//      println("-------------------------")

//      println("hspace: ${horizontalSpace.value}")
    val columnSizes = columnSizes()
    val rowSizes = rowSizes()

    val hSpaces = if (columnSizes.isEmpty()) 0.0 else (columnSizes.size - 1) * horizontalSpace()
    val vSpaces = if (rowSizes.isEmpty()) 0.0 else (rowSizes.size - 1) * verticalSpace()

//      println("columns")
//      columnSizes.forEach { println(it) }
//      println("rows")
//      rowSizes.forEach { println(it) }

    val colWidths = WeightedSize.distribute(contentWidth - hSpaces, columnSizes)
    val rowHeights = WeightedSize.distribute(contentHeight - vSpaces, rowSizes)

//      println("widths: $colWidths")
//      println("heights: $rowHeights")
//      println("-------------------------")

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

//          println("layoutInArea $node: $areaX, $areaY, $areaW, $areaH")
          layoutInArea(node, snappedToPixel(areaX), snappedToPixel(areaY), snappedToPixel(areaW), snappedToPixel(areaH), -1.0, hPos, vPos)
        }
      }
    }
  }

  fun snappedToPixel(value: Double): Double {
    return if (isSnapToPixel) ScaledMath.ceil(value, 1.0) else value
  }
}
