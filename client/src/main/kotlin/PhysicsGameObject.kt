import vision.gears.webglmath.*
import kotlin.math.exp
import kotlin.math.PI
import kotlin.math.floor

open class PhysicsGameObject(
    vararg meshes: Mesh
) : GameObject(*meshes) {

    val velocity = Vec3()
    val acceleration = Vec3()
    val force = Vec3()

    var angularVelocity = 0.0f
    var angularAcceleration = 0.0f
    var torque = 0.0f

    var radius = 1.0f
    var invMass = 1.0f
    var invAngularMass = 1.0f

    val preCollisionActions = ArrayList<(PhysicsGameObject) -> Boolean>()
    val collisionActions = ArrayList<(PhysicsGameObject) -> Unit>()
    val specialCollisionActions = ArrayList<(PhysicsGameObject, MutableList<GameObject>, Float) -> Unit>()
    init {
        collisionActions.add(::doCollisionMove)
    }

    val forces = arrayListOf<Vec3>()

    open fun control(
        dt: Float,
        t: Float,
        keysPressed: Set<String>,
        gameObjects: List<GameObject>
    ): Boolean {
        return true;
    }

    fun collision(
        dt: Float,
        t: Float,
        keysPressed: Set<String>,
        gameObjects: List<GameObject>
    ): Boolean {
        gameObjects.forEach {
            if (it !is PhysicsGameObject) {
                return@forEach
            }
            if (it == this) {
                return@forEach
            }

            for (shouldContinue in preCollisionActions) {
                if (!shouldContinue(it)) return@forEach
            }

            for (action in specialCollisionActions) {
                action(it, gameObjects as MutableList<GameObject>, t)
            }

            for (action in collisionActions) {
                action(it)
            }
        }

        return true;
    }

    private fun doCollisionMove(it: PhysicsGameObject) {
        val diff = position - it.position
        val dist = diff.length()
        if (dist >= radius + it.radius) return

        val collisionNormal = diff.normalize()

        position += collisionNormal * invMass / (invMass + it.invMass) * dist * 0.01f
        it.position += collisionNormal * it.invMass / (invMass + it.invMass) * dist * -0.01f

        val collisionTangent = Vec3(-collisionNormal.y, collisionNormal.x, 0.0f)
        val relativeVelocity = velocity - it.velocity

        val restitutionCoeff = 0.8f
        val impulseLength = collisionNormal.dot(relativeVelocity) / (invMass + it.invMass) * (1.0f + restitutionCoeff)
        val restitution = collisionNormal * impulseLength
        velocity -= restitution * invMass
        it.velocity += restitution * it.invMass
    }

    override fun move(
        dt: Float,
        t: Float,
        keysPressed: Set<String>,
        gameObjects: List<GameObject>
    ): Boolean {
        control(dt, t, keysPressed, gameObjects)

        force.set(if (!forces.isEmpty()) forces.reduce { a, b -> a + b } else Vec3())

        acceleration.set(force * invMass)
        velocity += acceleration * dt
        position += velocity * dt

        angularAcceleration = torque * invAngularMass
        angularVelocity += angularAcceleration * dt
        roll += angularVelocity * dt

        velocity *= exp(-dt)
        angularVelocity *= exp(-dt)

        collision(dt, t, keysPressed, gameObjects)
        return true;
    }

}
