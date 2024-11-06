import vision.gears.webglmath.Vec3

class BlackHoleProjectile(projectileMesh: Mesh, player: Player, ahead: Vec3, gameObjects: List<GameObject>): Projectile(projectileMesh, player, ahead) {
    init {
        timeToDespawn = 20f
        invMass = 0.0001f
        preCollisionActions.add({
            if (it != player) {
                val gravDirection = this.position - it.position
                val invDistSquared = (1 / gravDirection.lengthSquared())
                it.forces.add(gravDirection.normalize() * invDistSquared)
            }
            false
        })
    }
}