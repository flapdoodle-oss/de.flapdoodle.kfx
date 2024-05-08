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
package de.flapdoodle.kfx.sampler

import de.flapdoodle.kfx.strokes.LinearGradientSampler
import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.shape.ArcTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import javafx.stage.Stage


class ArcToSampler {

  class ArcExample : Application() {
    override fun start(stage: Stage) {
      //Creating an object of the class Path
      val path: Path = Path()


      //Moving to the starting point
      val moveTo = MoveTo()
      moveTo.x = 250.0
      moveTo.y = 250.0


      //Instantiating the arcTo class
      val arcTo = ArcTo(50.0, 50.0, 0.0, 300.0, 200.0, true, false)



      //setting properties of the path element arc
//      arcTo.x = 300.0
//      arcTo.y = 50.0
//
//      arcTo.radiusX = 50.0
//      arcTo.radiusY = 50.0


      //Adding the path elements to Observable list of the Path class
      path.getElements().add(moveTo)
      path.getElements().add(arcTo)


      //Creating a Group object
      val root: Group = Group(path)


      //Creating a scene object
      val scene = Scene(root, 600.0, 300.0)


      //Setting title to the Stage
      stage.title = "Drawing an arc through a path"


      //Adding scene to the stage
      stage.scene = scene


      //Displaying the contents of the stage
      stage.show()
    }
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(ArcExample::class.java, *args)
    }
  }
}