package de.flapdoodle.kfx.controls.charts

import com.sun.javafx.scene.layout.ScaledMath
import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.bindings.css.NumberCssMetaData
import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.types.Direction
import javafx.beans.property.DoublePropertyBase
import javafx.beans.value.ObservableValue
import javafx.css.CssMetaData
import javafx.css.Styleable
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path

class Scale<T>(
  private val range: ObservableValue<Range<T>>,
  private val chartSpacing: DoublePropertyBase,
  private val direction: Direction
): Pane(), Styleable {
//    private val spacingNonNull = spacing.mapToDouble { v -> v?.toDouble() ?: 10.0 }

  internal val scaleSpacing = SCALE_SPACING.asProperty(5.0) {
    requestLayout()
  }

  internal val scaleLength = SCALE_LENGTH.asProperty(5.0) {
    requestLayout()
  }

  internal val scaleDistance = SCALE_DISTANCE.asProperty(2.0) {
    requestLayout()
  }

  internal val scaleAttributes = ObjectBindings.merge(scaleSpacing, scaleLength, scaleDistance) { space, len, dist ->
    ScaleAttributes(spacing = space.toDouble(), length = len.toDouble(), distance = dist.toDouble())
  }

  private val path = Path().apply {
    cssClassName("ticks")
//    stroke = Color.BLACK
    
    //TODO das ist vielleicht nicht so richtig
    minWidthProperty().bind(chartSpacing.multiply(4))
    minHeightProperty().bind(chartSpacing.multiply(4))
  }

  private val pathSegments =
    ObjectBindings.merge(range, chartSpacing, scaleAttributes, layoutBoundsProperty()) { r, chartSpaceNumber, attr, _ ->
      // TODO will be zero soon
      val spaceAroundChart = chartSpaceNumber.toDouble()

      val space = attr.spacing
      
      val maxTicks = when (direction) {
        Direction.BOTTOM, Direction.TOP -> (width / space).toInt()
        Direction.LEFT, Direction.RIGHT -> (height / space).toInt()
      }

      val ticks = r.ticks(maxTicks).firstOrNull()?.list ?: emptyList()

      val scaleLength = when (direction) {
        Direction.BOTTOM, Direction.TOP -> width - insets.left - insets.right - spaceAroundChart * 2.0
        Direction.LEFT, Direction.RIGHT -> height - insets.top - insets.bottom - spaceAroundChart * 2.0
      }

      val startOffset = snappedToPixel(when(direction) {
        Direction.BOTTOM, Direction.TOP -> insets.left + spaceAroundChart
        Direction.LEFT, Direction.RIGHT -> insets.top + spaceAroundChart
      })



      ticks.flatMap {
        val scaleOffset = r.offset(it, scaleLength) + startOffset
        when (direction) {
          Direction.BOTTOM -> listOf(
            MoveTo(scaleOffset, attr.distance),
            LineTo(scaleOffset, attr.distance + attr.length)
          )
          Direction.TOP -> listOf(
            MoveTo(scaleOffset, attr.distance),
            LineTo(scaleOffset, attr.distance + attr.length)
          )
          Direction.LEFT -> listOf(
            MoveTo(attr.distance, scaleOffset),
            LineTo(attr.distance + attr.length, scaleOffset)
          )
          Direction.RIGHT -> listOf(
            MoveTo(attr.distance, scaleOffset),
            LineTo(attr.distance + attr.length, scaleOffset)
          )
        }
      }
    }

  init {
    bindCss("scale")

    path.elements.syncWith(pathSegments) { it }
    children.addAll(path)
  }

  private fun snappedToPixel(value: Double): Double {
    return if (isSnapToPixel) ScaledMath.ceil(value, 1.0) else value
  }

  override fun getCssMetaData(): List<CssMetaData<out Styleable, *>> {
    return STYLEABLES
  }

  companion object {
    val SCALE_SPACING: NumberCssMetaData<Scale<out Any>> = NumberCssMetaData("-scale-spacing", Scale<out Any>::scaleSpacing)
    val SCALE_LENGTH: NumberCssMetaData<Scale<out Any>> = NumberCssMetaData("-scale-length", Scale<out Any>::scaleLength)
    val SCALE_DISTANCE: NumberCssMetaData<Scale<out Any>> = NumberCssMetaData("-scale-distance", Scale<out Any>::scaleDistance)

    val STYLEABLES = emptyList<CssMetaData<out Styleable, *>>() + Pane.getClassCssMetaData() + SCALE_SPACING + SCALE_LENGTH + SCALE_DISTANCE
  }
}