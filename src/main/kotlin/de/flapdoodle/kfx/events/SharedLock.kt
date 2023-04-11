package de.flapdoodle.kfx.events

class SharedLock<T> {
  var current: Pair<T, Any>? = null

  @Synchronized
  fun tryLock(owner: T, lockFactory: () -> Any) {
    if (current == null) {
      current = owner to lockFactory()
    }
  }

  fun <K: Any> ifLocked(owner: T, lockType: Class<K>, onLocked: Locked<T,T,K>.(K) -> Unit) {
    withLock(owner, lockType, onLocked)
  }

  fun <O: T, K: Any> ifLocked(ownerType: Class<O>, lockType: Class<K>, onLocked: Locked<T,O,K>.(O, K) -> Unit) {
    withLock(ownerType, lockType, onLocked)
  }

  fun <K: Any> tryRelease(owner: T, lockType: Class<K>, onRelease: (K) -> Unit) {
    withLock(owner, lockType) {
      onRelease(it)
      releaseLock()
    }
  }

  @Synchronized
  fun ifUnlocked(onUnlocked: () -> Unit) {
    if (current==null) onUnlocked()
  }

  @Synchronized
  private fun <K: Any> withLock(owner: T, lockType: Class<K>, action: Locked<T,T,K>.(K) -> Unit) {
    current?.run {
      if (first==owner && lockType.isInstance(second)) {
        Locked<T,T,K>(this@SharedLock).action(lockType.cast(second))
      }
    }
  }

  @Synchronized
  private fun <O: T, K: Any> withLock(ownerType: Class<O>, lockType: Class<K>, action: Locked<T,O,K>.(O, K) -> Unit) {
    current?.run {
      if (ownerType.isInstance(first) && lockType.isInstance(second)) {
        Locked<T, O,K>(this@SharedLock).action(ownerType.cast(first), lockType.cast(second))
      }
    }
  }

  @Synchronized
  override fun toString(): String {
    return "SharedLock($current)"
  }

  class Locked<T,O: T,K: Any>(val sharedLock: SharedLock<T>) {
    fun replaceLock(newLock: K) {
      val current = sharedLock.current ?: throw IllegalStateException("current not set")
      sharedLock.current = current.first to newLock
    }

    fun releaseLock() {
      sharedLock.current = null
    }
  }
}