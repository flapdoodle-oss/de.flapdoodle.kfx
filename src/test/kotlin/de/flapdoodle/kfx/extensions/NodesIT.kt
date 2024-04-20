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
package de.flapdoodle.kfx.extensions

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.stage.Stage
import javafx.util.Subscription
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
class NodesIT {
  @Start
  private fun createElement(stage: Stage) {
    val root = Pane()

    root.styleClass.add("root")
    stage.scene = Scene(root,200.0,200.0)
    stage.show()
  }

  @Test
  fun addAndRemoveShouldTriggerAttachDetachListener(robot: FxRobot) {
    val root = robot.lookup(".root").queryAs(Pane::class.java)

    val button = Button("x")

    var counter = 0
    button.unsubscribeOnDetach {
      counter += 1
      Subscription {
        counter -= 1
      }
    }

    assertThat(counter).isEqualTo(0)
    robot.interact {
      root.children.add(button)
    }
    assertThat(counter).isEqualTo(1)
    robot.interact {
      root.children.removeAll(button)
    }
    assertThat(counter).isEqualTo(0)
    robot.interact {
      root.children.add(button)
    }
    assertThat(counter).isEqualTo(1)
  }
}