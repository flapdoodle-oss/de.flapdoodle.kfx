package de.flapdoodle.kfx.bindings

import javafx.beans.binding.ObjectBinding
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList

object ObjectBindings {

  fun <T> defaultIfNull(sources: ObservableValue<T?>, fallback: ObservableValue<T>) = DefaultIfNull(sources, fallback)
  fun <S> with(source: ObservableValue<S>) = WithSource(source)

  fun <S, T> map(source: ObservableValue<S>, mapping: (S) -> T) = Map(source, mapping)
  fun <A, B, T> merge(a: ObservableValue<A>, b: ObservableValue<B>, mapping: (A, B) -> T) = Merge2(a, b, mapping)
  fun <A, B, C, T> merge(a: ObservableValue<A>, b: ObservableValue<B>, c: ObservableValue<C>, mapping: (A, B, C) -> T) = Merge3(a, b, c, mapping)
  fun <A, B, C, D, T> merge(a: ObservableValue<A>, b: ObservableValue<B>, c: ObservableValue<C>, d: ObservableValue<D>, mapping: (A, B, C, D) -> T) =
    Merge4(a, b, c, d, mapping)


  class WithSource<S>(private val source: ObservableValue<S>) {
    fun <T> map(mapping: (S) -> T) = map(source, mapping)
    fun <B, T> merge(other: ObservableValue<B>, mapping: (S, B) -> T) = merge(source, other, mapping)
    fun <B, C, T> merge(b: ObservableValue<B>, c: ObservableValue<C>, mapping: (S, B, C) -> T) = merge(source, b, c, mapping)
    fun <B, C, D, T> merge(b: ObservableValue<B>, c: ObservableValue<C>, d: ObservableValue<D>, mapping: (S, B, C, D) -> T) = merge(source, b, c, d, mapping)
  }

  abstract class Base<T>(
    private vararg val sources: ObservableValue<*>
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

  class Map<S, T>(
    private val source: ObservableValue<S>,
    private val mapping: (S) -> T
  ) : Base<T>(source) {
    override fun computeValue(): T {
      return mapping(source.value)
    }
  }

  class Merge2<A, B, T>(
    private val a: ObservableValue<A>,
    private val b: ObservableValue<B>,
    private val mapping: (A, B) -> T
  ) : Base<T>(a, b) {
    override fun computeValue(): T {
      return mapping(a.value, b.value)
    }
  }

  class Merge3<A, B, C, T>(
    private val a: ObservableValue<A>,
    private val b: ObservableValue<B>,
    private val c: ObservableValue<C>,
    private val mapping: (A, B, C) -> T
  ) : Base<T>(a, b, c) {
    override fun computeValue(): T {
      return mapping(a.value, b.value, c.value)
    }
  }

  class Merge4<A, B, C, D, T>(
    private val a: ObservableValue<A>,
    private val b: ObservableValue<B>,
    private val c: ObservableValue<C>,
    private val d: ObservableValue<D>,
    private val mapping: (A, B, C, D) -> T
  ) : Base<T>(a, b, c, d) {
    override fun computeValue(): T {
      return mapping(a.value, b.value, c.value, d.value)
    }
  }

  class DefaultIfNull<T>(
    private val source: ObservableValue<T?>,
    private val fallback: ObservableValue<T>
  ) : Base<T>(source, fallback) {
    override fun computeValue(): T {
      return source.value ?: fallback.value
    }
  }
}