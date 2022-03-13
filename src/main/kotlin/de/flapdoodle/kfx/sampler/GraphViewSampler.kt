package de.flapdoodle.kfx.sampler

import de.flapdoodle.kfx.events.SharedEventLock
import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.extensions.size
import de.flapdoodle.kfx.graph.nodes.*
import de.flapdoodle.kfx.layout.decoration.Nodes
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.layout.layer.LayerPane
import de.flapdoodle.kfx.layout.virtual.PanZoomPanel
import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.stage.Stage

class GraphViewSampler : Application() {

    override fun start(stage: Stage) {
        val sharedEventLock = SharedEventLock()

        val testee = PanZoomPanel(sharedEventLock).apply {
            setContent(sample(sharedEventLock))
            WeightGridPane.setPosition(this, 1, 0)
        }

        stage.scene = Scene(WeightGridPane().apply {
//            children.add(template)
            children.add(testee)

            setColumnWeight(0, 1.0)
            setColumnWeight(1, 1.0)
            setRowWeight(0, 1.0)
        }, 400.0, 400.0)
        stage.scene.stylesheets.add(getStyleResource())
        stage.show()
    }

    fun getStyleResource(): String {
        val resource = PanningWindowsSampler::class.java.getResource("panning-windows-sampler.css") ?: throw IllegalArgumentException("stylesheet not found")
        return resource.toExternalForm() ?: throw IllegalArgumentException("could not get external form for $resource")
    }


    fun sample(sharedEventLock: SharedEventLock): LayerPane<String> {
        val nodeLayer = "Nodes"
        val connectionLayer = "Connections"
        return LayerPane(setOf(nodeLayer, connectionLayer)).apply {
            val resizablePane = NonResizablePane().apply {
                layoutPosition = Point2D(10.0, 10.0)
            }

            val movables = Movables(sharedEventLock) { node ->
                when (node) {
                    is NonResizablePane -> Movable(node, NonResizablePane::size) { Resizeable(it::resizeTo) }
                    else -> null
                }
            }
            movables.addAll(resizablePane, NonResizablePane().apply {
                layoutX = 80.0
                children.addAll(Button("click me").apply {
                    onAction = EventHandler {
                        println("click...")
                    }
                })
            })
            addAll(nodeLayer, movables)

            val start = Connector(Rectangle(10.0, 10.0, Color.DARKGRAY)).apply {
                relocate(10.0, 20.0)
            }
            val boundingBox = Nodes.boundingBox()
            Nodes.attachBoundingBox(start, boundingBox)

            val end = Connector(Circle(10.0, Color.DARKGRAY)).apply {
                relocate(70.0, 30.0)
            }

            addAll(connectionLayer, start)
            addAll(connectionLayer, end)
            addAll(connectionLayer, boundingBox)
            addAll(connectionLayer, ConnectionPath(start, end))

            val connectableConnectors = ConnectableConnectors { connectors, start ->
                if (start!=null) connectors.filter { it !== start }
                else connectors
            }
            
            val connections = Connections(connectableConnectors, sharedEventLock).apply {
//                addConnectorAt(Point2D(130.0, 140.0)).apply { angle(30.0) }
//                addConnectorAt(Point2D(80.0, 160.0)).apply { angle(200.0) }

                addSocket(Connector().apply {
                    relocate(130.0, 140.0)
                    angle(30.0)
                })

                addSocket(Connector().apply {
                    relocate(80.0, 160.0)
                    angle(200.0)
                })
            }

            addAll(connectionLayer, connections)
        }
    }

}