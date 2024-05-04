package de.flapdoodle.kfx.types.ranges

import de.flapdoodle.kfx.types.ranges.LocalDateRangeFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Month
import java.time.temporal.ChronoUnit

class LocalDateRangeFactoryTest {

  @Test
  fun dateTicks() {
    val min = LocalDate.of(2023, Month.JUNE, 18)
    val max = LocalDate.of(2024, Month.SEPTEMBER, 23)

    val yearTicks = LocalDateRangeFactory.ticks(min, max, ChronoUnit.YEARS, 20)

    assertThat(yearTicks.list)
      .hasSize(1)
      .containsExactly(
        LocalDate.of(2024, Month.JANUARY, 1)
      )

    val monthTicks = LocalDateRangeFactory.ticks(min, max, ChronoUnit.MONTHS, 20)

    assertThat(monthTicks.list)
      .hasSize(15)
      .contains(
        LocalDate.of(2023, Month.JULY, 1),
        LocalDate.of(2024, Month.SEPTEMBER, 1),
      )
      .doesNotContain(
        LocalDate.of(2023, Month.JUNE, 1),
        LocalDate.of(2024, Month.OCTOBER, 1),
      )

    val dayTicks = LocalDateRangeFactory.ticks(min, max, ChronoUnit.DAYS, 20)

    assertThat(dayTicks.list)
      .isEmpty()

    val unlimitedDayTicks = LocalDateRangeFactory.ticks(min, max, ChronoUnit.DAYS, 1000)

    assertThat(unlimitedDayTicks.list)
      .hasSize(464)
      .contains(
        min,
        max,
      )
      .doesNotContain(
        min.minusDays(1),
        max.plusDays(1),
      )
  }

  @Test
  fun dateTicksYearsEdgeCase() {
    val min = LocalDate.of(2023, Month.JANUARY, 1)
    val max = LocalDate.of(2024, Month.JANUARY, 1)

    val yearTicks = LocalDateRangeFactory.ticks(min, max, ChronoUnit.YEARS, 20)

    assertThat(yearTicks.list)
      .hasSize(2)
      .containsExactly(
        min,
        max
      )
  }

  @Test
  fun dateTicksMonthEdgeCase() {
    val min = LocalDate.of(2023, Month.JANUARY, 1)
    val max = LocalDate.of(2023, Month.FEBRUARY, 1)

    val yearTicks = LocalDateRangeFactory.ticks(min, max, ChronoUnit.MONTHS, 20)

    assertThat(yearTicks.list)
      .hasSize(2)
      .containsExactly(
        min,
        max
      )
  }

  @Test
  fun dateTicksDaysEdgeCase() {
    val min = LocalDate.of(2023, Month.JANUARY, 1)
    val max = LocalDate.of(2023, Month.JANUARY, 1)

    val yearTicks = LocalDateRangeFactory.ticks(min, max, ChronoUnit.DAYS, 20)

    assertThat(yearTicks.list)
      .hasSize(1)
      .containsExactly(
        min
      )
  }
}