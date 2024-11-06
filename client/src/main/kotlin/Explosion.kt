import vision.gears.webglmath.Vec3

class Explosion(explosionMesh: Mesh, blownUpPos: Vec3) : GameObject(explosionMesh) {
    init {
        spritesPerSheet = 6f
        position.set(blownUpPos)
    }

    override fun move(dt: Float, t: Float, keysPressed: Set<String>, gameObjects: List<GameObject>): Boolean {
        return true
    }
}