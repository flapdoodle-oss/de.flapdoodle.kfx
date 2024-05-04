package de.flapdoodle.kfx.controls.charts.numbers

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.ThreadLocalRandom

class BigIntTypeTest {
    private val maxDelta = Percentage.withPercentage(0.1)

    @Test
    fun minMax() {
        val someDouble = BigInteger.valueOf(ThreadLocalRandom.current().nextLong())

        val testee = BigIntType

        assertThat(testee.min(emptyList())).isNull()
        assertThat(testee.max(emptyList())).isNull()
        assertThat(testee.min(listOf(someDouble))).isEqualTo(someDouble)
        assertThat(testee.max(listOf(someDouble))).isEqualTo(someDouble)
        assertThat(testee.min(listOf(someDouble + BigInteger.ONE, someDouble))).isEqualTo(someDouble)
        assertThat(testee.max(listOf(someDouble, someDouble - BigInteger.ONE))).isEqualTo(someDouble)
    }

    @Test
    fun offset() {
        val testee = BigIntType

        assertThat(testee.offset(BigInteger.ZERO, BigInteger.TEN, 100.0, BigInteger.valueOf(3L))).isEqualTo(30.0)
    }

    @Test
    fun units() {
        val testee = BigIntType

        assertThat(testee.units(BigInteger.valueOf(0), BigInteger.valueOf(10)))
            .containsExactly(
                BigIntType.Unit(BigInteger.valueOf(10)),
                BigIntType.Unit(BigInteger.valueOf(2)),
                BigIntType.Unit(BigInteger.valueOf(1))
            )

        assertThat(testee.units(BigInteger.valueOf(100), BigInteger.valueOf(1000)))
            .containsExactly(
                BigIntType.Unit(BigInteger.valueOf(1000)),
                BigIntType.Unit(BigInteger.valueOf(200)),
                BigIntType.Unit(BigInteger.valueOf(100)),
                BigIntType.Unit(BigInteger.valueOf(20)),
                BigIntType.Unit(BigInteger.valueOf(10))
            )
    }

    @Test
    fun unit() {
        val testee = BigIntType.Unit(BigInteger.valueOf(1))

        assertThat(testee.unitsBetween(BigInteger.valueOf(0), BigInteger.valueOf(9))).isEqualTo(9)
        assertThat(testee.firstUnit(BigInteger.valueOf(2))).isEqualTo(BigInteger.valueOf(2))
        assertThat(testee.next(BigInteger.valueOf(10), 3)).isEqualTo(BigInteger.valueOf(13))
    }
}