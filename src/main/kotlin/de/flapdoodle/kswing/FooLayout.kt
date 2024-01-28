package de.flapdoodle.kswing

import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.LayoutManager2
import java.io.Serializable
import kotlin.math.max

class FooLayout : LayoutManager2, Serializable {
  private var hgap: Int = 0
  private var vgap: Int = 0
  private var north: Component? = null
  private var west: Component? = null
  private var east: Component? = null
  private var south: Component? = null
  private var center: Component? = null
  private var firstLine: Component? = null
  private var lastLine: Component? = null
  private var firstItem: Component? = null
  private var lastItem: Component? = null
  val NORTH: String = "North"
  val SOUTH: String = "South"
  val EAST: String = "East"
  val WEST: String = "West"
  val CENTER: String = "Center"
  val BEFORE_FIRST_LINE: String = "First"
  val AFTER_LAST_LINE: String = "Last"
  val BEFORE_LINE_BEGINS: String = "Before"
  val AFTER_LINE_ENDS: String = "After"
  val PAGE_START: String = "First"
  val PAGE_END: String = "Last"
  val LINE_START: String = "Before"
  val LINE_END: String = "After"
  private val serialVersionUID = -8658291919501921765L

  fun BorderLayout(hgap: Int=0, vgap: Int=0) {
    this.hgap = hgap
    this.vgap = vgap
  }

  fun getHgap(): Int {
    return this.hgap
  }

  fun setHgap(hgap: Int) {
    this.hgap = hgap
  }

  fun getVgap(): Int {
    return this.vgap
  }

  fun setVgap(vgap: Int) {
    this.vgap = vgap
  }

  override fun addLayoutComponent(comp: Component, constraints: Any?) {
    synchronized(comp.treeLock) {
      require(!(constraints != null && constraints !is String)) { "cannot add to layout: constraint must be a string (or null)" }
      this.addLayoutComponent(constraints as String?, comp)
    }
  }


  @Deprecated("")
  override fun addLayoutComponent(name: String?, comp: Component) {
    var name = name
    synchronized(comp.treeLock) {
      if (name == null) {
        name = "Center"
      }
      if ("Center" == name) {
        this.center = comp
      } else if ("North" == name) {
        this.north = comp
      } else if ("South" == name) {
        this.south = comp
      } else if ("East" == name) {
        this.east = comp
      } else if ("West" == name) {
        this.west = comp
      } else if ("First" == name) {
        this.firstLine = comp
      } else if ("Last" == name) {
        this.lastLine = comp
      } else if ("Before" == name) {
        this.firstItem = comp
      } else {
        require("After" == name) { "cannot add to layout: unknown constraint: $name" }

        this.lastItem = comp
      }
    }
  }

  override fun removeLayoutComponent(comp: Component) {
    synchronized(comp.treeLock) {
      if (comp === this.center) {
        this.center = null
      } else if (comp === this.north) {
        this.north = null
      } else if (comp === this.south) {
        this.south = null
      } else if (comp === this.east) {
        this.east = null
      } else if (comp === this.west) {
        this.west = null
      }
      if (comp === this.firstLine) {
        this.firstLine = null
      } else if (comp === this.lastLine) {
        this.lastLine = null
      } else if (comp === this.firstItem) {
        this.firstItem = null
      } else if (comp === this.lastItem) {
        this.lastItem = null
      }
    }
  }

  fun getLayoutComponent(constraints: Any): Component? {
    return if ("Center" == constraints) {
      center
    } else if ("North" == constraints) {
      north
    } else if ("South" == constraints) {
      south
    } else if ("West" == constraints) {
      west
    } else if ("East" == constraints) {
      east
    } else if ("First" == constraints) {
      firstLine
    } else if ("Last" == constraints) {
      lastLine
    } else if ("Before" == constraints) {
      firstItem
    } else if ("After" == constraints) {
      lastItem
    } else {
      throw IllegalArgumentException("cannot get component: unknown constraint: $constraints")
    }
  }

  fun getLayoutComponent(target: Container, constraints: Any): Component? {
    val ltr = target.componentOrientation.isLeftToRight
    var result: Component? = null
    if ("North" == constraints) {
      result = if (this.firstLine != null) this.firstLine else this.north
    } else if ("South" == constraints) {
      result = if (this.lastLine != null) this.lastLine else this.south
    } else if ("West" == constraints) {
      result = if (ltr) this.firstItem else this.lastItem
      if (result == null) {
        result = this.west
      }
    } else if ("East" == constraints) {
      result = if (ltr) this.lastItem else this.firstItem
      if (result == null) {
        result = this.east
      }
    } else {
      require("Center" == constraints) { "cannot get component: invalid constraint: $constraints" }

      result = this.center
    }

    return result
  }

  fun getConstraints(comp: Component?): Any? {
    return if (comp == null) {
      null
    } else if (comp === this.center) {
      "Center"
    } else if (comp === this.north) {
      "North"
    } else if (comp === this.south) {
      "South"
    } else if (comp === this.west) {
      "West"
    } else if (comp === this.east) {
      "East"
    } else if (comp === this.firstLine) {
      "First"
    } else if (comp === this.lastLine) {
      "Last"
    } else if (comp === this.firstItem) {
      "Before"
    } else {
      if (comp === this.lastItem) "After" else null
    }
  }

