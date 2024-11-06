import org.w3c.dom.HTMLCanvasElement
import org.khronos.webgl.WebGLRenderingContext as GL //# GL# we need this for the constants declared ˙HUN˙ a constansok miatt kell
import kotlin.js.Date
import vision.gears.webglmath.UniformProvider

class Scene (
  val gl : WebGL2RenderingContext)  : UniformProvider("scene") {

  val vsTextured = Shader(gl, GL.VERTEX_SHADER, "textured-vs.glsl")
  val vsBackground = Shader(gl, GL.VERTEX_SHADER, "background-vs.glsl")  
  val fsTextured = Shader(gl, GL.FRAGMENT_SHADER, "textured-fs.glsl")
  val texturedProgram = Program(gl, vsTextured, fsTextured)
  val backgroundProgram = Program(gl, vsBackground, fsTextured)

  //TODO: create various materials with different solidColor settings
  val fighterMaterial = Material(texturedProgram).apply{
    this["colorTexture"]?.set(Texture2D(gl, "media/fighter.png"))
  }
  val backgroundMaterial = Material(backgroundProgram).apply{
    this["colorTexture"]?.set(Texture2D(gl, "media/nebula.jpg"))
  }
  val asteroidMaterial = Material(texturedProgram).apply{
    this["colorTexture"]?.set(Texture2D(gl, "media/asteroid.png"))
  }
  val projectileMaterial = Material(texturedProgram).apply {
    this["colorTexture"]?.set(Texture2D(gl, "media/plasma.png"))
  }
  val explosionMaterial = Material(texturedProgram).apply{
    this["colorTexture"]?.set(Texture2D(gl, "media/boom.png"))
  }

  val texturedQuadGeometry = TexturedQuadGeometry(gl)
  val backgroundMesh = Mesh(backgroundMaterial, texturedQuadGeometry)
  val avatarMesh = Mesh(fighterMaterial, texturedQuadGeometry)
  val asteroidMesh = Mesh(asteroidMaterial, texturedQuadGeometry)
  val projectileMesh = Mesh(projectileMaterial, texturedQuadGeometry)
  val explosionMesh = Mesh(explosionMaterial, texturedQuadGeometry)
  
  val camera = OrthoCamera().apply{
    position.set(1f, 1f)
    windowSize.set(20f, 20f)
    updateViewProjMatrix()
  }

  var gameObjects = ArrayList<GameObject>()
  init {
    gameObjects += GameObject(backgroundMesh)
  }

  val player = Player(avatarMesh, projectileMesh, explosionMesh)
  init {
    gameObjects += player
  }

  val platform = PhysicsGameObject(asteroidMesh).apply {
    invMass = 0f
  }
  init {
    platform.position.y = -10f
    gameObjects += platform
  }

  fun resize(canvas : HTMLCanvasElement) {
    gl.viewport(0, 0, canvas.width, canvas.height)//#viewport# tell the rasterizer which part of the canvas to draw to ˙HUN˙ a raszterizáló ide rajzoljon
    camera.setAspectRatio(canvas.width.toFloat()/canvas.height)
  }

  val timeAtFirstFrame = Date().getTime()
  var timeAtLastFrame =  timeAtFirstFrame
  //TODO: add property reflecting uniform scene.time
  //TODO: add all programs as child components

  @Suppress("UNUSED_PARAMETER")
  fun update(keysPressed : Set<String>) {
    val timeAtThisFrame = Date().getTime() 
    val dt = (timeAtThisFrame - timeAtLastFrame).toFloat() / 1000.0f
    val t = (timeAtThisFrame - timeAtFirstFrame).toFloat() / 1000.0f
    //TODO: set property time (reflecting uniform scene.time) 
    timeAtLastFrame = timeAtThisFrame
    
    camera.position.set(player.position)
    camera.updateViewProjMatrix()

    gl.clearColor(0.3f, 0.0f, 0.3f, 1.0f)//## red, green, blue, alpha in [0, 1]
    gl.clearDepth(1.0f)//## will be useful in 3D ˙HUN˙ 3D-ben lesz hasznos
    gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)//#or# bitwise OR of flags

    gl.enable(GL.BLEND)
    gl.blendFunc(
      GL.SRC_ALPHA,
      GL.ONE_MINUS_SRC_ALPHA)

    gameObjects.forEach{
      it.move(dt, t, keysPressed, gameObjects)
    }
    gameObjects.forEach{
      it.update()
    }
    gameObjects.forEach{
      it.draw(this, camera)
    }
  }
}
