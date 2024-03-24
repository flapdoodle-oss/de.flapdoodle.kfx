package de.flapdoodle.kfx.bindings

import de.flapdoodle.kfx.bindings.list.IndexedMappingListChangeListener
import de.flapdoodle.kfx.bindings.list.List2ListChangeListener
import de.flapdoodle.kfx.bindings.list.List2ObservableListChangeListener
import de.flapdoodle.kfx.bindings.list.MappingListChangeListener
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableValue
import javafx.collections.ObservableList
import javafx.util.Subscription

object ObservableLists {

  fun <S, T> syncWith(source: ObservableValue<List<S>>, destination: WritableValue<List<T>>, transformation: (S) -> T): Subscription {
    destination.value = source.value.map(transformation)
    val listener = List2ListChangeListener(destination, transformation)
    source.addListener(listener)
    return Subscription {
      source.removeListener(listener)
    }
  }

  fun <S, T> syncWith(source: ObservableValue<List<S>>, destination: ObservableList<T>, transformation: (S) -> T): Subscription {
    destination.setAll(source.value.map(transformation))
    val listener = List2ObservableListChangeListener(destination, transformation)
    source.addListener(listener)
    return Subscription {
      source.removeListener(listener)
    }
  }

  fun <S, T> syncWith(source: ObservableList<S>, destination: ObservableList<T>, transformation: (S) -> T): Subscription {
    destination.setAll(source.map(transformation))
    val listener = MappingListChangeListener(destination, transformation)
    source.addListener(listener)

    return Subscription {
      source.removeListener(listener)
    }
  }

  fun <S, T> syncWithIndexed(source: ObservableList<S>, destination: ObservableList<T>, transformation: (Int, S) -> T): Subscription {
    destination.setAll(source.mapIndexed(transformation))
    val listener = IndexedMappingListChangeListener(destination, transformation)
    source.addListener(listener)

    return Subscription {
      source.removeListener(listener)
    }
  }
}