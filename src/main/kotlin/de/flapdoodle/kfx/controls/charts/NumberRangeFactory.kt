package de.flapdoodle.kfx.controls.charts

import de.flapdoodle.kfx.controls.charts.numbers.NumberType
import de.flapdoodle.kfx.controls.charts.numbers.NumberUnit

class NumberRangeFactory<T: Number>(
  private val type: NumberType<T>
): RangeFactory<T> {
  override fun rangeOf(values: List<T>): Range<T> {
    return doubleRange(type, values)
  }

  companion object {
    private fun <T: Number> doubleRange(type: NumberType<T>, list: List<T>): Range<T> {
      if (list.isEmpty()) return Range.empty()
      if (list.size == 1) return Range.single(list[0])


      val min = requireNotNull(type.min(list)) { "min not found for $list"}
      val max = requireNotNull(type.max(list)) { "max not found for $list"}

      return object : Range<T> {
        override fun offset(value: T, scale: Double): Double {
          return type.offset(min, max, scale, value)
        }

        override fun ticks(maxTicks: Int): List<Ticks<T>> {
          return type.units(min, max)
            .map { ticks(it, min, max, maxTicks) }
            .filter { it.list.isNotEmpty() }
            .reversed()
        }
      }
    }

    private fun <T: Number> ticks(unit: NumberUnit<T>, min: T, max: T, maxTicks: Int): Ticks<T> {
      val ticks = unit.unitsBetween(min, max)
      val list = if (ticks <= maxTicks) {
        val start = unit.firstAfter(min)
        (0..<ticks).map {
          unit.next(start, it)
        }
      } else {
        emptyList()
      }
      return Ticks(list)
    }
  }

}