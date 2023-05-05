package com.xenotactic.korge.listeners_event

import korlibs.logger.Logger
import com.xenotactic.gamelogic.components.DamageUpgradeComponent
import com.xenotactic.gamelogic.components.SpeedUpgradeComponent
import com.xenotactic.gamelogic.state.GameplayState
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.EventListener
import com.xenotactic.korge.events.UpgradeTowerDamageEvent
import com.xenotactic.korge.events.UpgradeTowerSpeedEvent
import com.xenotactic.korge.events.UpgradedTowerDamageEvent
import com.xenotactic.korge.events.UpgradedTowerSpeedEvent
import com.xenotactic.gamelogic.utils.GameMapApi

class TowerUpgradeEventListeners(
    val engine: Engine
) : EventListener {
    private val gameWorld = engine.gameWorld
    private val world = gameWorld.world
    private val gameMapApi = engine.injections.getSingleton<GameMapApi>()
    private val gamePlayState = engine.stateInjections.getSingleton<GameplayState>()
    val logger = Logger<TowerUpgradeEventListeners>()

    init {
        engine.eventBus.register<UpgradeTowerDamageEvent> {
            handleUpgradeTowerDamageEvent(it)
        }
        engine.eventBus.register<UpgradeTowerSpeedEvent> {
            handleUpgradeTowerSpeedEvent(it)
        }
    }

    private fun handleUpgradeTowerDamageEvent(event: UpgradeTowerDamageEvent) {
        gameWorld.selectionFamily.getSequence().forEach {
            if (!gameWorld.isTowerEntity(it)) return@forEach
            val damageUpgradeComponent = world[it, DamageUpgradeComponent::class]
            world.modifyEntity(it) {
                val newDamageUpgrade = damageUpgradeComponent.numUpgrades + event.numUpgrades
                addOrReplaceComponent(DamageUpgradeComponent(newDamageUpgrade))
                val newDamage = gameMapApi.calculateTowerDamage(it)
                engine.eventBus.send(
                    UpgradedTowerDamageEvent(
                        it, newDamage, newDamageUpgrade
                    )
                )
            }
        }
    }

    private fun handleUpgradeTowerSpeedEvent(event: UpgradeTowerSpeedEvent) {
        gameWorld.selectionFamily.getSequence().forEach {
            if (!gameWorld.isTowerEntity(it)) return@forEach
            val speedUpgradeComponent = world[it, SpeedUpgradeComponent::class]
            if (speedUpgradeComponent.numUpgrades == gamePlayState.maxSpeedUpgrades) {
                return@forEach
            }
            world.modifyEntity(it) {
                val newSpeedUpgrade =
                    minOf(speedUpgradeComponent.numUpgrades + event.numUpgrades, gamePlayState.maxSpeedUpgrades)
                addOrReplaceComponent(SpeedUpgradeComponent(newSpeedUpgrade))
                val newWeaponSpeedMillis = gameMapApi.calculateWeaponSpeedMillis(it)
                val newAttacksPerSecond = gameMapApi.calculateTowerAttacksPerSecond(it)
                engine.eventBus.send(
                    UpgradedTowerSpeedEvent(
                        it, newWeaponSpeedMillis, newAttacksPerSecond, newSpeedUpgrade
                    )
                )
            }
        }
    }
}