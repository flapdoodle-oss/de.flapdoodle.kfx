package de.flapdoodle.kfx.bindings

import javafx.beans.property.Property
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

fun <T> Property<T>.bindTo(source: ObservableValue<T>) {
  bind(source)
}

fun <T> ObservableValue<T>.storeTo(destination: Property<T>) {
  destination.bindTo(this)
}
