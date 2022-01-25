package de.flapdoodle.kfx.sampler

import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.application.Application
import javafx.geometry.HPos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.stage.Stage

class WeightGridPaneSampler : Application() {

    override fun start(stage: Stage) {
        stage.scene = Scene(WeightGridPane().apply {
            children.add(Button("test").apply {
                minWidth = 20.0
                maxWidth = 100.0
                WeightGridPane.setPosition(this, 0, 0)
            })
            children.add(Button("test-1").apply {
                WeightGridPane.setPosition(this, 1, 0, horizontalPosition = HPos.RIGHT)
            })
            children.add(Button("test-11").apply {
                WeightGridPane.setPosition(this, 1, 1)
                maxHeight = 100.0
            })

            setColumnWeight(0, 1.0)
            setColumnWeight(1, 2.0)
            setRowWeight(0, 4.0)
            setRowWeight(1, 1.0)
        })
        stage.show()
    }
}