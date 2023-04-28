package de.flapdoodle.kfx.bindings

import de.flapdoodle.kfx.Registration
import de.flapdoodle.kfx.bindings.list.IndexedMappingListChangeListener
import de.flapdoodle.kfx.bindings.list.MappingListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener

object ObservableLists {

  fun <S, T> syncWith(source: ObservableList<S>, destination: ObservableList<T>, transformation: (S) -> T): Registration {
    destination.setAll(source.map(transformation))
    val listener = MappingListChangeListener(destination, transformation)
    source.addListener(listener)

    return Registration {
      source.removeListener(listener)
    }
  }

  fun <S, T> syncWithIndexed(source: ObservableList<S>, destination: ObservableList<T>, transformation: (Int, S) -> T): Registration {
    destination.setAll(source.mapIndexed(transformation))
    val listener = IndexedMappingListChangeListener(destination, transformation)
    source.addListener(listener)

    return Registration {
      source.removeListener(listener)
    }
  }
}