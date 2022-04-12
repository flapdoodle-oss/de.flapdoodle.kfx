package de.flapdoodle.kfx.extensions

import de.flapdoodle.kfx.bindings.LazyBoundsProperty
import javafx.beans.InvalidationListener
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.Parent

fun Node.childsInParentBoundsProperty() = ChildsInParentBoundsExtension.childsInParentBoundsProperty(this)
fun Node.childsInParentBounds() = childsInParentBoundsProperty().get()
fun Node.childsInLocalBoundsProperty() = ChildsInParentBoundsExtension.childsInLocalBoundsProperty(this)
fun Node.childsInLocalBounds() = childsInLocalBoundsProperty().get()

fun Node.containerlessBoundsInParent() = ChildsInParentBoundsExtension.containerlessBoundsInParent(this)
fun Node.containerlessBoundsInLocal() = ChildsInParentBoundsExtension.containerlessBoundsInLocal(this)

fun Node.markAsContainer() = ChildsInParentBoundsExtension.markAsContainer(this)

object ChildsInParentBoundsExtension {

    fun isContainer(node: Node): Boolean {
        return node.property[IsContainer::class] != null
    }

    fun markAsContainer(node: Node) {
        node.property[IsContainer::class] = IsContainer
    }

    fun containerlessBoundsInParent(node: Node): Bounds {
        return if (isContainer(node)) {
            val bounds: List<Bounds> = when (node) {
                is Parent -> {
                    node.childrenUnmodifiable.map {
                        val bounds = containerlessBoundsInParent(it)
                        if (bounds.isEmpty) bounds else node.localToParent(bounds)
                    }
                }
                else -> listOf(node.boundsInParent)
            }
            BoundingBoxes.reduce(bounds)
        } else {
            node.boundsInParent
        }
    }

    fun containerlessBoundsInLocal(node: Node): Bounds {
        return if (isContainer(node)) {
            val bounds: List<Bounds> = when (node) {
                is Parent -> {
                    node.childrenUnmodifiable.map {
                        val bounds = containerlessBoundsInParent(it)
                        if (bounds.isEmpty) bounds else node.localToParent(bounds)
                    }
                }
                else -> listOf(node.boundsInLocal)
            }
            BoundingBoxes.reduce(bounds)
        } else {
            node.boundsInLocal
        }
    }

    // TODO:
    //  eigentlich muss man eine Node als Container markieren, damit man die
    //  boundingbox der kinder benutzt ..
    //  ein panel kann sowohl container als auch element sein, an der klasse
    //  kann man es nicht festmachen

    fun boundsInParent(parent: Node): Bounds {
        val bounds: List<Bounds> = when (parent) {
            is Parent -> {
                parent.childrenUnmodifiable.map {
                    val bounds = boundsInParent(it)
                    if (bounds.isEmpty) bounds else parent.localToParent(bounds)
                }
            }
            else -> listOf(parent.boundsInParent)
        }

        val ret = BoundingBoxes.reduce(bounds)
        println("bounds(p) $parent -> $ret ($bounds)")
        return ret
    }

    fun boundsInLocal(parent: Node): Bounds {
        val bounds: List<Bounds> = when (parent) {
            is Parent -> {
                parent.childrenUnmodifiable.map {
                    val bounds = boundsInParent(it)
                    if (bounds.isEmpty) bounds else parent.localToParent(bounds)
                }
            }
            else -> listOf(parent.boundsInLocal)
        }

        val ret = BoundingBoxes.reduce(bounds)
        println("bounds(l) $parent -> $ret ($bounds)")
        return ret
    }

    fun childsInParentBoundsProperty(parent: Node): ChildsInParentBoundsProperty {
        return parent.property.computeIfAbsend(ChildsInParentBoundsProperty::class) {
            ChildsInParentBoundsProperty(parent)
        }
    }

    class ChildsInParentBoundsProperty(val parent: Node) : LazyBoundsProperty<Bounds>() {
        init {
            parent.boundsInParentProperty().addListener(InvalidationListener {
                invalidate()
            })
        }

        override fun computeValue(): Bounds {
            return boundsInParent(parent)
        }

        override fun getBean(): Any {
            return parent
        }

        override fun getName(): String {
            return "childsInParentBoundsProperty"
        }

    }

    fun childsInLocalBoundsProperty(parent: Node): ChildsInLocalBoundsProperty {
        return parent.property.computeIfAbsend(ChildsInLocalBoundsProperty::class) {
            ChildsInLocalBoundsProperty(parent)
        }
    }

    class ChildsInLocalBoundsProperty(val parent: Node) : LazyBoundsProperty<Bounds>() {
        init {
            parent.boundsInParentProperty().addListener(InvalidationListener {
                invalidate()
            })
        }

        override fun computeValue(): Bounds {
            return boundsInLocal(parent)
        }

        override fun getBean(): Any {
            return parent
        }

        override fun getName(): String {
            return "childsInLocalBoundsProperty"
        }

    }

    object IsContainer {

    }
}