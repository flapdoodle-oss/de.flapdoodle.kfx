package de.flapdoodle.kfx.layout.layer

import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.layout.Region

class LayerPane<T>(val layers: Set<T>) : Region() {
    private val layerPanes = layers.associateWith { Layer() }

    init {
        layerPanes.forEach { (id, pane) ->
            children.add(pane)
        }
    }

    fun addAll(layerId: T, vararg elements: Node) {
        val layer = layerPanes[layerId]
        require(layer!=null) {"unknown layer $layerId"}
        layer.children.addAll(elements)
    }

    class Layer : Region() {

        public override fun getChildren(): ObservableList<Node> {
            return super.getChildren()
        }
    }
}