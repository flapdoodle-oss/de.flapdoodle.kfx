package de.flapdoodle.kfx.extensions

import javafx.util.Subscription

operator fun Subscription.plus(other: Subscription): Subscription {
  return this.and(other)
}
