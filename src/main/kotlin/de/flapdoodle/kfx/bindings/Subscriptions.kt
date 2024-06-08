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

import javafx.util.Subscription
import java.util.*

class Subscriptions {
  private val subscriptionMap = WeakHashMap<Any, Subscription>()

  @Synchronized
  fun add(instance: Any, subscription: Subscription) {
    val old: Subscription? = subscriptionMap[instance]
    if (old == null) {
      subscriptionMap[instance] = subscription
    } else {
      subscriptionMap[instance] = old.and(subscription)
    }
  }

  fun unsubscribeAll(instance: Any) {
    val subscription = subscriptionMap[instance]
    if (subscription != null) {
      subscription.unsubscribe()
      subscriptionMap.remove(instance)
    }
  }

  companion object {
    private val global = Subscriptions()

    fun add(instance: Any, subscription: Subscription) {
      global.add(instance, subscription)
    }

    fun unsubscribeAll(instance: Any) {
      global.unsubscribeAll(instance)
    }
  }
}