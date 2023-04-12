package de.flapdoodle.kfx.events

class SharedLock<T> {
  var current: Pair<T, Any>? = null

  @Synchronized
  fun tryLock(owner: T, lockFactory: () -> Any) {
    if (current == null) {
      current = owner to lockFactory()
    }
  }

  fun <K: Any> ifLocked(owner: T, lockType: Class<K>, onLocked: (Lock<T,T,K>) -> Unit) {
    withLock(owner, lockType, onLocked)
  }

  fun <O: T, K: Any> ifLocked(ownerType: Class<O>, lockType: Class<K>, onLocked: (Lock<T,O,K>) -> Unit) {
    withLock(ownerType, lockType, onLocked)
  }

  fun <K: Any> tryRelease(owner: T, lockType: Class<K>, onRelease: (K) -> Unit) {
    withLock(owner, lockType) {
      onRelease(it.value)
      it.releaseLock()
    }
  }

  @Synchronized
  fun ifUnlocked(onUnlocked: () -> Unit) {
    if (current==null) onUnlocked()
  }

  @Synchronized
  private fun <K: Any> withLock(owner: T, lockType: Class<K>, action: (Lock<T,T,K>) -> Unit) {
    current?.run {
      if (first==owner && lockType.isInstance(second)) {
        action(Lock(first, lockType.cast(second), this@SharedLock))
      }
    }
  }

  @Synchronized
  private fun <O: T, K: Any> withLock(ownerType: Class<O>, lockType: Class<K>, action: (Lock<T,O,K>) -> Unit) {
    current?.run {
      if (ownerType.isInstance(first) && lockType.isInstance(second)) {
        action(Lock(ownerType.cast(first), lockType.cast(second), this@SharedLock))
      }
    }
  }

  @Synchronized
  override fun toString(): String {
    return "SharedLock($current)"
  }

  data class Lock<T, O: T, K: Any>(
    val owner: O,
    val value: K,
    private val sharedLock: SharedLock<T>
  ) {
    fun replaceLock(newLock: K) {
      val current = sharedLock.current ?: throw IllegalStateException("current not set")
      sharedLock.current = current.first to newLock
    }

    fun releaseLock() {
      sharedLock.current = null
    }
  }
}