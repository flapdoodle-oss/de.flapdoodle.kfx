package de.flapdoodle.kfx.bindings

import javafx.beans.binding.ObjectBinding
import javafx.beans.value.ObservableDoubleValue
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList

fun <T> ObservableValue<T?>.defaultIfNull(other: ObservableValue<T>): ObjectBindings.DefaultIfNull<T> {
    return ObjectBindings.defaultIfNull(this,other)
}

fun <S, T> ObservableValue<S>.map(mapping: (S) -> T): ObjectBindings.Map<S, T> {
    return ObjectBindings.map(this, mapping)
}

fun <S> ObservableValue<S>.mapToDouble(mapping: (S) -> Double): ObjectBindings.Map<S, Double> {
    return ObjectBindings.map(this, mapping)
}

fun ObservableValue<Number>.mapToDouble(): ObjectBindings.Map<Number, Double> {
    return ObjectBindings.map(this, Number::toDouble)
}

fun <A, B> ObservableValue<A>.and(other: ObservableValue<B>): ObjectBindings.WithAB<A, B> {
    return ObjectBindings.with(this).and(other)
}

object Bindings {

    fun <S, T> map(source: ObservableValue<S>, mapping: (S) -> T): ObjectBindings.Map<S, T> {
        return ObjectBindings.Map(source, mapping)
    }

    fun <S, T> mapList(source: ObservableList<S>, mapping: (List<S>) -> T): ObjectBindings.MapList<S, T> {
        return ObjectBindings.MapList(source, mapping)
    }

    fun <A, B, T> map(a: ObservableValue<A>, b: ObservableValue<B>, mapping: (A, B) -> T): ObjectBindings.Merge2<A, B, T> {
        return ObjectBindings.Merge2(a,b,mapping)
    }

    fun <A, B, C, T> map(a: ObservableValue<A>, b: ObservableValue<B>, c: ObservableValue<C>, mapping: (A, B, C) -> T): ObjectBindings.Merge3<A, B, C, T> {
        return ObjectBindings.Merge3(a, b, c, mapping)
    }

    fun <A, B, C, D, T> map(a: ObservableValue<A>, b: ObservableValue<B>, c: ObservableValue<C>, d: ObservableValue<D>, mapping: (A, B, C, D) -> T): ObjectBindings.Merge4<A, B, C, D, T> {
        return ObjectBindings.Merge4(a, b, c, d, mapping)
    }

    fun <T> mapDouble(a: ObservableDoubleValue, b: ObservableDoubleValue, mapping: (Double, Double) -> T): Merge2Double<T> {
        return Merge2Double(a,b,mapping)
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