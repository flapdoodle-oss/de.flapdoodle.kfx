package de.flapdoodle.kfx.layout.absolute

import javafx.beans.property.BooleanProperty
import javafx.beans.property.BooleanPropertyBase
import javafx.collections.ObservableList
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.Parent

open class KGroup : Parent {
    init {
        // To initialize the class helper at the begining each constructor of this class
        KGroupHelper.initHelper(this)
    }

    /**
     * Constructs a group.
     */
    constructor() {}

    /**
     * Constructs a group consisting of children.
     *
     * @param children children.
     */
    constructor(vararg children: Node?) {
        getChildren().addAll(*children)
    }

    /**
     * Constructs a group consisting of the given children.
     *
     * @param children children of the group
     * @throws NullPointerException if the specified collection is null
     * @since JavaFX 8.0
     */
    constructor(children: Collection<Node>?) {
        getChildren().addAll(children!!)
    }

    /**
     * Controls whether or not this `Group` will automatically resize any
     * managed resizable children to their preferred sizes
     * during the layout pass. If set to `false`, then the application is
     * responsible for setting the size of this Group's resizable children, otherwise
     * such nodes may end up with a zero width/height and will not be visible.
     * This variable has no effect on content nodes which are not resizable (Shape, Text, etc).
     *
     * @defaultValue true
     */
    private var autoSizeChildren: BooleanProperty? = null
    fun setAutoSizeChildren(value: Boolean) {
        autoSizeChildrenProperty().set(value)
    }

    fun isAutoSizeChildren(): Boolean {
        return if (autoSizeChildren == null) true else autoSizeChildren!!.get()
    }

    fun autoSizeChildrenProperty(): BooleanProperty {
        if (autoSizeChildren == null) {
            autoSizeChildren = object : BooleanPropertyBase(true) {
                override fun invalidated() {
                    requestLayout()
                }

                override fun getBean(): Any {
                    return this@KGroup
                }

                override fun getName(): String {
                    return "autoSizeChildren"
                }
            }
        }
        return autoSizeChildren as BooleanProperty
    }

    /**
     * Gets the list of children of this `Group`.
     * @return the list of children of this `Group`.
     */
    public override fun getChildren(): ObservableList<Node> {
        return super.getChildren()
    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private fun doComputeLayoutBounds(): Bounds? {
        layout() // Needs to done prematurely, as we otherwise don't know the bounds of the children
        return null // helper only requires this node to call layout().
    }

    /**
     * Group defines the preferred width as simply being the width of its layout bounds, which
     * in turn is simply the union of the layout bounds of all of its children. That is,
     * the preferred width is the one that it is at, because a Group cannot be resized.
     *
     * Note: as the layout bounds in autosize Group depend on the Group to be already laid-out,
     * this call will do the layout of the Group if necessary.
     *
     * @param height This parameter is ignored by Group
     * @return The layout bounds width
     */
    override fun prefWidth(height: Double): Double {
        if (isAutoSizeChildren()) {
            layout()
        }
        val result = layoutBounds.width
        return if (java.lang.Double.isNaN(result) || result < 0) 0.0 else result
    }

    /**
     * Group defines the preferred height as simply being the height of its layout bounds, which
     * in turn is simply the union of the layout bounds of all of its children. That is,
     * the preferred height is the one that it is at, because a Group cannot be resized.
     *
     * Note: as the layout bounds in autosize Group depend on the Group to be already laid-out,
     * this call will do the layout of the Group if necessary.
     *
     * @param width This parameter is ignored by Group
     * @return The layout bounds height
     */
    override fun prefHeight(width: Double): Double {
        if (isAutoSizeChildren()) {
            layout()
        }
        val result = layoutBounds.height
        return if (java.lang.Double.isNaN(result) || result < 0) 0.0 else result
    }

    override fun minHeight(width: Double): Double {
        return prefHeight(width)
    }

    override fun minWidth(height: Double): Double {
        return prefWidth(height)
    }

    /**
     * Group implements layoutChildren such that each child is resized to its preferred
     * size, if the child is resizable. Non-resizable children are simply left alone.
     * If [autoSizeChildren][.autoSizeChildrenProperty] is false, then Group does nothing in this method.
     */
    override fun layoutChildren() {
        if (isAutoSizeChildren()) {
            super.layoutChildren()
        }
    }



    companion object {
        init {
            // This is used by classes in different packages to get access to
            // private and package private methods.
            KGroupHelper.setGroupAccessor { node -> (node as KGroup).doComputeLayoutBounds() }
        }
    }
}
