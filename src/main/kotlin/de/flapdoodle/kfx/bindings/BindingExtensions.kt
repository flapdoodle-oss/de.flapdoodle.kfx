/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
