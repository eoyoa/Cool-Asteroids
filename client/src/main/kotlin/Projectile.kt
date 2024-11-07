import vision.gears.webglmath.Vec3

open class Projectile(projectileMesh: Mesh, player: Player, ahead: Vec3, val explosionMesh: Mesh) : PhysicsGameObject(projectileMesh) {
    var timeToDespawn = 10f

    init {
        radius = 1f
        position.set(player.position + ahead)
        velocity.set(ahead * 10f)
        preCollisionActions.add {
            it != player
        }
        collisionActions.clear()
        specialCollisionActions.add { it, gameObjects, t ->
            val diff = position - it.position
            val dist = diff.length()

            if (it !is BlackHoleProjectile && dist < radius + it.radius) {
                gameObjects.remove(it)
                val boom = Explosion(explosionMesh, it.position, t)
                gameObjects.add(boom)
            }
        }
    }

    override fun move(dt: Float, t: Float, keysPressed: Set<String>, gameObjects: List<GameObject>): Boolean {
        timeToDespawn -= dt
        if (timeToDespawn < 0f) (gameObjects as MutableList<GameObject>).remove(this)

        return super.move(dt, t, keysPressed, gameObjects)
    }
}