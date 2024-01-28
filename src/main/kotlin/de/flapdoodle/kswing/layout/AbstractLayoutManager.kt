package de.flapdoodle.kswing.layout

import java.awt.Component
import java.awt.LayoutManager2

interface AbstractLayoutManager<T: Any> : LayoutManager2 {
  override fun addLayoutComponent(p0: Component, p1: Any?) {
    addComponent(p0, p1 as T?)
  }

  @Deprecated("don't use", ReplaceWith("addLayoutComponent(p1)"))
  override fun addLayoutComponent(p0: String?, p1: Component?) {
    throw IllegalArgumentException("not supported")
  }

  abstract fun addComponent(component: Component, attribute: T?)

  fun logCalls(): DebugLayoutDelegate<T> {
    return DebugLayoutDelegate(this)
  }
}