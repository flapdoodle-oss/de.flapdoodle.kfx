package de.flapdoodle.kfx.bindings

import javafx.util.Subscription
import java.util.WeakHashMap

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