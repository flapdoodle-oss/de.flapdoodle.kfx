package de.flapdoodle.kfx.controls.charts

@Deprecated("")
class NumberRangeFactory : RangeFactory<Number> {
  override fun rangeOf(values: List<Number>): Range<Number> {
    return doubleRange(values)
  }

  companion object {
    private fun doubleRange(list: List<Number>): Range<Number> {
      if (list.isEmpty()) return Range.empty()
      if (list.size == 1) return Range.single(list[0])

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
          val baseDist = 1.0

          return listOf(ticks(min, max, 1, maxTicks)).filter {
            it.list.isNotEmpty()
          }
        }
      }
    }

    internal fun ticks(min: Double, max: Double, base: Int, maxTicks: Int): Ticks<Number> {
      return Ticks(emptyList())
    }
  }
}