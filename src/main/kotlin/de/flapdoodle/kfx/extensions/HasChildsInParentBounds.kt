package de.flapdoodle.kfx.extensions

import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectPropertyBase
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.ObservableList
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.Parent

////fun Node.childBoundsInParentProperty(): ReadOnlyObjectProperty<javafx.geometry.Bounds> {
////    //this.property.
////}
//
//class ChildBoundsInParent(val node: Parent) : ReadOnlyObjectPropertyBase<Bounds>() {
//    override fun get(): Bounds {
//        val childrenUnmodifiable = node.getChildrenUnmodifiable()
//        val bounds: List<Bounds> = childrenUnmodifiable.map {
//            return when (it) {
//                is HasChildsInParentBounds -> it.childBoundsInParent()
//                else -> it.boundsInParent
//            }
//        }
//
//        return BoundingBoxes.reduce(bounds)
//    }
//
//    override fun getBean(): Any {
//        return node
//    }
//
//    override fun getName(): String {
//        return "childBoundsInParentProperty"
//    }
//}


interface HasChildsInParentBounds {
    fun getChildrenUnmodifiable(): ObservableList<Node>

    fun childBoundsInParent(): Bounds {
        val childrenUnmodifiable = getChildrenUnmodifiable()
        val bounds: List<Bounds> = childrenUnmodifiable.map { it ->
            return when (it) {
                is HasChildsInParentBounds -> it.childBoundsInParent()
                else -> it.boundsInParent
            }
        }

        return BoundingBoxes.reduce(bounds)
    }
}