package com.xenotactic.gamelogic.system

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.gamelogic.components.HealthComponent
import com.xenotactic.gamelogic.components.MaxHealthComponent
import com.xenotactic.gamelogic.components.MonsterComponent
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.events.MonsterDeathsEvent
import kotlin.time.Duration

class MonsterDeathSystem(
    val engine: Engine
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            MonsterComponent::class,
            HealthComponent::class,
            MaxHealthComponent::class
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