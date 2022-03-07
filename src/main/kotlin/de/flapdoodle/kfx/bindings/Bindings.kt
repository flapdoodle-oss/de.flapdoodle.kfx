package de.flapdoodle.kfx.bindings

import javafx.beans.binding.ObjectBinding
import javafx.beans.value.ObservableDoubleValue
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList

object Bindings {

    fun <S, T> map(source: ObservableValue<S>, mapping: (S) -> T): MappedBinding<S, T> {
        return MappedBinding(source, mapping)
    }

    fun <A, B, T> map(a: ObservableValue<A>, b: ObservableValue<B>, mapping: (A, B) -> T): Merge2<A, B, T> {
        return Merge2(a,b,mapping)
    }

    fun <T> mapDouble(a: ObservableDoubleValue, b: ObservableDoubleValue, mapping: (Double, Double) -> T): Merge2Double<T> {
        return Merge2Double(a,b,mapping)
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