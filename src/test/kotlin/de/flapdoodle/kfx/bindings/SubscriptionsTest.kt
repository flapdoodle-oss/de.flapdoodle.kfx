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