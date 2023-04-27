package de.flapdoodle.kfx.bindings

import de.flapdoodle.kfx.Registration
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.WeakListChangeListener

object ObservableLists {

  fun <S, T> syncWith(source: ObservableList<S>, destination: ObservableList<T>, transformation: (S) -> T): Registration {
    destination.setAll(source.map(transformation))
    val listener = WeakListChangeListener(MappingListChangeListener(destination, transformation))
    source.addListener(listener)

    return Registration {
      source.removeListener(listener)
    }
  }
}