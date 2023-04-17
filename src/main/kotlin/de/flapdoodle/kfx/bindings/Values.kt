package de.flapdoodle.kfx.bindings

import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableDoubleValue
import javafx.beans.value.ObservableValue

object Values {
  fun constant(value: Double): ObservableDoubleValue {
    return DoubleConstant(value)
  }

  fun <T> constantObject(value: T): Constant<T> {
    return Constant(value)
  }

  class Constant<T>(private val value: T): ObservableValue<T> {
    override fun addListener(listener: ChangeListener<in T>?) {
      // no-op
    }

    override fun addListener(listener: InvalidationListener?) {
      // no-op
    }

    override fun removeListener(listener: InvalidationListener?) {
      // no-op
    }

    override fun removeListener(listener: ChangeListener<in T>?) {
      // no-op
    }

    override fun getValue(): T {
      return value
    }
  }

  class DoubleConstant(private val value: Double) : ObservableDoubleValue {
    override fun get(): Double {
      return value
    }

    override fun getValue(): Double {
      return value
    }

    override fun addListener(observer: InvalidationListener) {
      // no-op
    }

    override fun addListener(listener: ChangeListener<in Number?>) {
      // no-op
    }

    override fun removeListener(observer: InvalidationListener) {
      // no-op
    }

    override fun removeListener(listener: ChangeListener<in Number?>) {
      // no-op
    }

    override fun intValue(): Int {
      return value.toInt()
    }

    override fun longValue(): Long {
      return value.toLong()
    }

    override fun floatValue(): Float {
      return value.toFloat()
    }

    override fun doubleValue(): Double {
      return value
    }

    companion object {
      fun valueOf(value: Double): DoubleConstant {
        return DoubleConstant(value)
      }
    }
  }


}