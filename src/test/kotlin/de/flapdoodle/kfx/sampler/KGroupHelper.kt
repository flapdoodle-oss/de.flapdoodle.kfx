/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.kfx.sampler

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
