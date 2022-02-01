package de.flapdoodle.kfx.bindings

import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList

fun <S, T> ReadOnlyObjectProperty<S>.mapTo(mapping: (S) -> T): MappedBinding<S, T> {
    return MappedBinding(this, mapping)
}

class MappedBinding<S, T>(
    val source: ReadOnlyObjectProperty<S>,
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