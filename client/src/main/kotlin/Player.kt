import vision.gears.webglmath.Vec3
import kotlin.math.cos
import kotlin.math.sin

class Player(mesh: Mesh, val projectileMesh: Mesh) : PhysicsGameObject(mesh){
    val gravity = Vec3(0f, -9.8f)
    val controlVec = Vec3()
    var projectileCooldown = 0f

    val projectiles = arrayListOf<Projectile>()

    override fun move(dt: Float, t: Float, keysPressed: Set<String>, gameObjects: List<GameObject>): Boolean {
        force.set(gravity + controlVec)
        controlVec.set()

        return super.move(dt, t, keysPressed, gameObjects)
    }

    override fun control(dt: Float, t: Float, keysPressed: Set<String>, gameObjects: List<GameObject>): Boolean {
        attemptJump(keysPressed)
        doSideMovement(keysPressed)
        shootProjectile(keysPressed, dt, gameObjects as MutableList<GameObject>)

        return super.control(dt, t, keysPressed, gameObjects)
    }

    private fun shootProjectile(keysPressed: Set<String>, dt: Float, gameObjects: MutableList<GameObject>) {
        if (projectileCooldown > 0)
            projectileCooldown -= dt
        else projectileCooldown = 0f

        if ("SPACE" !in keysPressed) return
        // you have tried to shoot

        if (projectileCooldown > 0) return
        // you are allowed to shoot

        val ahead = Vec3 (cos (roll), sin (roll))
        val projectile = Projectile(projectileMesh, this, ahead)
        projectiles += projectile
        gameObjects += projectile
        projectileCooldown += 1f
    }

    private fun skipProjectileCollisions(it: PhysicsGameObject): Boolean {
        if (it in projectiles) {
            return false
        }
        return true
    }
    init {
        preCollisionActions.add(::skipProjectileCollisions)
    }

    private fun doSideMovement(keysPressed: Set<String>) {
        var sideX = 0f
        if ("A" in keysPressed) {
            sideX -= 10f
        }
        if ("D" in keysPressed) {
            sideX += 10f
        }
        controlVec.x = sideX
    }

    fun attemptJump(keysPressed: Set<String>) {
        if (velocity.y > 0) {
            // you are not allowed to jump if you are already going up!
            return
        }

        if ("W" in keysPressed) {
            velocity.y = -gravity.y
            angularVelocity = 10f
        }
        return
    }
}