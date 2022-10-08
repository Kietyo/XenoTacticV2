package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.korge.components.*
import kotlin.time.Duration

class ReloadSystem(
    val world: World
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            ReloadTimeComponent::class,
            ReloadDowntimeComponent::class,
        ),
        noneOfComponents = setOf(ReadyToAttackComponent::class)
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val reloadTimeComponent = world[it, ReloadTimeComponent::class]
            val reloadDowntimeComponent = world[it, ReloadDowntimeComponent::class]
            reloadDowntimeComponent.currentDowntimeMillis += deltaTime.inWholeMilliseconds
            require(reloadDowntimeComponent.currentDowntimeMillis / reloadTimeComponent.reloadTimeMillis < 2.0) {
                "Is eligible for 2 attacks in the same tick, which is weird..."
            }
            if (reloadDowntimeComponent.currentDowntimeMillis >= reloadTimeComponent.reloadTimeMillis) {
                world.modifyEntity(it) {
                    addComponentOrThrow(ReadyToAttackComponent)
                    removeComponent<ReloadDowntimeComponent>()
                }
            }
        }
    }
}