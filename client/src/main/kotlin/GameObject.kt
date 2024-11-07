import vision.gears.webglmath.*
import kotlin.math.floor

open class GameObject(
  vararg val meshes : Mesh
   ) : UniformProvider("gameObject") {

  val position = Vec3()
  var roll = 0.0f
  val scale = Vec3(1.0f, 1.0f, 1.0f)

  val modelMatrix by Mat4()

    var spriteScale by Vec1()
    var spritesPerRow = 1f

    val offset by Vec2()
    var currentSprite = 0

  init { 
    addComponentsAndGatherUniforms(*meshes)
  }

  fun update() {
    modelMatrix.set().
      scale(scale).
      rotate(roll).
      translate(position)

      spriteScale.set(1/spritesPerRow)
      offset.set(currentSprite % spritesPerRow, floor(currentSprite / spritesPerRow))
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
