package de.flapdoodle.kfx.sampler

import de.flapdoodle.kfx.extensions.property
import de.flapdoodle.kfx.layout.backgrounds.Bounds
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.layout.virtual.PanZoomPanel
import javafx.application.Application
import javafx.collections.ObservableList
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage

class ComponentsBehaviorSampler : Application() {

    override fun start(stage: Stage) {
        val overview = Overview()
        val bounds = ShowBounds(overview)

        stage.scene = Scene(StackPane().apply {
            children.add(overview)
            children.add(bounds)
        }, 600.0, 400.0)
        stage.show()
    }

    class ShowBounds(val overview: Overview) : Pane() {
        init {
            overview.childrenUnmodifiable.forEach { node ->
                children.add(Rectangle().apply {
                    isManaged = false
                    isMouseTransparent = true

                    val meta = node.property[Meta::class] ?: Meta(0,0)

                    layoutX = (meta.col * _panelW) + _padding
                    layoutY = (meta.row * _panelH) + _padding
                    width = _panelW - (_padding * 2)
                    height = _panelH - (_padding * 2)
                    
                    fill = Color.TRANSPARENT
                    stroke = Color.RED
                })
                children.add(Bounds.boundsRectangle(node).apply {
                    fill = Color.rgb(255,255,255,0.5)
                })                
            }
        }
    }

    class Overview : Control() {
        init {
            children.add(sample(Pane(), Meta(0,0)) { fillStuffInto("Pane", it.children) })
            children.add(sample(RegionAdapter(), Meta(1,0)) { fillStuffInto("Region", it.c()) })
            children.add(sample(StackPane(), Meta(0,1)) { fillStuffInto("SPane", it.children) })
            children.add(sample(Group(), Meta(1,1)) { fillStuffInto("Group", it.children) })
            children.add(sample(WeightGridPane(), Meta(2,0)) { fillStuffInto("WeightG", it.children) })
            children.add(sample(PanZoomPanel(), Meta(2,1)) { pz ->
                val content = Pane()
                pz.setContent(content)
                fillStuffInto("PanZ", content.children)
            })
        }

        private val skin = OverviewSkin(this)
        override fun createDefaultSkin() = skin
    }

    class RegionAdapter : Region() {
        fun c(): ObservableList<Node> {
            return super.getChildren()
        }
    }

    class OverviewSkin(
        private val control: Overview
    ) : SkinBase<Overview>(control) {
        override fun layoutChildren(contentX: Double, contentY: Double, contentWidth: Double, contentHeight: Double) {
            super.layoutChildren(contentX, contentY, contentWidth, contentHeight)

            children.forEach { node ->
                val meta = node.property[Meta::class] ?: Meta(0,0)

                val x = _panelW * meta.col + _padding
                val y = _panelH * meta.row + _padding
                val w = _panelW - _padding*2
                val h = _panelH - _padding*2

                layoutInArea(node, x, y, w, h, -1.0, HPos.CENTER, VPos.CENTER)
            }
        }
    }

    companion object {
        val _panelW = 200.0
        val _panelH = 200.0
        val _padding = 50.0

        fun <T: Node> sample(node: T, meta: Meta, action: (T) -> Unit ): T {
            action(node)
            node.property[Meta::class] = meta
            return node
        }

        fun fillStuffInto(name: String, list: ObservableList<Node>) {
            list.add(Button(name))
            list.add(Button("X").apply {
                layoutX = 50.0
                layoutY = 50.0
            })
            list.add(Rectangle(30.0, 30.0).apply {
                layoutX = 90.0
                layoutY = 90.0
                fill = Color.BLACK
            })
        }
    }

    data class Meta(val col: Int, val row: Int)
}