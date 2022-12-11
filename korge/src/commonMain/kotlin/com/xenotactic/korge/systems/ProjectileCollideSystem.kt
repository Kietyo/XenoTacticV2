package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.korge.components.*
import kotlin.time.Duration

class ProjectileCollideSystem(
    val world: World
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            com.xenotactic.gamelogic.components.ProjectileComponent::class, com.xenotactic.gamelogic.components.MutableCenterPositionComponent::class,
            com.xenotactic.gamelogic.components.TargetingComponent::class, com.xenotactic.gamelogic.components.VelocityComponent::class,
            com.xenotactic.gamelogic.components.DamageComponent::class,
            com.xenotactic.gamelogic.components.CollideWithTargetComponent::class
        ),
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val targetingComponent = world[it, com.xenotactic.gamelogic.components.TargetingComponent::class]
            val damageComponent = world[it, com.xenotactic.gamelogic.components.DamageComponent::class]

            val monsterHealthComponent = world[targetingComponent.targetEntityId, com.xenotactic.gamelogic.components.HealthComponent::class]
            monsterHealthComponent.health -= damageComponent.damage

            world.modifyEntity(it) {
                remove()
            }
        }
    }
}