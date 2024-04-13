package de.flapdoodle.kfx.extensions

import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.value.ObservableValue

fun ReadOnlyDoubleProperty.asDouble(default: Double): ObservableValue<Double> {
    return mapNullable { it -> it?.toDouble() ?: default }
}