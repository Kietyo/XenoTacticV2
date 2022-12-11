package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.HealthComponent
import com.xenotactic.gamelogic.components.MaxHealthComponent
import com.xenotactic.gamelogic.components.MonsterComponent
import com.xenotactic.gamelogic.components.UIHealthBarComponent
import kotlin.time.Duration

class MonsterHealthRenderSystem(
    val world: World
) : System() {
    override val familyConfiguration: FamilyConfiguration
        = FamilyConfiguration(
        allOfComponents = setOf(
            com.xenotactic.gamelogic.components.MonsterComponent::class,
            com.xenotactic.gamelogic.components.HealthComponent::class,
            com.xenotactic.gamelogic.components.MaxHealthComponent::class,
            com.xenotactic.gamelogic.components.UIHealthBarComponent::class
            )
        )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val healthComponent = world[it, com.xenotactic.gamelogic.components.HealthComponent::class]
            val maxHealthComponent = world[it, com.xenotactic.gamelogic.components.MaxHealthComponent::class]
            val uiHealthBarComponent = world[it, com.xenotactic.gamelogic.components.UIHealthBarComponent::class]

            uiHealthBarComponent.view.maximum = maxHealthComponent.maxHealth
            uiHealthBarComponent.view.current = healthComponent.health

        }
    }
}