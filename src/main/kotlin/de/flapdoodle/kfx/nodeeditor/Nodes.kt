package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.bindings.LazyProperty
import de.flapdoodle.kfx.bindings.mapToDouble
import de.flapdoodle.kfx.extensions.BoundingBoxes
import de.flapdoodle.kfx.extensions.property
import javafx.beans.InvalidationListener
import javafx.beans.property.ReadOnlyProperty
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.shape.Rectangle

object Nodes {
  fun childBoundsRectangle(parent: Parent): Rectangle {
    val wrapperBounds: ReadOnlyProperty<Bounds> = childBoundsInParentProperty(parent)

    val rect = Rectangle().apply {
//            styleClass.addAll("content-background")
      isManaged = false
      isMouseTransparent = true

      xProperty().bind(wrapperBounds.mapToDouble(Bounds::getMinX))
      yProperty().bind(wrapperBounds.mapToDouble(Bounds::getMinY))
      widthProperty().bind(wrapperBounds.mapToDouble(Bounds::getWidth))
      heightProperty().bind(wrapperBounds.mapToDouble(Bounds::getHeight))
    }

    wrapperBounds.addListener(InvalidationListener {
      rect.parent?.requestLayout()
    })

    return rect
  }

  private fun childBoundsInParentProperty(parent: Node): ChildBoundsInParentProperty {
    return parent.property.computeIfAbsend(ChildBoundsInParentProperty::class) {
      ChildBoundsInParentProperty(parent)
    }
  }

  class ChildBoundsInParentProperty(val parent: Node) : LazyProperty<Bounds>() {
    init {
      parent.boundsInParentProperty().addListener(InvalidationListener {
        invalidate()
      })
      parent.boundsInParentProperty().addListener { _, _, _ ->
        invalidate()
      }
    }

    override fun computeValue(): Bounds {
      return if (parent is Parent) {
        parent.localToParent(boundsInParent(parent.childrenUnmodifiable))
      } else BoundingBoxes.empty()
    }

    override fun getBean(): Any {
      return parent
    }

    override fun getName(): String {
      return "childBoundsInParentProperty"
    }

  }

  fun boundsInParent(nodeList: Collection<out Node>): Bounds {
    val nodes = nodeList.filterIsInstance<de.flapdoodle.kfx.nodeeditor.Node>()
    return BoundingBoxes.reduce(nodes.map { it.boundsInParent })
  }
}