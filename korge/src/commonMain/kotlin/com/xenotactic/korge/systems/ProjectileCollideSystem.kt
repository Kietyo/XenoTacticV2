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
            ProjectileComponent::class, MutableCenterPositionComponent::class,
            TargetingComponent::class, VelocityComponent::class,
            DamageComponent::class,
            CollideWithTargetComponent::class
        ),
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val targetingComponent = world[it, TargetingComponent::class]
            val damageComponent = world[it, DamageComponent::class]

            val monsterHealthComponent = world[targetingComponent.targetEntityId, HealthComponent::class]
            monsterHealthComponent.health -= damageComponent.damage

            world.modifyEntity(it) {
                remove()
            }
        }
    }
}