package de.flapdoodle.kfx.extensions

import de.flapdoodle.kfx.bindings.LazyBoundsProperty
import javafx.beans.InvalidationListener
import javafx.beans.property.ReadOnlyProperty
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.Parent

fun Node.childsInParentBoundsProperty() = ChildsInParentBoundsExtension.childsInParentBoundsPropertyFake(this)

object ChildsInParentBoundsExtension {

    fun bounds(parent: Node): Bounds {
        val bounds: List<Bounds> = when (parent) {
            is Parent -> {
                parent.childrenUnmodifiable.map {
                    val bounds = bounds(it)
                    if (bounds.isEmpty) bounds else parent.localToParent(bounds)
                }
            }
            else -> listOf(parent.boundsInParent)
        }

        val ret = BoundingBoxes.reduce(bounds)
        println("bounds $parent -> $ret ($bounds)")
        return ret
    }

    fun childsInParentBoundsPropertyFake(parent: Node): LazyBoundsProperty<Bounds> {
        val ret = object : LazyBoundsProperty<Bounds>() {
            override fun computeValue(): Bounds {
                println("------------------")
                return bounds(parent)
            }

            override fun getBean(): Any {
                return parent
            }

            override fun getName(): String {
                return "childsInParentBoundsPropertyFake"
            }
        }
        parent.boundsInParentProperty().addListener(InvalidationListener {
            ret.invalidate()
        })
        return ret
    }

    fun childsInParentBoundsProperty(parent: Node): ChildsInParentBoundsProperty {
        return parent.property.computeIfAbsend(ChildsInParentBoundsProperty::class) {
            val ret = ChildsInParentBoundsProperty(parent)
//            parent.boundsInParentProperty()
//                .addListener(InvalidationListener { ret.invalidate() })
            ret
        }
    }

    class ChildsInParentBoundsProperty(val parent: Node) : LazyBoundsProperty<Bounds>() {
        init {
            parent.boundsInParentProperty().addListener(InvalidationListener {
                println("bounds invalid: $parent")
                invalidate()
                when (parent) {
                    is Parent -> parent.childrenUnmodifiable.forEach {
                        childsInParentBoundsProperty(it).invalidate()
                    }
                }
            })
        }

        override fun computeValue(): Bounds {
            val bounds: List<Bounds> = when (parent) {
                is Parent -> {
                    parent.childrenUnmodifiable.map {
                        childsInParentBoundsProperty(it).value
                    }
                }
                else -> listOf(parent.boundsInParent)
            }

            val ret = BoundingBoxes.reduce(bounds)
            println("bounds of $parent: $ret ($bounds)")
            return ret;
        }

        override fun getBean(): Any {
            return parent
        }

        override fun getName(): String {
            return "childsInParentBoundsProperty"
        }

    }
}