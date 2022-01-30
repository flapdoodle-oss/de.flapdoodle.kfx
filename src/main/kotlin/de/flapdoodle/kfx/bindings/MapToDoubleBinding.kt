package de.flapdoodle.kfx.bindings

import javafx.beans.binding.DoubleBinding
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList

fun <T> ReadOnlyObjectProperty<T>.mapToDouble(mapping: (T) -> Double): MapToDoubleBinding<T> {
    return MapToDoubleBinding(this,mapping)
}

class MapToDoubleBinding<T>(val source: ReadOnlyObjectProperty<T>, val mapping: (T) -> Double) : DoubleBinding() {
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

    override fun computeValue(): Double {
        return mapping(source.value)
    }
}