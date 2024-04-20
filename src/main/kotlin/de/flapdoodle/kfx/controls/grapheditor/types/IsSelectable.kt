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
package de.flapdoodle.kfx.controls.grapheditor.types

interface IsSelectable {
  fun isSelected(): Boolean
  fun select(value: Boolean)

  companion object {
    fun <T: IsSelectable> select(multi: Boolean, item: T, all: List<T>) {
      if (!multi) {
        var selected = 0
        all.forEach {
          if (it.isSelected()) selected += 1
        }
        if (selected>1) {
          item.select(true)
        } else
          item.select(!item.isSelected())

        all.forEach {
          if (item != it) it.select(false)
        }
      } else {
        item.select(!item.isSelected())
      }
    }
  }
}