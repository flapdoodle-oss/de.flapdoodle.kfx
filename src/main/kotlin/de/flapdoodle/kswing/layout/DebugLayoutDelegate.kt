package de.flapdoodle.kswing.layout

import java.awt.Component
import java.awt.Container
import java.awt.Dimension

class DebugLayoutDelegate<T: Any>(val delegate: AbstractLayoutManager<T>) : AbstractLayoutManager<T> {

  override fun addComponent(component: Component, attribute: T?) {
    println("addComponent($component,$attribute) -> $delegate")
    delegate.addComponent(component, attribute)
  }

  override fun removeLayoutComponent(component: Component) {
    println("removeLayoutComponent($component) -> $delegate")
    delegate.removeLayoutComponent(component)
  }

  override fun preferredLayoutSize(target: Container): Dimension {
    println("preferredLayoutSize($target) -> $delegate")
    val ret = delegate.preferredLayoutSize(target)
    println("preferredLayoutSize($target) -> $delegate = $ret")
    return ret
  }

  override fun minimumLayoutSize(target: Container): Dimension {
    println("minimumLayoutSize($target) -> $delegate")
    val ret = delegate.minimumLayoutSize(target)
    println("minimumLayoutSize($target) -> $delegate = $ret")
    return ret
  }

  override fun layoutContainer(target: Container) {
    println("layoutContainer($target) -> $delegate")
    delegate.layoutContainer(target)
  }

  override fun maximumLayoutSize(target: Container): Dimension {
    println("maximumLayoutSize($target) -> $delegate")
    val ret = maximumLayoutSize(target)
    println("maximumLayoutSize($target) -> $delegate = $ret")
    return ret
  }

  override fun getLayoutAlignmentX(target: Container): Float {
    println("getLayoutAlignmentX($target) -> $delegate")
    val ret = getLayoutAlignmentX(target)
    println("getLayoutAlignmentX($target) -> $delegate = $ret")
    return ret
  }

  override fun getLayoutAlignmentY(target: Container): Float {
    println("getLayoutAlignmentY($target) -> $delegate")
    val ret = getLayoutAlignmentY(target)
    println("getLayoutAlignmentY($target) -> $delegate = $ret")
    return ret
  }

  override fun invalidateLayout(target: Container) {
    println("invalidateLayout($target) -> $delegate")
    delegate.invalidateLayout(target)
  }
}