  override fun minimumLayoutSize(target: Container): Dimension {
    synchronized(target.treeLock) {
      val dim = Dimension(0, 0)
      val ltr = target.componentOrientation.isLeftToRight
      var c: Component? = null
      var d: Dimension
      if ((getChild("East", ltr).also { c = it }) != null) {
        d = c!!.minimumSize
        dim.width += d.width + this.hgap
        dim.height = max(d.height.toDouble(), dim.height.toDouble()).toInt()
      }

      if ((getChild("West", ltr).also { c = it }) != null) {
        d = c!!.minimumSize
        dim.width += d.width + this.hgap
        dim.height = max(d.height.toDouble(), dim.height.toDouble()).toInt()
      }

      if ((getChild("Center", ltr).also { c = it }) != null) {
        d = c!!.minimumSize
        dim.width += d.width
        dim.height = max(d.height.toDouble(), dim.height.toDouble()).toInt()
      }

      if ((getChild("North", ltr).also { c = it }) != null) {
        d = c!!.minimumSize
        dim.width = max(d.width.toDouble(), dim.width.toDouble()).toInt()
        dim.height += d.height + this.vgap
      }

      if ((getChild("South", ltr).also { c = it }) != null) {
        d = c!!.minimumSize
        dim.width = max(d.width.toDouble(), dim.width.toDouble()).toInt()
        dim.height += d.height + this.vgap
      }

      val insets = target.insets
      dim.width += insets.left + insets.right
      dim.height += insets.top + insets.bottom
      return dim
    }
  }

  override fun preferredLayoutSize(target: Container): Dimension {
    synchronized(target.treeLock) {
      val dim = Dimension(0, 0)
      val ltr = target.componentOrientation.isLeftToRight
      var c: Component? = null
      var d: Dimension
      if ((getChild("East", ltr).also { c = it }) != null) {
        d = c!!.preferredSize
        dim.width += d.width + this.hgap
        dim.height = max(d.height.toDouble(), dim.height.toDouble()).toInt()
      }

      if ((getChild("West", ltr).also { c = it }) != null) {
        d = c!!.preferredSize
        dim.width += d.width + this.hgap
        dim.height = max(d.height.toDouble(), dim.height.toDouble()).toInt()
      }

      if ((getChild("Center", ltr).also { c = it }) != null) {
        d = c!!.preferredSize
        dim.width += d.width
        dim.height = max(d.height.toDouble(), dim.height.toDouble()).toInt()
      }

      if ((getChild("North", ltr).also { c = it }) != null) {
        d = c!!.preferredSize
        dim.width = max(d.width.toDouble(), dim.width.toDouble()).toInt()
        dim.height += d.height + this.vgap
      }

      if ((getChild("South", ltr).also { c = it }) != null) {
        d = c!!.preferredSize
        dim.width = max(d.width.toDouble(), dim.width.toDouble()).toInt()
        dim.height += d.height + this.vgap
      }

      val insets = target.insets
      dim.width += insets.left + insets.right
      dim.height += insets.top + insets.bottom
      return dim
    }
  }

  override fun maximumLayoutSize(target: Container?): Dimension {
    return Dimension(Int.MAX_VALUE, Int.MAX_VALUE)
  }

  override fun getLayoutAlignmentX(parent: Container?): Float {
    return 0.5f
  }

  override fun getLayoutAlignmentY(parent: Container?): Float {
    return 0.5f
  }

  override fun invalidateLayout(target: Container?) {
  }

  override fun layoutContainer(target: Container) {
    synchronized(target.treeLock) {
      val insets = target.insets
      var top = insets.top
      var bottom = target.height - insets.bottom
      var left = insets.left
      var right = target.width - insets.right
      val ltr = target.componentOrientation.isLeftToRight
      var c: Component? = null
      var d: Dimension
      if ((getChild("North", ltr).also { c = it }) != null) {
        c!!.setSize(right - left, c!!.height)
        d = c!!.preferredSize
        c!!.setBounds(left, top, right - left, d.height)
        top += d.height + this.vgap
      }

      if ((getChild("South", ltr).also { c = it }) != null) {
        c!!.setSize(right - left, c!!.height)
        d = c!!.preferredSize
        c!!.setBounds(left, bottom - d.height, right - left, d.height)
        bottom -= d.height + this.vgap
      }

      if ((getChild("East", ltr).also { c = it }) != null) {
        c!!.setSize(c!!.width, bottom - top)
        d = c!!.preferredSize
        c!!.setBounds(right - d.width, top, d.width, bottom - top)
        right -= d.width + this.hgap
      }

      if ((getChild("West", ltr).also { c = it }) != null) {
        c!!.setSize(c!!.width, bottom - top)
        d = c!!.preferredSize
        c!!.setBounds(left, top, d.width, bottom - top)
        left += d.width + this.hgap
      }
      if ((getChild("Center", ltr).also { c = it }) != null) {
        c!!.setBounds(left, top, right - left, bottom - top)
      }
    }
  }

  private fun getChild(key: String, ltr: Boolean): Component? {
    var result: Component? = null
    if (key === "North") {
      result = if (this.firstLine != null) this.firstLine else this.north
    } else if (key === "South") {
      result = if (this.lastLine != null) this.lastLine else this.south
    } else if (key === "West") {
      result = if (ltr) this.firstItem else this.lastItem
      if (result == null) {
        result = this.west
      }
    } else if (key === "East") {
      result = if (ltr) this.lastItem else this.firstItem
      if (result == null) {
        result = this.east
      }
    } else if (key === "Center") {
      result = this.center
    }

    if (result != null && !result.isVisible) {
      result = null
    }

    return result
  }

  override fun toString(): String {
    val var10000 = this.javaClass.name
    return var10000 + "[hgap=" + this.hgap + ",vgap=" + this.vgap + "]"
  }
}
