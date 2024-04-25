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
package de.flapdoodle.kfx.controls.charts

import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

object RangeFactories {

  fun localDate(): RangeFactory<LocalDate> {
    return RangeFactory { values -> dateRange(values) }
  }

  fun number(): RangeFactory<Number> {
    return RangeFactory { values -> doubleRange(values) }
  }

  private fun dateRange(list: List<LocalDate>): Range<LocalDate> {
    if (list.isEmpty() || list.size == 1) return empty()

    val min = list.min()
    val max = list.max()

    val dist = ChronoUnit.DAYS.between(min, max)

    return object : Range<LocalDate> {
      override fun offset(value: LocalDate, scale: Double): Double {
        val valueDist = ChronoUnit.DAYS.between(min, value)
        return if (dist != 0L)
          scale * valueDist / dist
        else
          scale / 2.0
      }

      override fun ticks(maxTicks: Int): List<Ticks<LocalDate>> {
        return dateTicks(min, max, maxTicks)
      }
    }
  }

  // VisibleForTesting
  internal fun dateTicks(min: LocalDate, max: LocalDate, maxTicks: Int): List<Ticks<LocalDate>> {
    val chronoUnits = listOf(
      ChronoUnit.YEARS,
      ChronoUnit.MONTHS,
      ChronoUnit.DAYS
    )

    return chronoUnits.map {
      ticks(min, max, it, maxTicks)
    }.filter { it.list.isNotEmpty() }
  }

  internal fun ticks(min: LocalDate, max: LocalDate, chronoUnit: ChronoUnit, maxTicks: Int): Ticks<LocalDate> {
    val dist = chronoUnit.between(min, max)

    val period = when (chronoUnit) {
      ChronoUnit.YEARS -> Period.ofYears(1)
      ChronoUnit.MONTHS -> Period.ofMonths(1)
      ChronoUnit.DAYS -> Period.ofDays(1)
      else -> throw IllegalArgumentException("not implemented: $chronoUnit")
    }

    val end = max.plusDays(1)

    val start= when (chronoUnit) {
      ChronoUnit.YEARS -> (if (min.dayOfYear != 1)
        min.withDayOfYear(1).plusYears(1)
      else min)

      ChronoUnit.MONTHS -> (if (min.dayOfMonth != 1)
        min.withDayOfMonth(1).plusMonths(1)
      else min)

      ChronoUnit.DAYS -> min

      else -> throw IllegalArgumentException("not implemented: $chronoUnit")
    }

    val list = if (dist < maxTicks) {
      start.datesUntil(end, period)
        .map { it }
        .toList()
    } else emptyList()
    
    return Ticks(list)
  }

  private fun doubleRange(list: List<Number>): Range<Number> {
    if (list.isEmpty() || list.size == 1) return empty()

    val asDouble = list.map { it.toDouble() }
    val min = asDouble.min()
    val max = asDouble.max()

    val dist = max - min

    return object : Range<Number> {
      override fun offset(value: Number, scale: Double): Double {
        val valueDist = value.toDouble() - min
        return if (dist != 0.0)
          scale * valueDist / dist
        else
          scale / 2.0
      }

      override fun ticks(maxTicks: Int): List<Ticks<Number>> {
        return emptyList()
      }
    }
  }

  private fun <T : Any> empty(): Range<T> {
    return object : Range<T> {
      override fun offset(value: T, scale: Double): Double {
        return scale / 2.0
      }

      override fun ticks(maxTicks: Int): List<Ticks<T>> {
        return emptyList()
      }
    }
  }
}