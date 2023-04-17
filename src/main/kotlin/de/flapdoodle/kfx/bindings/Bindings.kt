package de.flapdoodle.kfx.bindings

import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList

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
}