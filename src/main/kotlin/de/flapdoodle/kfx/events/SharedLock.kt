package de.flapdoodle.kfx.events

class SharedLock<T> {
  var current: Pair<T, Any>? = null

  @Synchronized
  fun tryLock(owner: T, lockFactory: () -> Any) {
    if (current == null) {
      current = owner to lockFactory()
    }
  }

  fun <K> ifLocked(owner: T, lockType: Class<K>, onLocked: (K) -> Unit) {
    withLock(owner, lockType, onLocked)
  }

  fun <O: T, K> ifLocked(ownerType: Class<O>, lockType: Class<K>, onLocked: (O, K) -> Unit) {
    withLock(ownerType, lockType, onLocked)
  }

  fun <K> replaceLock(owner: T, lockType: Class<K>, onLocked: (K) -> Any) {
    withLock(owner, lockType) { lock ->
      current = owner to onLocked(lock)
    }
  }

  fun <O: T, K> replaceLock(ownerType: Class<O>, lockType: Class<K>, onLocked: (O, K) -> Any) {
    withLock(ownerType, lockType) { owner, lock ->
      current = owner to onLocked(owner, lock)
    }
  }

  fun <K> tryRelease(owner: T, lockType: Class<K>, onRelease: (K) -> Unit) {
    withLock(owner, lockType) {
      onRelease(it)
      current = null
    }
  }

  @Synchronized
  fun ifUnlocked(onUnlocked: () -> Unit) {
    if (current==null) onUnlocked()
  }

  @Synchronized
  private fun <K> withLock(owner: T, lockType: Class<K>, action: (K) -> Unit) {
    current?.run {
      if (first==owner && lockType.isInstance(second)) {
        action(lockType.cast(second))
      }
    }
  }

  @Synchronized
  private fun <O: T, K> withLock(ownerType: Class<O>, lockType: Class<K>, action: (O, K) -> Unit) {
    current?.run {
      if (ownerType.isInstance(first) && lockType.isInstance(second)) {
        action(ownerType.cast(first), lockType.cast(second))
      }
    }
  }

  @Synchronized
  override fun toString(): String {
    return "SharedLock($current)"
  }
}