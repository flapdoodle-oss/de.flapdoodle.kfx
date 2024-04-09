package de.flapdoodle.kfx.controls.charts

import de.flapdoodle.kfx.extensions.bindCss
import javafx.beans.NamedArg
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.chart.Axis
import javafx.scene.chart.LineChart
import javafx.scene.paint.Color

// max 8 colors
class ColorableLineChart<X, Y>(
  @NamedArg("xAxis") xAxis: Axis<X>,
  @NamedArg("yAxis") yAxis: Axis<Y>,
  @NamedArg("data") data: ObservableList<Series<X, Y>>,
  private val colorMapping: (Series<X, Y>) -> Color
) : LineChart<X, Y>(xAxis, yAxis, data) {
  init {
    bindCss("colorable-line-chart")

    setColors(data)
    
    data.addListener(ListChangeListener {
      setColors(it.list)
    })
  }

  private fun setColors(list: ObservableList<out Series<X, Y>>) {
    if (list.size > 8) {
      IllegalArgumentException("more than 8 series, color mapping does not work as expected")
        .printStackTrace()
    }
    var colors = ""
    list.forEachIndexed { index, serie ->
      val color = colorMapping(serie)
      colors += "-fx-chart-color-$index: ${asCss(color)};\n"
    }
    style = colors
  }

  private fun asCss(color: Color): String {
    return String.format("#%02X%02X%02X", (color.red * 255).toInt(), (color.green * 255).toInt(), (color.blue * 255).toInt())
  }

}