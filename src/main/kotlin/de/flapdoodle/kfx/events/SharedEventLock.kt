/**
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
package de.flapdoodle.kfx.events

import javafx.scene.Node

class SharedEventLock {
    var current: Pair<Node, Any>? = null

    fun lock(owner: Node, stateFactory: () -> Any) {
        if (current == null) {
            current = owner to stateFactory()
        }
    }

    fun <K> replaceLocked(owner: Node, stateFactory: () -> Any) {
        current?.let { active ->
            if (active.first == owner) {
                current = owner to stateFactory()
            }
        }
    }

    private fun <K> currentState(owner: Node, clazz: Class<K>): K? {
        return current?.run {
            return if (first == owner) clazz.cast(second) else null
        }
    }

    fun <K> ifLocked(owner: Node, clazz: Class<K>, onLocked: (K) -> Unit) {
        currentState(owner, clazz)?.let(onLocked)
    }

    fun <N: Node, K> ifAnyLocked(ownerType: Class<N>, clazz: Class<K>, onLocket: (N, K) -> Unit) {
        current?.let { (owner,state) ->
            if (ownerType.isInstance(owner) && clazz.isInstance(state)) {
                onLocket(ownerType.cast(owner), clazz.cast(state))
            }
        }
    }

    fun ifUnlocked(onUnlocked: () -> Unit) {
        if (current==null) onUnlocked()
    }

    fun <K> release(owner: Node, clazz: Class<K>, onRelease: (K) -> Unit) {
        current?.run {
            if (first==owner) {
                onRelease(clazz.cast(second))
                current=null
            }
        }
    }

    override fun toString(): String {
        return "SharedEventLock($current)"
    }
}