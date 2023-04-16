package de.flapdoodle.kfx.bindings

import javafx.beans.InvalidationListener
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ChangeListener

abstract class LazyProperty<T> : ReadOnlyObjectProperty<T>() {
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

    override fun get(): T {
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
            val currentValue = get()
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


