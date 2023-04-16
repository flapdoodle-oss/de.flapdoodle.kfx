package de.flapdoodle.kfx.bindings

import javafx.beans.binding.ObjectBinding
import javafx.beans.value.ObservableDoubleValue
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList

fun <S, T> ObservableValue<S>.map(mapping: (S) -> T): Bindings.Mapped<S, T> {
    return Bindings.map(this, mapping)
}

fun <S> ObservableValue<S>.mapToDouble(mapping: (S) -> Double): Bindings.Mapped<S, Double> {
    return Bindings.map(this, mapping)
}

fun <A, B> ObservableValue<A>.and(other: ObservableValue<B>): Bindings.ToMerge2<A, B> {
    return Bindings.ToMerge2(this, other)
}

object Bindings {

    fun <S, T> map(source: ObservableValue<S>, mapping: (S) -> T): Mapped<S, T> {
        return Mapped(source, mapping)
    }

    fun <S, T> mapList(source: ObservableList<S>, mapping: (List<S>) -> T): MappedList<S, T> {
        return MappedList(source, mapping)
    }

    fun <A, B, T> map(a: ObservableValue<A>, b: ObservableValue<B>, mapping: (A, B) -> T): Merge2<A, B, T> {
        return Merge2(a,b,mapping)
    }

    fun <T> mapDouble(a: ObservableDoubleValue, b: ObservableDoubleValue, mapping: (Double, Double) -> T): Merge2Double<T> {
        return Merge2Double(a,b,mapping)
    }

    class ToMerge2<A, B>(val a: ObservableValue<A>, val b: ObservableValue<B>) {
        fun <T> map(mapping: (A, B) -> T): Merge2<A, B, T> {
            return map(a,b, mapping)
        }

    }

    class MappedList<S, T>(
        val source: ObservableList<S>,
        val mapping: (List<S>) -> T
    ) : ObjectBinding<T>() {

        private val dependencies = FXCollections.observableArrayList(source)

        init {
            bind(source)
        }

        override fun dispose() {
            super.dispose()
            unbind(source)
        }

        override fun getDependencies(): ObservableList<*> {
            return dependencies
        }

        override fun computeValue(): T {
            return mapping(source)
        }

    }

    class Mapped<S, T>(
        val source: ObservableValue<S>,
        val mapping: (S) -> T
    ) : ObjectBinding<T>() {
        private val dependencies = FXCollections.observableArrayList(source)

        init {
            bind(source)
        }

        override fun dispose() {
            super.dispose()
            unbind(source)
        }

        override fun getDependencies(): ObservableList<*> {
            return dependencies
        }

        override fun computeValue(): T {
            return mapping(source.value)
        }
    }

    class Merge2<A, B, T>(
        val a: ObservableValue<A>,
        val b: ObservableValue<B>,
        val mapping: (A, B) -> T
    ) : ObjectBinding<T>() {
        private val dependencies = FXCollections.observableArrayList(a, b)

        init {
            bind(a, b)
        }

        override fun dispose() {
            super.dispose()
            unbind(a, b)
        }

        override fun getDependencies(): ObservableList<*> {
            return dependencies
        }

        override fun computeValue(): T {
            return mapping(a.value, b.value)
        }
    }

    class Merge2Double<T>(
        val a: ObservableDoubleValue,
        val b: ObservableDoubleValue,
        val mapping: (Double, Double) -> T
    ) : ObjectBinding<T>() {
        private val dependencies = FXCollections.observableArrayList(a, b)

        init {
            bind(a, b)
        }

        override fun dispose() {
            super.dispose()
            unbind(a, b)
        }

        override fun getDependencies(): ObservableList<*> {
            return dependencies
        }

        override fun computeValue(): T {
            return mapping(a.get(), b.get())
        }
    }
}