package de.flapdoodle.kfx.bindings

import javafx.collections.ObservableList
import javafx.util.Subscription

fun <S, T> ObservableList<T>.syncWith(source: ObservableList<S>, mapping: (S) -> T): Subscription {
  return ObservableLists.syncWith(source, this, mapping)
}