package de.flapdoodle.kswing.layout.grid

import de.flapdoodle.kfx.layout.grid.GridMap
import de.flapdoodle.kfx.layout.grid.WeightedSize
import de.flapdoodle.kfx.types.AutoArray
import de.flapdoodle.kswing.heightLimits
import de.flapdoodle.kswing.layout.AbstractLayoutManager
import de.flapdoodle.kswing.widthLimits
import javafx.geometry.HPos
import javafx.geometry.VPos
import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.io.Serializable

class WeightGridLayout : AbstractLayoutManager<GridConstraint>, Serializable {
  private var map: Map<Component, GridConstraint> = emptyMap()
  private var rowWeights = AutoArray.empty<Double>()
  private var columnWeights = AutoArray.empty<Double>()
  private var debugLayoutLevel = 0

  fun debugLayoutLevel(level: Int = 0) {
    this.debugLayoutLevel = level
  }

  fun setRowWeight(row: Int, weight: Double) {
    require(row >= 0) { "invalid row: $row" }
    require(weight >= 0.0) { "invalid weight: $weight" }

    rowWeights = rowWeights.set(row, weight)

    // TODO requestLayout()
  }

  fun setColumnWeight(column: Int, weight: Double) {
    require(column >= 0) { "invalid column: $column" }
    require(weight >= 0.0) { "invalid weight: $weight" }

    columnWeights = columnWeights.set(column, weight)

    // TODO requestLayout()
  }

  override fun addComponent(component: Component, attribute: GridConstraint?) {
    if (attribute!=null) {
      synchronized(component.treeLock) {
        map = map + (component to attribute)
      }
    }
  }

  override fun removeLayoutComponent(component: Component) {
    synchronized(component.treeLock) {
      map = map - (component)
    }
  }

  override fun layoutContainer(target: Container) {
//    println("layoutContainer called for "+target)

    synchronized(target.treeLock) {
      val insets = target.insets
      val top = insets.top
      val bottom = target.height - insets.bottom
      val left = insets.left
      val right = target.width - insets.right
      val ltr = target.componentOrientation.isLeftToRight
      require(ltr) {"componentOrientation is not left to right"}

      val contentWidth = right - left
      val contentHeight = bottom - top
      val contentX = left
      val contentY = top

      val gridMap = gridMap(target)

      val columnSizes = columnSizes(gridMap)
      val rowSizes = rowSizes(gridMap)

//      println(columnSizes)

      val hSpaces = if (columnSizes.isEmpty()) 0.0 else (columnSizes.size-1) * horizontalSpace()
      val vSpaces = if (rowSizes.isEmpty()) 0.0 else (rowSizes.size-1) * verticalSpace()

      val colWidths = WeightedSize.distribute(contentWidth - hSpaces, columnSizes)
      val rowHeights = WeightedSize.distribute(contentHeight - vSpaces, rowSizes)

      if (debugLayoutLevel>0) println("----")
      if (debugLayoutLevel>0) println("colWidth: $colWidths")
      if (debugLayoutLevel>0) println("rowHeights: $rowHeights")

      gridMap.rows().forEachIndexed { r_idx, r ->
        gridMap.columns().forEachIndexed { c_idx, c ->
          val node = gridMap[GridMap.Pos(c, r)]
          if (node != null && true /* node.isManaged */) {
            val areaX = contentX + colWidths.subList(0, c_idx).sumWithSpaceAfter(horizontalSpace()) { it }
            val areaY = contentY + rowHeights.subList(0, r_idx).sumWithSpaceAfter(verticalSpace()) { it }

            val areaW = colWidths[c_idx]
            val areaH = rowHeights[r_idx]

            val hPos = map[node]?.horizontalPosition ?: HPos.CENTER
            val vPos = map[node]?.verticalPosition ?: VPos.CENTER

            layoutInArea(node, areaX, areaY, areaW, areaH, hPos, vPos)
//            node.setBounds(areaX.toInt(), areaY.toInt(),areaW.toInt(), areaH.toInt())
          }
        }
      }

    }
  }

