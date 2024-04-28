package de.flapdoodle.kfx.bindings.css

import javafx.css.*

open class NumberCssMetaData<S : Styleable> : CssMetaData<S, Number> {
  private val propertyOfStyleable: (S) -> StyleableProperty<Number>
  private val propertyIsSetable: (S) -> Boolean

  constructor(
    cssProperty: String,
    propertyOfStylable: (S) -> SimpleStyleableDoubleProperty,
  ) : super(cssProperty, StyleConverter.getSizeConverter()) {
    this.propertyOfStyleable = { propertyOfStylable(it) }
    this.propertyIsSetable = { !propertyOfStylable(it).isBound }
  }

  override fun isSettable(styleable: S): Boolean {
    return propertyIsSetable(styleable)
  }

  override fun getStyleableProperty(styleable: S): StyleableProperty<Number> {
    return propertyOfStyleable(styleable)
  }

  fun asProperty(initialValue: Double, onInvalidated: () -> Unit): SimpleStyleableDoubleProperty {
    return object : SimpleStyleableDoubleProperty(this, this, null, initialValue) {
      override fun invalidated() {
        onInvalidated()
      }
    }
  }
}