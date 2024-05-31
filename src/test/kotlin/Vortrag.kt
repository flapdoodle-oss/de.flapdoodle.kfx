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