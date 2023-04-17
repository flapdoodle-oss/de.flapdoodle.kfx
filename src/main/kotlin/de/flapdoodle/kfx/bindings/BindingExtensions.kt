package de.flapdoodle.kfx.bindings

import javafx.beans.value.ObservableValue

fun <T> ObservableValue<T?>.defaultIfNull(other: ObservableValue<T>): ObjectBindings.DefaultIfNull<T> {
  return ObjectBindings.defaultIfNull(this,other)
}

fun <S, T> ObservableValue<S>.map(mapping: (S) -> T): ObjectBindings.Map<S, T> {
  return ObjectBindings.map(this, mapping)
}

fun <S> ObservableValue<S>.mapToDouble(mapping: (S) -> Double): ObjectBindings.Map<S, Double> {
  return ObjectBindings.map(this, mapping)
}

fun ObservableValue<Number>.mapToDouble(): DoubleBindings.Map<Double> {
  return DoubleBindings.map(this) { it }
}

fun <A, B> ObservableValue<A>.and(other: ObservableValue<B>): ObjectBindings.WithAB<A, B> {
  return ObjectBindings.with(this).and(other)
}

