package de.flapdoodle.kfx.types.ranges

import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors

class LocalDateRangeFactory : RangeFactory<LocalDate> {
  override fun rangeOf(values: List<LocalDate>): Range<LocalDate> {
    return dateRange(values)
  }

  companion object {
    internal fun dateRange(list: List<LocalDate>): Range<LocalDate> {
      if (list.isEmpty()) return Range.empty()
      if (list.size == 1) return Range.single(list[0])

      val min = list.min()
      val max = list.max()

      require(!min.isAfter(max)) { "$min > $max" }

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
        ChronoUnit.DAYS,
        ChronoUnit.MONTHS,
        ChronoUnit.YEARS
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

      val start = when (chronoUnit) {
        ChronoUnit.YEARS -> (if (min.dayOfYear != 1)
          min.withDayOfYear(1).plusYears(1)
        else min)

        ChronoUnit.MONTHS -> (if (min.dayOfMonth != 1)
          min.withDayOfMonth(1).plusMonths(1)
        else min)

        ChronoUnit.DAYS -> min

        else -> throw IllegalArgumentException("not implemented: $chronoUnit")
      }

      val list = if (dist < maxTicks && !start.isAfter(end)) {
        start.datesUntil(end, period).collect(Collectors.toList())
      } else emptyList()

      return Ticks(list)
    }
  }
}