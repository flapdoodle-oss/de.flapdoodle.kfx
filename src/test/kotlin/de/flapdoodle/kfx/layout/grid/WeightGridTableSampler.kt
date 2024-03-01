package de.flapdoodle.kfx.layout.grid

import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.stage.Stage

class WeightGridTableSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {

      val model = SimpleObjectProperty(listOf("Anna", "Bert", "Susi"))
      val columns = listOf(
        WeightGridTable.Column(nodeFactory = ::Label),
        WeightGridTable.Column(weight = 2.0, nodeFactory = { TextField(it) })
      )
      stage.scene = Scene(WeightGridTable(model, columns).apply {
        verticalSpace().set(5.0)
        horizontalSpace().set(10.0)
//
//        children.add(Button("test").apply {
//          minWidth = 20.0
//          maxWidth = 100.0
//          WeightGridPane.setPosition(this, 0, 0)
//        })
//        children.add(Button("test-1").apply {
//          WeightGridPane.setPosition(this, 1, 0, horizontalPosition = HPos.RIGHT)
//        })
//        children.add(Button("test-11").apply {
//          WeightGridPane.setPosition(this, 1, 1)
//          maxHeight = 100.0
//        })
//
//        setColumnWeight(0, 1.0)
//        setColumnWeight(1, 2.0)
//        setRowWeight(0, 4.0)
//        setRowWeight(1, 1.0)
      })
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