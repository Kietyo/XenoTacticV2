package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.korge.components.HealthComponent
import com.xenotactic.korge.components.MaxHealthComponent
import com.xenotactic.korge.components.MonsterComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.MonsterDeathsEvent
import kotlin.time.Duration

class MonsterDeathSystem(
    val engine: Engine
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            MonsterComponent::class,
            HealthComponent::class, MaxHealthComponent::class
        )
    )
    val world = engine.gameWorld.world

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val healthComponent = world[it, HealthComponent::class]
            val maxHealthComponent = world[it, MaxHealthComponent::class]
            var numDeaths = 0
            while (healthComponent.health <= 0.0) {
                healthComponent.health += maxHealthComponent.maxHealth
                numDeaths++
            }
            engine.eventBus.send(MonsterDeathsEvent(numDeaths))
        }
    }
}