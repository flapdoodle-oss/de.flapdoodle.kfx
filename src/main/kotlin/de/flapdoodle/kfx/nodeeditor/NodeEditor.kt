package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.events.SharedLock
import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.extensions.Nodes
import de.flapdoodle.kfx.graph.nodes.SizeMode
import de.flapdoodle.kfx.nodeeditor.Node.Style.disable
import de.flapdoodle.kfx.nodeeditor.Node.Style.enable
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.geometry.Point2D
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane

class NodeEditor : AnchorPane() {
  val sharedLock = SharedLock<javafx.scene.Node>()

  init {
    children.add(NodeView(sharedLock).withAnchors(all = 0.0).apply {
      getViewChildren().addAll(Node("one").apply {
        layoutPosition = Point2D(100.0, 50.0)
      }, Node("two").apply {
        content = Button("Helloooo")
      })
    })

    addEventHandler(MouseEvent.ANY, this::handleMouseEventSimplified)
  }

  private fun bestSizeMode(screenX: Double, screenY: Double): SizeMode? {
    return Nodes.hit(
      this,
      Point2D(screenX, screenY),
      0.0,
      javafx.scene.Node::screenToLocal
    ).filter {
      it is Node || Markers.isDragBar(it)
    }.map {
      when {
        Markers.isDragBar(it) -> SizeMode.INSIDE
        it is Node -> {
          val targetLocalPosition = it.screenToLocal(Point2D(screenX, screenY))
          val sizeMode = SizeMode.guess(targetLocalPosition, it.size)
          if (sizeMode != SizeMode.INSIDE) sizeMode else null
        }

        else -> null
      }
    }
      .firstOrNull()
  }

  private fun bestAction(screenPosition: Point2D): Pair<Node,Action>? {
    val nodesAndBars = Nodes.hit(
      this,
      screenPosition,
      0.0,
      javafx.scene.Node::screenToLocal
    ).filter {
      it is Node || Markers.isDragBar(it)
    }

    val bestSizeMode = nodesAndBars.map { when {
      Markers.isDragBar(it) -> SizeMode.INSIDE
      it is Node -> {
        val targetLocalPosition = it.screenToLocal(screenPosition)
        val sizeMode = SizeMode.guess(targetLocalPosition, it.size)
        if (sizeMode != SizeMode.INSIDE) sizeMode else null
      }

      else -> null
    } }.firstOrNull()

    val matchingNode = nodesAndBars.filterIsInstance<Node>().firstOrNull()
    return if (matchingNode!=null && bestSizeMode!=null) {
      matchingNode to when (bestSizeMode) {
        SizeMode.INSIDE -> Action.Move(screenPosition, matchingNode.layoutPosition)
        else -> Action.Resize(screenPosition,bestSizeMode,LayoutBounds(matchingNode.layoutPosition, matchingNode.size))
      }
    } else null
  }

