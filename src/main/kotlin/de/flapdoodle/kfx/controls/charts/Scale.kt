package de.flapdoodle.kfx.controls.charts

import com.sun.javafx.scene.layout.ScaledMath
import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.bindings.css.NumberCssMetaData
import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.controls.charts.parts.ScaleAttributes
import de.flapdoodle.kfx.controls.charts.ranges.Range
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.types.Direction
import javafx.beans.value.ObservableValue
import javafx.css.CssMetaData
import javafx.css.Styleable
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path

class Scale<T>(
  private val range: ObservableValue<Range<T>>,
  private val direction: Direction
): Region(), Styleable {
//    private val spacingNonNull = spacing.mapToDouble { v -> v?.toDouble() ?: 10.0 }

  internal val minSpacing = SCALE_MIN_SPACING.asProperty(5.0) {
    requestLayout()
  }

  internal val scaleLength = SCALE_LENGTH.asProperty(5.0) {
    requestLayout()
  }

  internal val scaleDistance = SCALE_DISTANCE.asProperty(2.0) {
    requestLayout()
  }

  internal val scaleAttributes = ObjectBindings.merge(minSpacing, scaleLength, scaleDistance) { space, len, dist ->
    ScaleAttributes(spacing = space.toDouble(), length = len.toDouble(), distance = dist.toDouble())
  }

  private val path = Path().apply {
    cssClassName("tick")
  }

  private val ticksWithSize = ObjectBindings.merge(range, scaleAttributes, layoutBoundsProperty()) { r, attr, _ ->
    val space = attr.spacing

    val maxTicks = when (direction) {
      Direction.BOTTOM, Direction.TOP -> (width / space).toInt()
      Direction.LEFT, Direction.RIGHT -> (height / space).toInt()
    }

    val allTicks = r.ticks(maxTicks)
    val firstLevelTicks = allTicks.firstOrNull()?.list ?: emptyList()
    val secondLevelTicks = allTicks.getOrNull(1)?.list ?: emptyList()

    firstLevelTicks.map { it to 2 } + secondLevelTicks.map { it to 1 }
  }

  private val pathSegments =
    ObjectBindings.merge(range, scaleAttributes, layoutBoundsProperty(), ticksWithSize) { r, attr, _, ticks ->
      val scaleLength = when (direction) {
        Direction.BOTTOM, Direction.TOP -> width - insets.left - insets.right
        Direction.LEFT, Direction.RIGHT -> height - insets.top - insets.bottom
      }

      val startOffset = snappedToPixel(when(direction) {
        Direction.BOTTOM, Direction.TOP -> insets.left
        Direction.LEFT, Direction.RIGHT -> insets.top
      })

      ticks.flatMap {
        val scaleOffset = r.offset(it.first, scaleLength) + startOffset
        val length = attr.length / it.second.toDouble()

        when (direction) {
          Direction.BOTTOM -> listOf(
            MoveTo(scaleOffset, insets.top),
            LineTo(scaleOffset, length + insets.top)
          )
          Direction.TOP -> listOf(
            MoveTo(scaleOffset, height - length - insets.bottom),
            LineTo(scaleOffset, height - insets.bottom)
          )
          Direction.LEFT -> listOf(
            MoveTo(width - insets.right - length, scaleOffset),
            LineTo(width - insets.right, scaleOffset)
          )
          Direction.RIGHT -> listOf(
            MoveTo(length + insets.left, scaleOffset),
            LineTo(insets.left, scaleOffset)
          )
        }
      }
    }

  private val paths = Pane().apply {
    cssClassName("ticks")

    when (direction) {
      Direction.LEFT -> WeightGridPane.setPosition(this, 1, 0)
      Direction.TOP -> WeightGridPane.setPosition(this, 0, 1)
      Direction.RIGHT -> WeightGridPane.setPosition(this, 0, 0)
      Direction.BOTTOM -> WeightGridPane.setPosition(this, 0, 0)
    }

    minWidthProperty().bind(ObjectBindings.merge(scaleAttributes, insetsProperty()) { s, i -> snappedToPixel(s.length + s.distance + i.left + i.right)})
    minHeightProperty().bind(ObjectBindings.merge(scaleAttributes, insetsProperty()) { s, i -> snappedToPixel(s.length + s.distance + i.top + i.bottom)})

    path.elements.syncWith(pathSegments) { it }
    children.add(path)
  }

  private val labels = Pane().apply {
    cssClassName("labels")

    when (direction) {
      Direction.LEFT -> WeightGridPane.setPosition(this, 0, 0)
      Direction.TOP -> WeightGridPane.setPosition(this, 0, 0)
      Direction.RIGHT -> WeightGridPane.setPosition(this, 1, 0)
      Direction.BOTTOM -> WeightGridPane.setPosition(this, 0, 1)
    }
  }

  private val all = WeightGridPane().apply {
    when (direction) {
      Direction.LEFT -> {
        setColumnWeight(0, 1.0)
        setColumnWeight(1, 0.0)
      }
      Direction.TOP -> {
        setRowWeight(0, 1.0)
        setRowWeight(1, 0.0)
      }
      Direction.RIGHT -> {
        setColumnWeight(0, 0.0)
        setColumnWeight(1, 1.0)
      }
      Direction.BOTTOM -> {
        setRowWeight(0, 0.0)
        setRowWeight(1, 1.0)
      }
    }
  }

  init {
    bindCss("scale")

    all.children.addAll(paths, labels)
    children.addAll(all)
  }

  private fun snappedToPixel(value: Double): Double {
    return if (isSnapToPixel) ScaledMath.ceil(value, 1.0) else value
  }

  override fun getCssMetaData(): List<CssMetaData<out Styleable, *>> {
    return STYLEABLES
  }

  companion object {
    val SCALE_MIN_SPACING: NumberCssMetaData<Scale<out Any>> = NumberCssMetaData("-scale-min-spacing", Scale<out Any>::minSpacing)
    val SCALE_LENGTH: NumberCssMetaData<Scale<out Any>> = NumberCssMetaData("-scale-length", Scale<out Any>::scaleLength)
    val SCALE_DISTANCE: NumberCssMetaData<Scale<out Any>> = NumberCssMetaData("-scale-distance", Scale<out Any>::scaleDistance)

    val STYLEABLES = emptyList<CssMetaData<out Styleable, *>>() + Pane.getClassCssMetaData() + SCALE_MIN_SPACING + SCALE_LENGTH + SCALE_DISTANCE
  }
}