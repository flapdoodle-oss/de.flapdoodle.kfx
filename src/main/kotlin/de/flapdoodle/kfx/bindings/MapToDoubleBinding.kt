/**
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