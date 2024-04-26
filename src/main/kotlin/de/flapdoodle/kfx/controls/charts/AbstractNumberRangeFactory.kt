package de.flapdoodle.kfx.controls.charts

abstract class AbstractNumberRangeFactory<T: Number>: RangeFactory<T> {
  override fun rangeOf(values: List<T>): Range<T> {
    return doubleRange(values)
  }

  companion object {
    private fun <T: Number> doubleRange(list: List<T>): Range<T> {
      if (list.isEmpty()) return Range.empty()
      if (list.size == 1) return Range.single(list[0])

      val asDouble = list.map { it.toDouble() }
      val min = asDouble.min()
      val max = asDouble.max()

      val dist = max - min

      return object : Range<T> {
        override fun offset(value: T, scale: Double): Double {
          val valueDist = value.toDouble() - min
          return if (dist != 0.0)
            scale * valueDist / dist
          else
            scale / 2.0
        }

        override fun ticks(maxTicks: Int): List<Ticks<T>> {
          val baseDist = 1.0

          return listOf(ticks<T>(min, max, 1, maxTicks)).filter {
            it.list.isNotEmpty()
          }
        }
      }
    }

    internal fun <T: Number> ticks(min: Double, max: Double, base: Int, maxTicks: Int): Ticks<T> {
      return Ticks(emptyList())
    }
  }
}