  private fun handleMouseEventSimplified(event: MouseEvent) {
//    println("--> ${event.eventType} : $sharedLock")

    val target = event.target

    when (event.eventType) {
      MouseEvent.MOUSE_ENTERED_TARGET -> {
        if (target is Node) {
          sharedLock.tryLock(target) {
            event.consume()
            Node.Style.Active.enable(target)
            Action.Focus()
          }
        }
      }
      MouseEvent.MOUSE_EXITED_TARGET -> {
        if (target is Node) {
          sharedLock.ifLocked(target, Action.Focus::class.java) {
            event.consume()
            cursor = null
            Node.Style.Active.disable(target)
            it.releaseLock()
          }
        }
      }
      MouseEvent.MOUSE_MOVED -> {
        sharedLock.ifLocked(Node::class.java, Action::class.java) {
          val nodeAction = bestAction(event.screenPosition)
          val action = nodeAction?.second
          cursor = when (action) {
            is Action.Move -> SizeMode.INSIDE.cursor()
            is Action.Resize -> action.sizeMode.cursor()
            else -> null
          }
        }
      }
      MouseEvent.MOUSE_PRESSED -> {
        sharedLock.ifLocked(Node::class.java, Action::class.java) {
          val nodeAction = bestAction(event.screenPosition)
          if (nodeAction != null) {
            event.consume()
            it.replaceLock(nodeAction.second)
          } else {
            println("no action found")
          }
        }
      }
      MouseEvent.MOUSE_DRAGGED -> {
        sharedLock.ifLocked(Node::class.java, Action::class.java) { lock ->
          val action = lock.value
          val active = lock.owner

          when (action) {
            is Action.Move -> {
              val diff = event.screenPosition - action.clickPosition
              active.layoutPosition = action.layoutPosition + active.screenDeltaToLocal(diff)
            }

            is Action.Resize -> {
              val diff = event.screenPosition - action.clickPosition
              val fixedDiff = active.screenDeltaToLocal(diff)
              val resizedBounds = SizeMode.resize(action.sizeMode, action.layout, fixedDiff)

              active.resizeTo(resizedBounds)
            }

            else -> {
              println(".. should not happen")
            }
          }

        }
      }
      MouseEvent.MOUSE_RELEASED -> {
        sharedLock.ifLocked(Node::class.java, Action::class.java) { lock ->
          event.consume()
          cursor = null
          lock.replaceLock(Action.Focus())
        }
      }
    }
  }
  private fun handleMouseEvent(event: MouseEvent) {
    println("--> ${event.eventType}  ${event.target}")
    
//    if (event.eventType == MouseEvent.MOUSE_ENTERED_TARGET || event.eventType == MouseEvent.MOUSE_EXITED_TARGET ) {
//      println("--> ${event.eventType}  ${event.target}")
//    }

    val target = event.target
    when (event.eventType) {
      MouseEvent.MOUSE_ENTERED_TARGET -> {
        if (target is Node) {
          sharedLock.tryLock(target) {
            event.consume()
            Node.Style.Active.enable(target)

            Action.Focus()
          }
        } else {
          sharedLock.ifLocked(Node::class.java, Action.Focus::class.java) { lock ->
            if (target is javafx.scene.Node && Markers.isDragBar(target)) {
              cursor = SizeMode.INSIDE.cursor()
              lock.replaceLock(Action.Focus(SizeMode.INSIDE))
            }
          }
        }
      }

      MouseEvent.MOUSE_EXITED_TARGET -> {
        if (target is Node) {
          sharedLock.tryRelease(target, Action.Focus::class.java) {
            event.consume()
            cursor = null
            Node.Style.Active.disable(target)
          }
        } else {
          sharedLock.ifLocked(Node::class.java, Action.Focus::class.java) {
            if (target is javafx.scene.Node && Markers.isDragBar(target)) {
              cursor = null
              it.replaceLock(Action.Focus())
            }
          }
        }
      }

      MouseEvent.MOUSE_MOVED -> {
        sharedLock.ifUnlocked {
          cursor = bestSizeMode(event.screenX, event.screenY)?.cursor()
        }
      }
    }

    sharedLock.ifLocked(Node::class.java, Action::class.java) { lock ->
      val action = lock.value
      val active = lock.owner
      if (action is Action.Focus) {
//        println("action -> $action")
        when (event.eventType) {
          MouseEvent.MOUSE_PRESSED -> {
            if (action.sizeMode!=null) {
              cursor = action.sizeMode.cursor()

              val newAction = when(action.sizeMode) {
                SizeMode.INSIDE -> Action.Move(
                  clickPosition = event.screenPosition,
                  layoutPosition = active.layoutPosition
                )
                else -> Action.Resize(
                  clickPosition = event.screenPosition,
                  sizeMode = action.sizeMode,
                  layout = LayoutBounds(active.layoutPosition, active.size)
                )
              }

              event.consume()
              lock.replaceLock(newAction)
            }
          }

          MouseEvent.MOUSE_MOVED -> {

//            val picked = Nodes.pick(this@NodeEditor, event.sceneX, event.sceneY)
//            if (picked!=null) {
//              println("node -> $picked")
//            }

            val targetLocalPosition = active.screenToLocal(event.screenPosition)
            val sizeMode = SizeMode.guess(targetLocalPosition, active.size)
            if (sizeMode != SizeMode.INSIDE) {
              if (sizeMode!=null) {
                cursor = sizeMode.cursor()
                lock.replaceLock(Action.Focus(sizeMode))
              }
            } else {
              if (action.sizeMode!=SizeMode.INSIDE) {
                cursor = null
                lock.replaceLock(Action.Focus())
              }
            }
          }
        }
      } else {
        when (event.eventType) {
          MouseEvent.MOUSE_DRAGGED -> {
            event.consume()

            when (action) {
              is Action.Move -> {
                val diff = event.screenPosition - action.clickPosition
                active.layoutPosition = action.layoutPosition + active.screenDeltaToLocal(diff)
              }

              is Action.Resize -> {
                val diff = event.screenPosition - action.clickPosition
                val fixedDiff = active.screenDeltaToLocal(diff)
                val resizedBounds = SizeMode.resize(action.sizeMode, action.layout, fixedDiff)

                active.resizeTo(resizedBounds)
              }

              else -> {
                println(".. should not happen")
              }
            }
          }

          MouseEvent.MOUSE_RELEASED -> {
            event.consume()
            cursor = null

            lock.replaceLock(Action.Focus())
          }
        }
      }
    }
  }

  sealed class Action {
    data class Focus(val sizeMode: SizeMode? = null) : Action()
    data class Move(
      val clickPosition: Point2D,
      val layoutPosition: Point2D
    ) : Action()

    data class Resize(
      val clickPosition: Point2D,
      val sizeMode: SizeMode,
      val layout: LayoutBounds
    ) : Action()
  }

}
