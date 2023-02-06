package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import kotlin.time.Duration

class ReloadSystem(
    val world: World
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            com.xenotactic.gamelogic.components.WeaponSpeedComponent::class,
            com.xenotactic.gamelogic.components.ReloadDowntimeComponent::class,
        ),
        noneOfComponents = setOf(com.xenotactic.gamelogic.components.ReadyToAttackComponent::class)
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val weaponSpeedComponent = world[it, com.xenotactic.gamelogic.components.WeaponSpeedComponent::class]
            val reloadDowntimeComponent = world[it, com.xenotactic.gamelogic.components.ReloadDowntimeComponent::class]
            reloadDowntimeComponent.currentDowntimeMillis += deltaTime.inWholeMilliseconds
            require(reloadDowntimeComponent.currentDowntimeMillis / weaponSpeedComponent.millis < 2.0) {
                "Is eligible for 2 attacks in the same tick, which is weird..."
            }
            if (reloadDowntimeComponent.currentDowntimeMillis >= weaponSpeedComponent.millis) {
                world.modifyEntity(it) {
                    addComponentOrThrow(com.xenotactic.gamelogic.components.ReadyToAttackComponent)
                    removeComponent<com.xenotactic.gamelogic.components.ReloadDowntimeComponent>()
                }
            }
        }
    }
}