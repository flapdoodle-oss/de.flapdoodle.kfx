package de.flapdoodle.kfx.controls.charts.numbers

object IntType : NumberType<Int> {
  override fun min(values: List<Int>): Int? {
    return values.minOrNull()
  }

  override fun max(values: List<Int>): Int? {
    return values.maxOrNull()
  }

  override fun offset(min: Int, max: Int, scale: Double, value: Int): Double {
    require(min <= max) { "$min > $max" }

    val valueDist = value - min
    val dist = max - min
    return if (dist != 0)
      scale * valueDist / dist
    else
      scale / 2.0
  }

  override fun units(min: Int, max: Int): List<NumberUnit<Int>> {
    require(min <= max) { "$min > $max" }

    val dist = max - min
    val oneTickUnit = biggestOneTick(dist)
    return listOf(
      Unit(oneTickUnit),
      Unit(oneTickUnit / 5),
      Unit(oneTickUnit / 10),
      Unit(oneTickUnit / 50),
      Unit(oneTickUnit / 100)
    ).filter { it.unit != 0 }
  }

  private fun biggestOneTick(dist: Int): Int {
    return if (dist > 1L) {
      unitUntilDistIsSmaller(dist, 1)
    } else {
      unitUntilDistIsBigger(dist, 1)
    }
  }

  private fun unitUntilDistIsSmaller(dist: Int, start: Int): Int {
    return if (dist > start) {
      unitUntilDistIsSmaller(dist, start * 10)
    } else {
      start
    }
  }

  private fun unitUntilDistIsBigger(dist: Int, start: Int): Int {
    return if (dist < start) {
      unitUntilDistIsBigger(dist, start / 10)
    } else {
      start
    }
  }

  data class Unit(val unit: Int) : NumberUnit<Int> {

    override fun unitsBetween(min: Int, max: Int): Int {
      return ((max - min) / unit).toInt()
    }

    override fun firstUnit(value: Int): Int {
      val rest = value % unit
      return if (rest == 0) value else value + (unit - rest)
    }

    override fun next(value: Int, offset: Int): Int {
      return value + (offset * unit)
    }
  }
}