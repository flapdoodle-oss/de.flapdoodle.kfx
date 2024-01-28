package de.flapdoodle.kswing

import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JFrame



object App {
  @JvmStatic
  fun main(vararg args: String) {
    val frame = JFrame("My First GUI")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(300, 300)
    val button = JButton("Press")
    frame.contentPane.layout = BorderLayout()
    frame.contentPane.add(button, BorderLayout.NORTH) // Adds Button to content pane of frame
    frame.isVisible = true
  }
}