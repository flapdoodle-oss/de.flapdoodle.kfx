package de.flapdoodle.kfx.bindings

import de.flapdoodle.kfx.Registration
import javafx.beans.InvalidationListener
import javafx.beans.binding.ObjectBinding
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.*

object ObservableMaps {
  fun <S, K, V> syncWith(source: ObservableList<S>, destination: ObservableMap<K, V>, keyOf: (S) -> K, valueOf: (S) -> V): Registration {
    source.forEach {
      destination[keyOf(it)] = valueOf(it)
    }
    val listener = MapKVListChangeListener(destination, keyOf, valueOf)
    source.addListener(listener)

    return Registration {
      source.removeListener(listener)
    }
  }

  fun <K, S, T> syncWith(source: ObservableMap<K, S>, destination: ObservableMap<K, T>, transformation: (S) -> T): Registration {
    source.forEach { (key, value) ->
      destination[key] = transformation(value)
    }
    val listener = MappingMapChangeListener(destination, transformation)
    source.addListener(listener)

    return Registration {
      source.removeListener(listener)
    }
  }

  fun <K, V : ObservableValue<T>, T> valueOf(source: ObservableMap<K, V>, key: K): ObservableValue<T?> {
    return ValueOf(source, key)
  }

  internal class ValueOf<K, V : ObservableValue<T>, T>(
    private val map: ObservableMap<K, V>,
    private val key: K
  ) : ObjectBinding<T?>() {

    private val dependencies = FXCollections.observableArrayList(map)
    private var property: ObservableValue<T>? = null

    private val invalidationFromProperty = InvalidationListener {
      invalidate()
    }

    private val changeListener = ChangeListener<T> { _, _, _ ->
      invalidate()
    }

    init {
      bind(map)
    }

    override fun dispose() {
      unbind(map)
      super.dispose()
    }

    override fun onInvalidating() {
      super.onInvalidating()
    }

    override fun getDependencies(): ObservableList<*> {
      return dependencies
    }

    override fun computeValue(): T? {
      val newProperty = map[key]
      if (newProperty != null) {
        if (newProperty != property) {
          property?.removeListener(invalidationFromProperty)
          property?.removeListener(changeListener)

          newProperty.addListener(invalidationFromProperty)
          newProperty.addListener(changeListener)

          property = newProperty
        }
      } else {
        property?.removeListener(invalidationFromProperty)
        property?.removeListener(changeListener)
        property = null
      }

      return property?.value
    }
  }
}