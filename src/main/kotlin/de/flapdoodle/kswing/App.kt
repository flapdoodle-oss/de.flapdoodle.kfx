package de.flapdoodle.kswing

import de.flapdoodle.kfx.layout.grid.WeightedSize
import de.flapdoodle.kswing.layout.DebugLayoutDelegate
import de.flapdoodle.kswing.layout.grid.GridConstraint
import de.flapdoodle.kswing.layout.grid.WeightGridLayout
import javafx.geometry.HPos
import javafx.geometry.VPos
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.UIManager


object App {
  @JvmStatic
  fun main(vararg args: String) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    val frame = JFrame("My First GUI")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(300, 300)

    frame.contentPane.layout = BorderLayout()
    frame.contentPane.add(JButton("North"), BorderLayout.NORTH)
//    frame.contentPane.add(JButton("Center"), BorderLayout.CENTER)
//    frame.contentPane.add(JButton("C2"), BorderLayout.CENTER)
    frame.contentPane.add(JButton("South"), BorderLayout.SOUTH)
    frame.contentPane.add(JButton("E"), BorderLayout.EAST)
    frame.contentPane.add(JButton("W"), BorderLayout.WEST)

    val center = JPanel()
    val layout = WeightGridLayout()
    layout.debugLayoutLevel(0)
    layout.setColumnWeight(0, 1.0)
    layout.setColumnWeight(1, 2.0)
    layout.setRowWeight(0, 1.0)
    layout.setRowWeight(1, 1.0)
    center.layout = layout//.logCalls()
    frame.contentPane.add(center, BorderLayout.CENTER)
    center.add(JButton("A").also { it.maximumSize = Dimension(100, 100) }, GridConstraint(0,0))
    center.add(JButton("B"), GridConstraint(1,0, HPos.RIGHT, VPos.BOTTOM))
    center.add(JButton("C"), GridConstraint(0,1))
    center.add(JButton("D").also { it.maximumSize = Dimension(100, 100) }, GridConstraint(1,1))

    frame.isVisible = true
  }
}