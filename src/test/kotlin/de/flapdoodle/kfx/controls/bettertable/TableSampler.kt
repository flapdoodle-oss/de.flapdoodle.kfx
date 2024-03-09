package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.extensions.withAnchors
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

class TableSampler {
  class Sample : Application() {
    override fun start(stage: Stage) {
      val content = AnchorPane(Table<String>().withAnchors(all = 10.0))
      stage.scene = Scene(content, 800.0, 600.0)
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