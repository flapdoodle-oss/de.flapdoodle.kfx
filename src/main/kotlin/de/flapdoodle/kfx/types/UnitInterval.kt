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
package de.flapdoodle.kfx.types

operator fun Double.times(other: UnitInterval) = other.times(this)

/**
 * https://en.wikipedia.org/wiki/Unit_interval
 */
data class UnitInterval(val value: Double) {
    init {
        require(value>=0.0) {"invalid value: $value < 0.0"}
        require(value <=1.0) {"invalid value: $value > 1.0"}
    }

    operator fun times(other: Double): Double {
        return other*value
    }

    companion object {
        val ZERO=UnitInterval(0.0)
        val HALF=UnitInterval(0.5)
        val ONE=UnitInterval(1.0)
    }
}
