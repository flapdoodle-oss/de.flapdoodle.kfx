/*
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

import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

abstract class LazyValue<T> : ObservableValue<T> {
    private var valid = false
    private var _value: T? = null

    private var invalidationListener = emptyList<InvalidationListener>()
    private var changeListener = emptyList<ChangeListener<in T>>()

    override fun addListener(listener: InvalidationListener) {
        invalidationListener = invalidationListener + listener
    }

    override fun removeListener(listener: InvalidationListener) {
        invalidationListener = invalidationListener - listener
    }

    override fun addListener(listener: ChangeListener<in T>) {
        changeListener = changeListener + listener
    }

    override fun removeListener(listener: ChangeListener<in T>) {
        changeListener = changeListener - listener
    }

    override fun getValue(): T {
        if (!valid) {
            _value = computeValue()
            valid = true
        }
        return _value!!
    }

    fun invalidate() {
        if (valid) {
            valid = false
            fireValueChangedEvent()
        }
    }

    private fun fireValueChangedEvent() {
        invalidationListener.forEach {
            try {
                it.invalidated(this)
            } catch (e: Exception) {
                Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
            }
        }
        if (changeListener.isNotEmpty()) {
            val oldValue = _value
            val currentValue = value
            val changed = if (currentValue == null) oldValue != null else currentValue != oldValue
            if (changed) {
                changeListener.forEach {
                    try {
                        it.changed(this, oldValue, currentValue)
                    } catch (e: Exception) {
                        Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
                    }
                }
            }
        }
    }

    protected abstract fun computeValue(): T
}


