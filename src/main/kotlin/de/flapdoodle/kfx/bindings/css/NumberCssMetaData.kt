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