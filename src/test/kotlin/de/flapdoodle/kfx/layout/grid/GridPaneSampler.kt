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
package de.flapdoodle.kfx.layout.grid

import de.flapdoodle.kfx.css.bindCss
import javafx.application.Application
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.stage.Stage

class GridPaneSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {
      stage.scene = Scene(GridPane().apply {
        bindCss(GridPaneSampler::class,"sampler")
        padding = Insets(60.0)

        verticalSpace.set(10.0)
//        horizontalSpace.set(20.0)

        add(Button("(0,0)").also {
          it.minWidth = 20.0
          it.maxWidth = 100.0
        }, Pos(0, 0))
        add(Button("-(1,0)"), Pos(1, 0), horizontalPosition = HPos.RIGHT)
        add(Button("(1,1)").also {
          it.maxHeight = 100.0
        }, Pos(1, 1))
        children.add(GridPane().apply {
          GridPane.setPosition(this, 0, 1)
          val that = this

          verticalSpace.set(10.0)
          horizontalSpace.set(20.0)

          that.add(Button("(0,1)(0,0)").also {
            it.minWidth = 20.0
            it.maxWidth = 100.0
          }, 0, 0)
          that.add(Button("(0,1)-(1,0)"), 1, 0, horizontalPosition = HPos.RIGHT)
          that.add(Button("(0,1)(1,1)").also {
            it.maxHeight = 100.0
          }, 1, 1)
        })

        columnWeight(0, 1.0)
        columnWeight(1, 2.0)
//        rowWeight(0, 4.0)
//        rowWeight(1, 1.0)
        rowWeights(4.0, 1.0, 1.0)

        add(Button("(0..1,2)").also {
          it.maxWidth = 300.0
        }, Pos(0,2,2,1))
      })
      stage.show()
    }
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Sample::class.java, *args)
    }
  }
}