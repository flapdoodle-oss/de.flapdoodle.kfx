package de.flapdoodle.kfx.sampler

import de.flapdoodle.kfx.clone.BoxFactory
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.layout.virtual.PanningWindow
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage

class PanningWindowsSampler: Application() {

    override fun start(stage: Stage) {
        val graphEditorProperties = BoxFactory.sampleProperties()

        val template = object : de.flapdoodle.kfx.clone.PanningWindow() {
            init {
                setEditorProperties(graphEditorProperties)
                setContent(blue())
                WeightGridPane.setPosition(this, 0, 0)
            }
        }

        val testee = PanningWindow().apply {
//                children.add(blueBox())
            WeightGridPane.setPosition(this, 1, 0)
        }

        stage.scene = Scene(WeightGridPane().apply {
            children.add(template)
            children.add(testee)

            setColumnWeight(0, 1.0)
            setColumnWeight(1, 1.0)
            setRowWeight(0, 1.0)
        }, 800.0, 400.0)
        stage.show()
    }

    fun blue() : Region {
        return object : Region() {
            init {
                children.addAll(Pane().apply {
                    this.minWidth = 400.0
                    this.minHeight =200.0
                    this.border = Border(BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii(1.0), BorderWidths(1.0)))
                })
            }
        }
    }

    fun blueBox(): Rectangle {
        return Rectangle(-10.0, 20.0, 200.0, 200.0).apply {
            fill=Color.BLUE
        }
    }
}