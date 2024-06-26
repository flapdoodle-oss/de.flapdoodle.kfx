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

import javafx.beans.binding.ObjectBinding
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList

object DoubleBindings {

  fun with(source: ObservableValue<out Number>) = WithSource(source)

  fun <T> map(source: ObservableValue<out Number>, mapping: (Double) -> T) = Map(source, mapping)
  fun <T> merge(a: ObservableValue<out Number>, b: ObservableValue<out Number>, mapping: (Double, Double) -> T) = Merge2(a, b, mapping)
  fun <T> merge(a: ObservableValue<out Number>, b: ObservableValue<out Number>, c: ObservableValue<out Number>, mapping: (Double, Double, Double) -> T) = Merge3(a, b, c, mapping)
  fun <T> merge(a: ObservableValue<out Number>, b: ObservableValue<out Number>, c: ObservableValue<out Number>, d: ObservableValue<out Number>, mapping: (Double, Double, Double, Double) -> T) =
    Merge4(a, b, c, d, mapping)


  class WithSource(private val source: ObservableValue<out Number>) {
    fun <T> map(mapping: (Double) -> T) = map(source, mapping)
    fun <T> merge(other: ObservableValue<out Number>, mapping: (Double, Double) -> T) = merge(source, other, mapping)
    fun <T> merge(b: ObservableValue<out Number>, c: ObservableValue<out Number>, mapping: (Double, Double, Double) -> T) = merge(source, b, c, mapping)
    fun <T> merge(b: ObservableValue<out Number>, c: ObservableValue<out Number>, d: ObservableValue<out Number>, mapping: (Double, Double, Double, Double) -> T) = merge(source, b, c, d, mapping)

    fun <B> and(other: ObservableValue<out Number>) = WithAB(source, other)
  }

  class WithAB(
    private val a: ObservableValue<out Number>,
    private val b: ObservableValue<out Number>
  ) {
    fun <T> map(mapping: (Double, Double) -> T): Merge2<T> {
      return Merge2(a,b, mapping)
    }
  }

  abstract class Base<T>(
    private vararg val sources: ObservableValue<out Number>
  ) : ObjectBinding<T>() {
    private val dependencies = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(sources))

    init {
      bind(*sources)
    }

    override fun dispose() {
      unbind(*sources)
      super.dispose()
    }

    override fun getDependencies(): ObservableList<*> {
      return dependencies
    }
  }

  class Map<T>(
    private val source: ObservableValue<out Number>,
    private val mapping: (Double) -> T
  ) : Base<T>(source) {
    override fun computeValue(): T {
      return mapping(source.value.toDouble())
    }
  }

  class Merge2<T>(
    private val a: ObservableValue<out Number>,
    private val b: ObservableValue<out Number>,
    private val mapping: (Double, Double) -> T
  ) : Base<T>(a, b) {
    override fun computeValue(): T {
      return mapping(a.value.toDouble(), b.value.toDouble())
    }
  }

  class Merge3<T>(
    private val a: ObservableValue<out Number>,
    private val b: ObservableValue<out Number>,
    private val c: ObservableValue<out Number>,
    private val mapping: (Double, Double, Double) -> T
  ) : Base<T>(a, b, c) {
    override fun computeValue(): T {
      return mapping(a.value.toDouble(), b.value.toDouble(), c.value.toDouble())
    }
  }

  class Merge4<T>(
    private val a: ObservableValue<out Number>,
    private val b: ObservableValue<out Number>,
    private val c: ObservableValue<out Number>,
    private val d: ObservableValue<out Number>,
    private val mapping: (Double, Double, Double, Double) -> T
  ) : Base<T>(a, b, c, d) {
    override fun computeValue(): T {
      return mapping(a.value.toDouble(), b.value.toDouble(), c.value.toDouble(), d.value.toDouble())
    }
  }
}