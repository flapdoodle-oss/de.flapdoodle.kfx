package de.flapdoodle.kfx.controls.charts

import de.flapdoodle.kfx.bindings.toObservable
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage

class LinearChartSampler {
  class Sample : Application() {
    override fun start(stage: Stage) {

      val x = NumberAxis().apply {
        tickUnit = 1.0
      }
      val y = NumberAxis().apply {
        tickUnit = 1.0
      }

      val colorMap = mapOf(
        "a" to Color.RED,
        "b" to Color.ORANGE,
        "c" to Color.PURPLE,
      )

      val series = listOf(
        XYChart.Series(
          "a", listOf(
            XYChart.Data<Number, Number>(1.0, 1.0),
            XYChart.Data<Number, Number>(2.0, 1.2),
            XYChart.Data<Number, Number>(10.0, 3.4),
            XYChart.Data<Number, Number>(12.0, 2.8),
          ).toObservable()
        ).apply {
//          node.style = "-fx-stroke: red"
          nodeProperty().addListener { observable, oldValue, newValue ->
            if (newValue != null) {
//              newValue.style = "-fx-stroke: blue"
//              newValue.cssClassName("xyz-zyx")
            }
          }
        },
        XYChart.Series(
          "b", listOf(
            XYChart.Data<Number, Number>(1.4, 4.0),
            XYChart.Data<Number, Number>(3.0, -1.2),
            XYChart.Data<Number, Number>(9.0, 0.2),
          ).toObservable()
        )
      ).toObservable()

      val content = ColorableLineChart<Number, Number>(x, y, series) {
        it -> colorMap[it.name] ?: Color.BLACK
      }

      val all = BorderPane().apply {
        center = content
        bottom = VBox().apply {
          children.add(Button("+").apply {
            onAction = EventHandler {
              series.add(XYChart.Series(
                "c", listOf(
                  XYChart.Data<Number, Number>(1.0, 4.0),
                  XYChart.Data<Number, Number>(5.0, -2.0),
                ).toObservable()
              ))
            }
          })
        }
      }

      stage.scene = Scene(all, 800.0, 600.0)
      stage.show()
    }
  }


  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Sample::class.java, *args)
    }
  }
}