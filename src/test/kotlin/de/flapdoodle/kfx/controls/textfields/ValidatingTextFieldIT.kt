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
package de.flapdoodle.kfx.controls.textfields

import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.scene.Scene
import javafx.stage.Stage
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import java.util.*
import java.util.function.Predicate

@ExtendWith(ApplicationExtension::class)
class ValidatingTextFieldIT {
  @Start
  private fun createElement(stage: Stage) {
    stage.scene = Scene(ValidatingTextField(Converters.validatingFor(Int::class, Locale.GERMANY)).apply {
      id = "testee"
    },200.0,200.0)
    stage.show()
  }

  @Test
  fun justShow(robot: FxRobot) {
    val typedTextField = robot.lookup(Predicate { it.id=="testee" }).queryAs(ValidatingTextField::class.java)

    assertThat(typedTextField.get()).isNull()
    assertThat(typedTextField.valueProperty().value).isNull()
    assertThat(typedTextField.lastExceptionProperty().value).isNull()

    typedTextField.textProperty().value = "123"
    assertThat(typedTextField.get()).isEqualTo(123)
    assertThat(typedTextField.valueProperty().value).isEqualTo(123)
    assertThat(typedTextField.lastExceptionProperty().value).isNull()

    typedTextField.textProperty().value = "123a"
    assertThat(typedTextField.get()).isNull()
    assertThat(typedTextField.valueProperty().value).isNull()
    assertThat(typedTextField.lastExceptionProperty().value).isNotNull()
  }
}