package de.flapdoodle.kfx.bindings

import javafx.beans.binding.ObjectBinding
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList

object DoubleBindings {

  fun with(source: ObservableValue<Number>) = WithSource(source)

  fun <T> map(source: ObservableValue<Number>, mapping: (Double) -> T) = Map(source, mapping)
  fun <T> merge(a: ObservableValue<Number>, b: ObservableValue<Number>, mapping: (Double, Double) -> T) = Merge2(a, b, mapping)
  fun <T> merge(a: ObservableValue<Number>, b: ObservableValue<Number>, c: ObservableValue<Number>, mapping: (Double, Double, Double) -> T) = Merge3(a, b, c, mapping)
  fun <T> merge(a: ObservableValue<Number>, b: ObservableValue<Number>, c: ObservableValue<Number>, d: ObservableValue<Number>, mapping: (Double, Double, Double, Double) -> T) =
    Merge4(a, b, c, d, mapping)


  class WithSource(private val source: ObservableValue<Number>) {
    fun <T> map(mapping: (Double) -> T) = map(source, mapping)
    fun <T> merge(other: ObservableValue<Number>, mapping: (Double, Double) -> T) = merge(source, other, mapping)
    fun <T> merge(b: ObservableValue<Number>, c: ObservableValue<Number>, mapping: (Double, Double, Double) -> T) = merge(source, b, c, mapping)
    fun <T> merge(b: ObservableValue<Number>, c: ObservableValue<Number>, d: ObservableValue<Number>, mapping: (Double, Double, Double, Double) -> T) = merge(source, b, c, d, mapping)

    fun <B> and(other: ObservableValue<Number>) = WithAB(source, other)
  }

  class WithAB(
    private val a: ObservableValue<Number>,
    private val b: ObservableValue<Number>
  ) {
    fun <T> map(mapping: (Double, Double) -> T): Merge2<T> {
      return Merge2(a,b, mapping)
    }
  }

  abstract class Base<T>(
    private vararg val sources: ObservableValue<Number>
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
    private val source: ObservableValue<Number>,
    private val mapping: (Double) -> T
  ) : Base<T>(source) {
    override fun computeValue(): T {
      return mapping(source.value.toDouble())
    }
  }

  class Merge2<T>(
    private val a: ObservableValue<Number>,
    private val b: ObservableValue<Number>,
    private val mapping: (Double, Double) -> T
  ) : Base<T>(a, b) {
    override fun computeValue(): T {
      return mapping(a.value.toDouble(), b.value.toDouble())
    }
  }

  class Merge3<T>(
    private val a: ObservableValue<Number>,
    private val b: ObservableValue<Number>,
    private val c: ObservableValue<Number>,
    private val mapping: (Double, Double, Double) -> T
  ) : Base<T>(a, b, c) {
    override fun computeValue(): T {
      return mapping(a.value.toDouble(), b.value.toDouble(), c.value.toDouble())
    }
  }

  class Merge4<T>(
    private val a: ObservableValue<Number>,
    private val b: ObservableValue<Number>,
    private val c: ObservableValue<Number>,
    private val d: ObservableValue<Number>,
    private val mapping: (Double, Double, Double, Double) -> T
  ) : Base<T>(a, b, c, d) {
    override fun computeValue(): T {
      return mapping(a.value.toDouble(), b.value.toDouble(), c.value.toDouble(), d.value.toDouble())
    }
  }
}