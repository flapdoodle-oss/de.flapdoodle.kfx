package de.flapdoodle.kfx.bindings

import de.flapdoodle.kfx.Registration
import javafx.collections.ObservableList

fun <S, T> ObservableList<T>.syncWith(source: ObservableList<S>, mapping: (S) -> T): Registration {
  return ObservableLists.syncWith(source, this, mapping)
}