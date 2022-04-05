package de.flapdoodle.kfx.layout.absolute

import com.sun.javafx.scene.ParentHelper
import com.sun.javafx.sg.prism.NGNode
import com.sun.javafx.util.Utils
import javafx.geometry.Bounds
import javafx.scene.Group
import javafx.scene.Node

open class KGroupHelper : ParentHelper() {
    override fun createPeerImpl(node: Node): NGNode {
        return super.createPeerImpl(node)
    }

    override fun computeLayoutBoundsImpl(node: Node): Bounds {
        groupAccessor!!.doComputeLayoutBounds(node)
        return super.computeLayoutBoundsImpl(node)
    }

    fun interface KGroupAccessor {
        fun doComputeLayoutBounds(node: Node?): Bounds?
    }

    companion object {
        private var instance: KGroupHelper? = null
        private var groupAccessor: KGroupAccessor? = null

        init {
            instance = KGroupHelper()
            Utils.forceInit(Group::class.java)
        }

        fun initHelper(group: KGroup?) {
            setHelper(group, instance)
        }

        fun setGroupAccessor(newAccessor: KGroupAccessor?) {
            check(groupAccessor == null)
            groupAccessor = newAccessor
        }
    }
}
