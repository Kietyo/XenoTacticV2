package com.xenotactic.gamelogic.system

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.MutableCenterPositionComponent
import com.xenotactic.gamelogic.components.ProjectileComponent
import com.xenotactic.gamelogic.components.TargetingComponent
import com.xenotactic.gamelogic.components.VelocityComponent
import kotlin.time.Duration

class ProjectileRemoveSystem(
    val world: World
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            ProjectileComponent::class,
            MutableCenterPositionComponent::class,
            TargetingComponent::class,
            VelocityComponent::class
        ),
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val targetingComponent = world[it, TargetingComponent::class]

            // The target no longer exists in the world.
            if (!world.containsEntity(targetingComponent.targetEntityId)) {
                world.modifyEntity(it) {
                    removeThisEntity()
                }
            }
        }
    }
}