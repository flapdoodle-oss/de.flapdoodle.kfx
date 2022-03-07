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

import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList

fun <S, T> ObservableValue<S>.mapTo(mapping: (S) -> T): MappedBinding<S, T> {
    return MappedBinding(this, mapping)
}

class MappedBinding<S, T>(
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