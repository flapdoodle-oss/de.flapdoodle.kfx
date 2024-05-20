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
package de.flapdoodle.kfx.controls.fields

import javafx.application.Application
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.layout.Border
import javafx.scene.layout.FlowPane
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.Stage


class MultiColorTextSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {
      val flow = TextFlow()
      val log = ">> Sample passed \n"
      val t1: Text = Text()
      t1.setStyle("-fx-fill: #4F8A10;-fx-font-weight:bold;")
      t1.setText(log)
      val t2: Text = Text()
      t2.setStyle("-fx-fill: RED;-fx-font-weight:normal;")
      t2.setText(log)
      flow.children.addAll(t1, t2)

      stage.scene = Scene(FlowPane(Orientation.VERTICAL).apply {
        children.add(flow)
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
