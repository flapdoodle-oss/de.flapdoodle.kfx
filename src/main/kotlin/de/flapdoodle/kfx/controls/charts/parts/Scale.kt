package de.flapdoodle.kfx.controls.charts.parts

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.bindings.css.NumberCssMetaData
import de.flapdoodle.kfx.controls.charts.ranges.Range
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.StackLikeRegion
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.types.Direction
import javafx.beans.value.ObservableValue
import javafx.css.CssMetaData
import javafx.css.Styleable
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.layout.Region

class Scale<T>(
  private val range: ObservableValue<Range<T>>,
  private val direction: Direction
): StackLikeRegion(), Styleable {
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

  private val ticksPane = TicksPane(range, ticksWithSize, scaleAttributes, direction).apply {
    val ticks = this
    when (direction) {
      Direction.LEFT -> WeightGridPane.setPosition(ticks, 1, 0)
      Direction.TOP -> WeightGridPane.setPosition(ticks, 0, 1)
      Direction.RIGHT -> WeightGridPane.setPosition(ticks, 0, 0)
      Direction.BOTTOM -> WeightGridPane.setPosition(ticks, 0, 0)
    }
  }

  private val labels = Pane().apply {
    cssClassName("labels")

    val lab = this

    when (direction) {
      Direction.LEFT -> WeightGridPane.setPosition(lab, 0, 0)
      Direction.TOP -> WeightGridPane.setPosition(lab, 0, 0)
      Direction.RIGHT -> WeightGridPane.setPosition(lab, 1, 0)
      Direction.BOTTOM -> WeightGridPane.setPosition(lab, 0, 1)
    }

    lab.children.addAll(Label("foo"))
  }

  private val all = WeightGridPane().apply {
    when (direction) {
      Direction.LEFT -> {
        setColumnWeight(0, 1.0)
        setColumnWeight(1, 0.0)
      }
      Direction.TOP -> {
        setRowWeight(0, 1.0)
        setRowWeight(1, 0.01)
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

    all.children.addAll(ticksPane, labels)
    children.addAll(all)
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