package de.flapdoodle.kfx.types

import java.util.*
import kotlin.reflect.KClass

interface Id<T> {
  data class CountedId<T>(val key: Key<T>, val count: Int): Id<T> {
    override fun toString(): String {
      return "Id($key:$count)"
    }
  }
  data class UniqueId<T>(val id: UUID): Id<T>
  
  companion object {
    fun <T : Any> nextId(type: KClass<T>): Id<T> {
      return nextId(Key.keyOf(type))
    }

    fun <T : Any> nextId(key: Key<T>): Id<T> {
      return CountedId(key, Key.nextId(key))
    }

    fun <T: Any> uuid(): Id<T> = UniqueId(UUID.randomUUID())
  }
}