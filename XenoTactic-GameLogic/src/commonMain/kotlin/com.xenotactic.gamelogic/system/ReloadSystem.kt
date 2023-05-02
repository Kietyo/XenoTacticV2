package com.xenotactic.gamelogic.system

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.utils.GameMapApi
import com.xenotactic.gamelogic.components.BaseWeaponSpeedComponent
import com.xenotactic.gamelogic.components.ReloadDowntimeComponent
import com.xenotactic.gamelogic.utils.Engine
import kotlin.time.Duration

class ReloadSystem(
    val engine: Engine
) : System() {
    private val world: World = engine.gameWorld.world
    private val gameMapApi = engine.injections.getSingleton<GameMapApi>()
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            BaseWeaponSpeedComponent::class,
            ReloadDowntimeComponent::class,
        ),
        noneOfComponents = setOf(com.xenotactic.gamelogic.components.ReadyToAttackComponent::class)
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val weaponSpeedMillis = gameMapApi.calculateWeaponSpeedMillis(it)
            val reloadDowntimeComponent = world[it, ReloadDowntimeComponent::class]
            reloadDowntimeComponent.currentDowntimeMillis += deltaTime.inWholeMilliseconds
            require(reloadDowntimeComponent.currentDowntimeMillis / weaponSpeedMillis < 2.0) {
                "Is eligible for 2 attacks in the same tick, which is weird..."
            }
            if (reloadDowntimeComponent.currentDowntimeMillis >= weaponSpeedMillis) {
                world.modifyEntity(it) {
                    addComponentOrThrow(com.xenotactic.gamelogic.components.ReadyToAttackComponent)
                    removeComponent<ReloadDowntimeComponent>()
                }
            }
        }
    }
}