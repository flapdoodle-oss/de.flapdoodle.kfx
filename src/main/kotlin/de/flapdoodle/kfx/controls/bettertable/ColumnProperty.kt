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
package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.reflection.TypeInfo
import javafx.util.StringConverter
import java.util.*
import kotlin.reflect.KClass

data class ColumnProperty<T: Any, C: Any>(
  val type: TypeInfo<C>,
  val getter: (T) -> C?,
  val converter: ValidatingConverter<C> = Converters.validatingFor(type, Locale.getDefault())
) {
  @Deprecated("use typeinfo")
  constructor(
    type: KClass<C>,
    getter: (T) -> C?,
    converter: ValidatingConverter<C> = Converters.validatingFor(type, Locale.getDefault())
  ) : this(TypeInfo.of(type.javaObjectType), getter, converter)
}