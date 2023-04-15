package de.flapdoodle.kfx.bindings

import com.sun.javafx.binding.ExpressionHelper
import javafx.beans.InvalidationListener
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.value.ChangeListener

// TODO hmm.. das sollte besser weg
abstract class LazyBoundsProperty<T> : ReadOnlyObjectProperty<T>() {
    private var helper: ExpressionHelper<T>? = null
    private var valid = false
    private var _value: T? = null

    override fun addListener(listener: InvalidationListener) {
        helper = ExpressionHelper.addListener(helper, this, listener)
    }

    override fun removeListener(listener: InvalidationListener) {
        helper = ExpressionHelper.removeListener(helper, listener)
    }

    override fun addListener(listener: ChangeListener<in T>) {
        helper = ExpressionHelper.addListener(helper, this, listener)
    }

    override fun removeListener(listener: ChangeListener<in T>) {
        helper = ExpressionHelper.removeListener(helper, listener)
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
            ExpressionHelper.fireValueChangedEvent(helper)
        }
    }

    protected abstract fun computeValue(): T
}