  private fun layoutInArea(node: Component, areaX: Double, areaY: Double, areaW: Double, areaH: Double, hPos: HPos, vPos: VPos) {
    val max = node.maximumSize!!
    val width = if (max.width < areaW) max.width else areaW
    val height = if (max.height < areaH) max.height else areaH
    val spaceW = if (max.width < areaW) areaW - max.width else 0.0
    val spaceH = if (max.height < areaH) areaH - max.height else 0.0
    val offsetX = when(hPos) {
      HPos.CENTER -> spaceW / 2.0
      HPos.RIGHT -> spaceW
      HPos.LEFT -> 0.0
    }
    val offsetY = when(vPos) {
      VPos.CENTER -> spaceH / 2.0
      VPos.BOTTOM -> spaceH
      VPos.BASELINE -> spaceH
      VPos.TOP -> 0.0
    }

    if (debugLayoutLevel>1) println("--------------------")
    if (debugLayoutLevel>1) println("${map[node]}")
    if (debugLayoutLevel>1) println("size.min ${node.minimumSize}, size.max: $max")
    if (debugLayoutLevel>1) println("area: ($areaX,$areaY,$areaW,$areaH)")
    if (debugLayoutLevel>1) println("space: $spaceW,$spaceH -> offset: $offsetX, $offsetY, size: $width,$height")
    val x = (areaX + offsetX).toInt()
    val y = (areaY + offsetY).toInt()
    if (debugLayoutLevel>1) println("setBounds($x,$y,$width,$height)")
    node.setBounds(x, y, width.toInt(), height.toInt())
  }

  private fun verticalSpace(): Double = 0.0
  private fun horizontalSpace(): Double = 0.0

  private fun <T : Any> List<T>.sumWithSpaceBetween(space: Double, selector: (T) -> Double): Double {
    return sumByDouble(selector) + if (isEmpty()) 0.0 else (size - 1) * space
  }

  private fun <T : Any> List<T>.sumWithSpaceAfter(space: Double, selector: (T) -> Double): Double {
    return sumByDouble(selector) + size * space
  }

  private fun columnSizes(gridMap: GridMap<Component>) = gridMap.mapColumns { index, list ->
    val limits = list.map { it.widthLimits() }
    val min = limits.map { it.first }.maxOrNull() ?: 0
    val max = Math.max(min, limits.map { it.second }.maxOrNull() ?: Int.MAX_VALUE)

//      require(max >= min) { "invalid min/max for $list -> $min ? $max" }
    WeightedSize(columnWeights.get(index) ?: 1.0, min.toDouble(), max.toDouble())
  }


  private fun rowSizes(gridMap: GridMap<Component>) = gridMap.mapRows { index, list ->
    val limits = list.map { it.heightLimits() }
    val min = limits.map { it.first }.maxOrNull() ?: 0
    val max = Math.max(min, limits.map { it.second }.maxOrNull() ?: Int.MAX_VALUE)

//      require(max >= min) { "invalid min/max for $list -> $min ? $max" }
    WeightedSize(rowWeights.get(index) ?: 1.0, min.toDouble(), max.toDouble())
  }

  private fun gridMap(target: Container): GridMap<Component> {
    return GridMap(target.components
      .filter { it.isValid }
      .map { it: Component -> (map[it] ?: GridConstraint(0, 0)).asPos() to it }
      .toMap())
  }

  override fun minimumLayoutSize(target: Container): Dimension {
    val gridMap = gridMap(target)
    val width = columnSizes(gridMap).sumWithSpaceBetween(horizontalSpace()) { it.min }
    val height = rowSizes(gridMap).sumWithSpaceBetween(verticalSpace()) { it.min }
    return Dimension(width.toInt(), height.toInt())
  }

  override fun preferredLayoutSize(target: Container): Dimension {
    val gridMap = gridMap(target)
    val width = gridMap.mapColumns { _, list ->
      list.map { it.preferredSize.width }.maxOrNull() ?: Int.MAX_VALUE
    }.sumWithSpaceBetween(horizontalSpace()) { it.toDouble() }
    val height = gridMap.mapColumns { _, list ->
      list.map { it.preferredSize.height }.maxOrNull() ?: Int.MAX_VALUE
    }.sumWithSpaceBetween(verticalSpace()) { it.toDouble() }
    return Dimension(width.toInt(), height.toInt())
  }

  override fun maximumLayoutSize(target: Container): Dimension {
    val gridMap = gridMap(target)
    val width = gridMap.mapColumns { _, list ->
      list.map { it.maximumSize.width }.maxOrNull() ?: Int.MAX_VALUE
    }.sumWithSpaceBetween(horizontalSpace()) { it.toDouble() }
    val height = gridMap.mapColumns { _, list ->
      list.map { it.maximumSize.height }.maxOrNull() ?: Int.MAX_VALUE
    }.sumWithSpaceBetween(verticalSpace()) { it.toDouble() }
    return Dimension(width.toInt(), height.toInt())
  }

  override fun getLayoutAlignmentX(target: Container): Float {
    return 0.5f
  }

  override fun getLayoutAlignmentY(target: Container): Float {
    return 0.5f
  }

  override fun invalidateLayout(target: Container) {
//    println("invalidate called for "+target)
//    layoutContainer(target)
//    RuntimeException("invalidate").printStackTrace()
  }
}