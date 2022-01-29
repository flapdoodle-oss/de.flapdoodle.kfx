package de.flapdoodle.kfx.layout.virtual

import javafx.scene.Node

class SharedEventLock {
    var current: Pair<Node, Any>? = null

    fun lock(owner: Node, stateFactory: () -> Any) {
        if (current == null) {
            current = owner to stateFactory()
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
}