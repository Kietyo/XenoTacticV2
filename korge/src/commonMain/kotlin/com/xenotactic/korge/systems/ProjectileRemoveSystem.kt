package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import kotlin.time.Duration

class ProjectileRemoveSystem(
    val world: World
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            com.xenotactic.gamelogic.components.ProjectileComponent::class, com.xenotactic.gamelogic.components.MutableCenterPositionComponent::class,
            com.xenotactic.gamelogic.components.TargetingComponent::class, com.xenotactic.gamelogic.components.VelocityComponent::class
        ),
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val targetingComponent = world[it, com.xenotactic.gamelogic.components.TargetingComponent::class]

            // The target no longer exists in the world.
            if (!world.containsEntity(targetingComponent.targetEntityId)) {
                world.modifyEntity(it) {
                    remove()
                }
            }
        }
    }
}