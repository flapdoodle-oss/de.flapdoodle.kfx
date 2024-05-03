package de.flapdoodle.kfx.controls.charts.parts

import com.sun.javafx.scene.layout.ScaledMath
import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.controls.charts.ranges.Range
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.StackLikeRegion
import de.flapdoodle.kfx.types.Direction
import javafx.beans.value.ObservableValue
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path

class TicksPane<T>(
  private val range: ObservableValue<Range<T>>,
  private val ticksWithLevel: ObservableValue<List<Pair<T, Int>>>,
  private val scaleAttributes: ObservableValue<ScaleAttributes>,
  private val direction: Direction
) : Pane() {

  private val path = Path().apply {
    cssClassName("tick")
  }

  private val pathSegments =
    ObjectBindings.merge(range, scaleAttributes, layoutBoundsProperty(), ticksWithLevel) { r, attr, _, ticks ->
      val scaleLength = when (direction) {
        Direction.BOTTOM, Direction.TOP -> width - insets.left - insets.right
        Direction.LEFT, Direction.RIGHT -> -(height - insets.top - insets.bottom)
      }

      val startOffset = snappedToPixel(when(direction) {
        Direction.BOTTOM, Direction.TOP -> insets.left
        Direction.LEFT, Direction.RIGHT -> height - insets.bottom
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

  init {
    cssClassName("ticks")

    path.elements.syncWith(pathSegments) { it }
    children.add(path)

    when (direction) {
      Direction.TOP, Direction.BOTTOM -> {
        minHeightProperty().bind(ObjectBindings.merge(scaleAttributes, insetsProperty()) { s, i -> snappedToPixel(s.length + s.distance + i.top + i.bottom)})
        prefHeightProperty().bind(minWidthProperty())
      }
      Direction.LEFT, Direction.RIGHT -> {
        minWidthProperty().bind(ObjectBindings.merge(scaleAttributes, insetsProperty()) { s, i -> snappedToPixel(s.length + s.distance + i.left + i.right)})
        prefWidthProperty().bind(minWidthProperty())
      }
    }


  }

  private fun snappedToPixel(value: Double): Double {
    return if (isSnapToPixel) ScaledMath.ceil(value, 1.0) else value
  }

//  override fun getCssMetaData(): List<CssMetaData<out Styleable, *>> {
//    return Scale.STYLEABLES
//  }

//  companion object {
//    val SCALE_MIN_SPACING: NumberCssMetaData<TicksPane<out Any>> = NumberCssMetaData("-scale-min-spacing", TicksPane<out Any>::minSpacing)
//    val SCALE_LENGTH: NumberCssMetaData<TicksPane<out Any>> = NumberCssMetaData("-scale-length", TicksPane<out Any>::scaleLength)
//    val SCALE_DISTANCE: NumberCssMetaData<TicksPane<out Any>> = NumberCssMetaData("-scale-distance", TicksPane<out Any>::scaleDistance)
//
//    val STYLEABLES = emptyList<CssMetaData<out Styleable, *>>() + Pane.getClassCssMetaData() + SCALE_MIN_SPACING + SCALE_LENGTH + SCALE_DISTANCE
//  }
}