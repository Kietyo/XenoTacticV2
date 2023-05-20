package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.korge.components.MutableShowRangeTimeComponent
import kotlin.time.Duration

class UIMutableRangeTimeUpdateSystem(val world: World) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            MutableShowRangeTimeComponent::class
        )
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val comp = world[it, MutableShowRangeTimeComponent::class]
            comp.showTimeRemainingMillis -= deltaTime.inWholeMilliseconds

            if (comp.showTimeRemainingMillis <= 0) {
                world.modifyEntity(it) {
                    removeComponent<MutableShowRangeTimeComponent>()
                }
            }
        }
    }
}