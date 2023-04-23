package de.flapdoodle.kfx.sampler

import javafx.application.Application
import javafx.application.ConditionalFeature
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.geometry.Point3D
import javafx.geometry.VPos
import javafx.scene.*
import javafx.scene.input.MouseEvent
import javafx.scene.input.PickResult
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.*
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import javafx.stage.Stage

/**
 * A Simple Picking Example
 */
class PickMesh3DSampler : Application() {
  var meshView: MeshView? = null
  override fun start(stage: Stage) {
    if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
      throw RuntimeException("*** ERROR: common conditional SCENE3D is not supported")
    }
    stage.title = "JavaFX 3D Simple Picking demo"
    val camera = PerspectiveCamera()
    val pickResultPanel = createOverlay()
    val root = Group()
    root.children.addAll(pickResultPanel, createSubScene(camera))
    val scene = Scene(root, 800.0, 800.0)
    scene.fill = Color.color(0.2, 0.2, 0.2)
    scene.onKeyTyped = EventHandler { e ->
      when (e.character) {
        "L" -> {
          System.err.print("L ")
          val wireframe = meshView!!.drawMode == DrawMode.LINE
          meshView!!.drawMode = if (wireframe) DrawMode.FILL else DrawMode.LINE
        }
      }
    }
    stage.scene = scene
    stage.show()
    stage.requestFocus()
  }

  private fun createSimpleMesh(): Node {
    val triangleMesh = buildTriangleMesh(2, 2, 30f)
    meshView = MeshView(triangleMesh)
    activateShape(meshView!!, "Simple Mesh")
    meshView!!.drawMode = DrawMode.FILL
    meshView!!.cullFace = CullFace.NONE
    val material = PhongMaterial()
    material.diffuseColor = Color.GOLD
    material.specularColor = Color.rgb(30, 30, 30)
    meshView!!.material = material
    val group: Node = Group(meshView)
    group.translateX = 550.0
    group.translateY = 550.0
    return group
  }

  private fun activateShape(shape: Shape3D, name: String) {
    shape.id = name
    val moveHandler = EventHandler<MouseEvent> { event ->
      val res = event.pickResult
      setState(res)
      event.consume()
    }
    shape.onMouseMoved = moveHandler
    shape.onMouseDragOver = moveHandler
    shape.onMouseEntered = EventHandler { event ->
      val res = event.pickResult
      if (res == null) {
        System.err.println("Mouse entered has not pickResult")
      }
      setState(res)
    }
    shape.onMouseExited = EventHandler { event ->
      val res = event.pickResult
      if (res == null) {
        System.err.println("Mouse exited has not pickResult")
      }
      setState(res)
      event.consume()
    }
  }

  var caption: Text? = null
  var data: Text? = null
  private fun createOverlay(): Node {
    val hBox = HBox(10.0)
    caption = Text("Node:\n\nPoint:\nTexture Coord:\nFace:\nDistance:")
    caption!!.font = Font.font("Times New Roman", 18.0)
    caption!!.textOrigin = VPos.TOP
    caption!!.textAlignment = TextAlignment.RIGHT
    data = Text("-- None --\n\n\n\n")
    data!!.font = Font.font("Times New Roman", 18.0)
    data!!.textOrigin = VPos.TOP
    data!!.textAlignment = TextAlignment.LEFT
    val rect = Rectangle(300.0, 150.0, Color.color(0.2, 0.5, 0.3, 0.8))
    hBox.children.addAll(caption, data)
    return Group(rect, hBox)
  }

  private fun createSubScene(camera: Camera): SubScene {
    val simpleMesh = createSimpleMesh()
    val parent = Group(simpleMesh)
    parent.translateZ = 600.0
    parent.translateX = -150.0
    parent.translateY = -200.0
    parent.scaleX = 0.8
    parent.scaleY = 0.8
    parent.scaleZ = 0.8
    val pointLight = PointLight(Color.ANTIQUEWHITE)
    pointLight.translateX = 100.0
    pointLight.translateY = 100.0
    pointLight.translateZ = -300.0
    val root = Group(parent, pointLight, Group(camera))
    root.depthTest = DepthTest.ENABLE
    val subScene = SubScene(root, 800.0, 800.0, true, SceneAntialiasing.BALANCED)
    subScene.camera = camera
    subScene.fill = Color.TRANSPARENT
    subScene.id = "SubScene"
    return subScene
  }

  fun setState(result: PickResult?) {
    if (result!!.intersectedNode == null) {
      data!!.text = """
            Scene
            
            ${point3DToString(result.intersectedPoint)}
            ${point2DToString(result.intersectedTexCoord)}
            ${result.intersectedFace}
            ${String.format("%.1f", result.intersectedDistance)}
            """.trimIndent()
    } else {
      data!!.text = """
            ${result.intersectedNode.id}
            ${getCullFace(result.intersectedNode)}
            ${point3DToString(result.intersectedPoint)}
            ${point2DToString(result.intersectedTexCoord)}
            ${result.intersectedFace}
            ${String.format("%.1f", result.intersectedDistance)}
            """.trimIndent()
    }
  }

  companion object {
    const val minX = -10f
    const val minY = -10f
    const val maxX = 10f
    const val maxY = 10f
    fun buildTriangleMesh(subDivX: Int, subDivY: Int, scale: Float): TriangleMesh {
      val pointSize = 3
      val texCoordSize = 2
      // 3 point indices and 3 texCoord indices per triangle
      val faceSize = 6
      val numDivX = subDivX + 1
      val numVerts = (subDivY + 1) * numDivX
      val points = FloatArray(numVerts * pointSize)
      val texCoords = FloatArray(numVerts * texCoordSize)
      val faceCount = subDivX * subDivY * 2
      val faces = IntArray(faceCount * faceSize)

      // Create points and texCoords
      for (y in 0..subDivY) {
        val dy = y.toFloat() / subDivY
        val fy = ((1 - dy) * minY + dy * maxY).toDouble()
        for (x in 0..subDivX) {
          val dx = x.toFloat() / subDivX
          val fx = ((1 - dx) * minX + dx * maxX).toDouble()
          var index = y * numDivX * pointSize + x * pointSize
          points[index] = fx.toFloat() * scale
          points[index + 1] = fy.toFloat() * scale
          points[index + 2] = 0.0f
          index = y * numDivX * texCoordSize + x * texCoordSize
          texCoords[index] = dx
          texCoords[index + 1] = dy
        }
      }

      // Create faces
      for (y in 0 until subDivY) {
        for (x in 0 until subDivX) {
          val p00 = y * numDivX + x
          val p01 = p00 + 1
          val p10 = p00 + numDivX
          val p11 = p10 + 1
          val tc00 = y * numDivX + x
          val tc01 = tc00 + 1
          val tc10 = tc00 + numDivX
          val tc11 = tc10 + 1
          var index = (y * subDivX * faceSize + x * faceSize) * 2
          faces[index + 0] = p00
          faces[index + 1] = tc00
          faces[index + 2] = p10
          faces[index + 3] = tc10
          faces[index + 4] = p11
          faces[index + 5] = tc11
          index += faceSize
          faces[index + 0] = p11
          faces[index + 1] = tc11
          faces[index + 2] = p01
          faces[index + 3] = tc01
          faces[index + 4] = p00
          faces[index + 5] = tc00
        }
      }
      val triangleMesh = TriangleMesh()
      triangleMesh.points.setAll(*points)
      triangleMesh.texCoords.setAll(*texCoords)
      triangleMesh.faces.setAll(*faces)
      return triangleMesh
    }

    @JvmStatic
    fun main(args: Array<String>) {
      launch(*args)
    }

    private fun point3DToString(pt: Point3D?): String {
      return if (pt == null) {
        "null"
      } else String.format("%.1f; %.1f; %.1f", pt.x, pt.y, pt.z)
    }

    private fun point2DToString(pt: Point2D?): String {
      return if (pt == null) {
        "null"
      } else String.format("%.2f; %.2f", pt.x, pt.y)
    }

    private fun getCullFace(n: Node): String {
      return if (n is Shape3D) {
        "(CullFace." + n.cullFace + ")"
      } else ""
    }
  }
}