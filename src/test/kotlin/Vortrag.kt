import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.bindings.list.MappingListChangeListener
import de.flapdoodle.kfx.bindings.list.MappingListChangeListenerTest
import de.flapdoodle.kfx.controls.bettertable.TableSampler
import de.flapdoodle.kfx.controls.charts.LinearChartSampler
import de.flapdoodle.kfx.controls.charts.SmallChartSampler
import de.flapdoodle.kfx.controls.fields.ValidatingColoredTextField
import de.flapdoodle.kfx.controls.fields.ValidatingColoredTextFieldSampler
import de.flapdoodle.kfx.controls.grapheditor.GraphEditorSampler
import de.flapdoodle.kfx.controls.table.SlimTable2Sampler
import de.flapdoodle.kfx.controls.table.SlimTableSampler
import de.flapdoodle.kfx.sampler.BoundsPlaygroundSampler
import de.flapdoodle.kfx.sampler.SpreadsheetSampler
import de.flapdoodle.kfx.strokes.LinearGradientSampler
import de.flapdoodle.kfx.usecase.tab2.Tab2Sampler

object Vortrag {
  val the_good = listOf(
    BoundsPlaygroundSampler::class,

    LinearChartSampler::class, SmallChartSampler::class /* SmallChart.css */,

    SpreadsheetSampler::class, SlimTableSampler::class, SlimTable2Sampler::class, TableSampler::class,

    GraphEditorSampler::class, Tab2Sampler::class
  )

  val the_bad = listOf(
    MappingListChangeListenerTest::class,
    LinearGradientSampler::class
  )

  val the_ugly = listOf(
    ValidatingColoredTextFieldSampler::class
  )
}