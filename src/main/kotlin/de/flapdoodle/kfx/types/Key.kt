package de.flapdoodle.kfx.types

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

interface Key<K: Any> {
  fun type(): KClass<K>

  data class ClassKey<K: Any>(val clazz: KClass<K>) : Key<K> {
    override fun toString(): String {
      return "Key(${clazz.qualifiedName})"
    }

    override fun type(): KClass<K> = clazz
  }

  companion object {
    private val keyIdGeneratorMap = ConcurrentHashMap<Key<out Any>, AtomicInteger>()

    private fun nextIdFor(key: Key<out Any>): Int {
      return keyIdGeneratorMap.getOrPut(key) { AtomicInteger() }.incrementAndGet()
    }

    fun <T: Any> nextId(type:KClass<T>): Int {
      return nextId(keyOf(type))
    }

    fun <T: Any> nextId(key: Key<T>): Int {
      return nextIdFor(key)
    }

    fun <T: Any> keyOf(type: KClass<T>): Key<T> {
      return ClassKey(type)
    }
  }
}