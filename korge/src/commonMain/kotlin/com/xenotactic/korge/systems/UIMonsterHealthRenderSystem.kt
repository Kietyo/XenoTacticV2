package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.HealthComponent
import com.xenotactic.gamelogic.components.MaxHealthComponent
import com.xenotactic.gamelogic.components.MonsterComponent
import com.xenotactic.gamelogic.components.UIHealthBarComponent
import kotlin.time.Duration

class UIMonsterHealthRenderSystem(
    val world: World
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            MonsterComponent::class,
            HealthComponent::class,
            MaxHealthComponent::class,
            UIHealthBarComponent::class
        )
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val healthComponent = world[it, HealthComponent::class]
            val maxHealthComponent = world[it, MaxHealthComponent::class]
            val uiHealthBarComponent = world[it, UIHealthBarComponent::class]

            uiHealthBarComponent.view.maximum = maxHealthComponent.maxHealth.toFloat()
            uiHealthBarComponent.view.current = healthComponent.health.toFloat()
        }
    }
}