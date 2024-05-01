package de.flapdoodle.kfx.controls.charts.numbers

object DoubleType : NumberType<Double> {
  override fun min(values: List<Double>): Double? {
    return values.minOrNull()
  }

  override fun max(values: List<Double>): Double? {
    return values.maxOrNull()
  }

  override fun offset(min: Double, max: Double, scale: Double, value: Double): Double {
    require(min <= max) { "$min > $max" }

    val valueDist = value - min
    val dist = max - min
    return if (dist != 0.0)
      scale * valueDist / dist
    else
      scale / 2.0
  }

  override fun units(min: Double, max: Double): List<NumberUnit<Double>> {
    require(min <= max) { "$min > $max" }

    val dist = max - min
    val oneTickUnit = biggestOneTick(dist)
    return listOf(
      Unit(oneTickUnit),
      Unit(oneTickUnit/5.0),
      Unit(oneTickUnit/10.0),
      Unit(oneTickUnit/50.0),
      Unit(oneTickUnit/100.0))
  }

  private fun biggestOneTick(dist: Double, start: Double = 1.0): Double {
    return if (dist>start) {
      unitUntilDistIsSmaller(dist, start)
    } else {
      unitUntilDistIsBigger(dist, start)
    }
  }

  private fun unitUntilDistIsSmaller(dist: Double, start: Double): Double {
    return if (dist > start) {
      unitUntilDistIsSmaller(dist, start * 10.0)
    } else {
      start
    }
  }

  private fun unitUntilDistIsBigger(dist: Double, start: Double): Double {
    return if (dist < start) {
      unitUntilDistIsBigger(dist, start / 10.0)
    } else {
      start
    }
  }

  data class Unit(val unit: Double) : NumberUnit<Double> {

    override fun unitsBetween(min: Double, max: Double): Int {
      return ((max - min) / unit).toInt()
    }

    override fun firstAfter(value: Double): Double {
      val rest = value % unit
      return value + (unit - rest)
    }

    override fun next(value: Double, offset: Int): Double {
      return value + (offset * unit)
    }

  }
}