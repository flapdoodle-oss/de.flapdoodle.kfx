package de.flapdoodle.kfx.controls.textfields

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
import java.util.function.Predicate

@ExtendWith(ApplicationExtension::class)
class TypedTextFieldIT {
  @Start
  private fun createElement(stage: Stage) {
    stage.scene = Scene(TypedTextField(Int::class).apply {
      id = "testee"
    },200.0,200.0)
    stage.show()
  }

  @Test
  fun justShow(robot: FxRobot) {
    val typedTextField = robot.lookup(Predicate { it.id=="testee" }).queryAs(TypedTextField::class.java)

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