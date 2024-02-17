package de.flapdoodle.kfx.bindings

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SubscriptionsTest {

  @Test
  fun addAndUnsubscribe() {
    var called = 0
    val testee = Subscriptions()
    val instance = "foo"

    testee.add(instance) {
      called = called + 1
    }
    testee.add(instance) {
      called = called + 1
    }
    testee.unsubscribeAll(instance)

    assertThat(called).isEqualTo(2)
    called = 0
    testee.unsubscribeAll(instance)
    assertThat(called).isEqualTo(0)
  }
}