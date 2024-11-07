import vision.gears.webglmath.Vec3
import kotlin.math.floor
import kotlin.math.roundToInt

class Explosion(explosionMesh: Mesh, blownUpPos: Vec3, val startTime: Float) : GameObject(explosionMesh) {
    init {
        spritesPerRow = 6f
        position.set(blownUpPos)
    }

    override fun move(dt: Float, t: Float, keysPressed: Set<String>, gameObjects: List<GameObject>): Boolean {
        if (currentSprite >= 35) {
            (gameObjects as MutableList<GameObject>).remove(this)
        }
        currentSprite = ((t - startTime) * 24).roundToInt()
        return true
    }
}