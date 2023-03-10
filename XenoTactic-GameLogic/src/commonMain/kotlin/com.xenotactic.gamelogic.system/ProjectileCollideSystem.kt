package com.xenotactic.gamelogic.system

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.*
import kotlin.time.Duration

class ProjectileCollideSystem(
    val world: World
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            ProjectileComponent::class,
            MutableCenterPositionComponent::class,
            TargetingComponent::class,
            VelocityComponent::class,
            ProjectileDamageComponent::class,
            CollideWithTargetComponent::class
        ),
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val targetingComponent = world[it, TargetingComponent::class]
            val projectileDamageComponent = world[it, ProjectileDamageComponent::class]

            val monsterHealthComponent = world[targetingComponent.targetEntityId, HealthComponent::class]
            monsterHealthComponent.health -= projectileDamageComponent.damage

            world.modifyEntity(it) {
                removeThisEntity()
            }
        }
    }
}