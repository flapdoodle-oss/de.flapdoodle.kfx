package de.flapdoodle.kfx.controls.fields

import de.flapdoodle.kfx.controls.labels.ValidatedLabel
import de.flapdoodle.kfx.converters.ReadOnlyStringConverter
import javafx.application.Application
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.FlowPane
import javafx.stage.Stage
import java.util.*

class ValidatingComboBoxSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {
      val locales = Locale.getISOCountries().map { Locale("", it) }

      stage.scene = Scene(FlowPane(Orientation.VERTICAL).apply {
        val testee = ValidatingComboBox<Locale>(
          values = locales,
          default = Locale.getDefault(),
          initialConverter = ReadOnlyStringConverter.with { it -> it?.displayName ?: "--" },
          validate = { null }
        )

        val result = ValidatedLabel<Locale>(ReadOnlyStringConverter.with { it -> it?.displayName ?: "--" })
        
        children.add(testee)
        children.add(result)
        children.add(Button("OK"))
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