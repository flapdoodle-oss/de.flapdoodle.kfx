package de.flapdoodle.kfx.extensions

import de.flapdoodle.kfx.bindings.LazyProperty
import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.Parent

fun Node.childsInParentBoundsProperty() = ChildsInParentBoundsExtension.childsInParentBoundsProperty(this)
fun Node.childsInParentBounds() = childsInParentBoundsProperty().get()
fun Node.childsInLocalBoundsProperty() = ChildsInParentBoundsExtension.childsInLocalBoundsProperty(this)
fun Node.childsInLocalBounds() = childsInLocalBoundsProperty().get()

fun Node.containerlessBoundsInParentProperty() = ChildsInParentBoundsExtension.containerlessBoundsInParentProperty(this)
fun Node.containerlessBoundsInLocalProperty() = ChildsInParentBoundsExtension.containerlessBoundsInLocalProperty(this)
fun Node.containerlessBoundsInParent() = containerlessBoundsInParentProperty().get()
fun Node.containerlessBoundsInLocal() = containerlessBoundsInLocalProperty().get()

fun <T: Node> T.markAsContainer() = ChildsInParentBoundsExtension.markAsContainer(this)

@Deprecated("tooo complicated, of no use")
object ChildsInParentBoundsExtension {

    fun isContainer(node: Node): Boolean {
        return node.property[IsContainer::class] != null
    }

    fun <T: Node> markAsContainer(node: T): T {
        node.property[IsContainer::class] = IsContainer
        return node
    }

    fun containerlessBoundsInParent(node: Node): Bounds {
        return containerlessBounds(node, false)
    }

    fun containerlessBoundsInLocal(node: Node): Bounds {
        return containerlessBounds(node, true)
    }

    private fun containerlessBounds(node: Node, boundsInLocal: Boolean): Bounds {
        return if (isContainer(node)) {
            val bounds: List<Bounds> = when (node) {
                is Parent -> {
                    node.childrenUnmodifiable.map {
                        val bounds = containerlessBoundsInParent(it)
                        if (bounds.isEmpty) bounds else node.localToParent(bounds)
                    }
                }
                else -> {
                    throw IllegalArgumentException("marked as container, but is not a parent: $node")
//                    listOf(if (boundsInLocal) node.boundsInLocal else node.boundsInParent)
                }
            }
            BoundingBoxes.reduce(bounds)
        } else {
//            println("not container: $node")
            if (boundsInLocal) node.boundsInLocal else node.boundsInParent
        }
    }

    fun containerlessBoundsInParentProperty(parent: Node): ContainerlessBoundsInParentProperty {
        return parent.property.computeIfAbsend(ContainerlessBoundsInParentProperty::class) {
            ContainerlessBoundsInParentProperty(parent)
        }
    }

    class ContainerlessBoundsInParentProperty(val parent: Node) : LazyProperty<Bounds>() {
        init {
            parent.boundsInParentProperty().addListener(InvalidationListener {
                invalidate()
            })
            parent.boundsInParentProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                invalidate()
            })
        }

        override fun computeValue(): Bounds {
            return containerlessBoundsInParent(parent)
        }

        override fun getBean(): Any {
            return parent
        }

        override fun getName(): String {
            return "containerlessBoundsInParentProperty"
        }

    }

    fun containerlessBoundsInLocalProperty(parent: Node): ContainerlessBoundsInLocalProperty {
        return parent.property.computeIfAbsend(ContainerlessBoundsInLocalProperty::class) {
            ContainerlessBoundsInLocalProperty(parent)
        }
    }

    class ContainerlessBoundsInLocalProperty(val parent: Node) : LazyProperty<Bounds>() {
        init {
            parent.boundsInLocalProperty().addListener(InvalidationListener {
                invalidate()
            })
            parent.boundsInLocalProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                invalidate()
            })
        }

        override fun computeValue(): Bounds {
            return containerlessBoundsInLocal(parent)
        }

        override fun getBean(): Any {
            return parent
        }

        override fun getName(): String {
            return "containerlessBoundsInLocalProperty"
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

    class ChildsInParentBoundsProperty(val parent: Node) : LazyProperty<Bounds>() {
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

    class ChildsInLocalBoundsProperty(val parent: Node) : LazyProperty<Bounds>() {
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