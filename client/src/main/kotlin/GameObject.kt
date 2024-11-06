import vision.gears.webglmath.*

open class GameObject(
  vararg val meshes : Mesh
   ) : UniformProvider("gameObject") {

  val position = Vec3()
  var roll = 0.0f
  val scale = Vec3(1.0f, 1.0f, 1.0f)

  val modelMatrix by Mat4()
    var spriteScale by Vec1()
    var spritesPerSheet = 1f
    val offset by Vec3()

  init { 
    addComponentsAndGatherUniforms(*meshes)
  }

  fun update() {
    modelMatrix.set().
      scale(scale).
      rotate(roll).
      translate(position)

      // TODO: refuses to recognize as uniform??
      spriteScale.set(1/spritesPerSheet)
  }

  open fun move(
      dt : Float = 0.016666f,
      t : Float = 0.0f,
      keysPressed : Set<String> = emptySet<String>(),
      gameObjects : List<GameObject> = emptyList<GameObject>()
      ) : Boolean {
    return true;
  }

}
