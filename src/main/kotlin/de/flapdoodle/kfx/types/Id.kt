package de.flapdoodle.kfx.types

import kotlin.reflect.KClass

interface Id<T: Any> {
  fun type(): KClass<T>

  data class CountedId<T: Any>(val key: Key<T>, val count: Int): Id<T> {
    override fun toString(): String {
      return "Id($key:$count)"
    }

    override fun type() = key.type()
  }

  companion object {
    fun <T : Any> nextId(type: KClass<T>): Id<T> {
      return nextId(Key.keyOf(type))
    }

    fun <T : Any> nextId(key: Key<T>): Id<T> {
      return CountedId(key, Key.nextId(key))
    }
  }
}