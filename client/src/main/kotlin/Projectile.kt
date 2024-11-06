import vision.gears.webglmath.Vec3

class Projectile(projectileMesh: Mesh, player: Player, ahead: Vec3) : PhysicsGameObject(projectileMesh) {
    init {
        radius = 0f
        position.set(player.position + ahead)
        velocity.set(ahead * 10f)
    }
}