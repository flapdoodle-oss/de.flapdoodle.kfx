package de.flapdoodle.kfx.sampler

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.stage.Stage

class WeightGridPaneSampler : Application() {

    override fun start(stage: Stage) {
        stage.scene = Scene(Button("click me"))
        stage.show()
    }
}