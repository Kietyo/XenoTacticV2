package com.xenotactic.korge.event_listeners

import com.soywiz.klogger.Logger
import com.xenotactic.gamelogic.components.DamageUpgradeComponent
import com.xenotactic.gamelogic.components.SpeedUpgradeComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.engine.EventListener
import com.xenotactic.korge.events.UpgradeTowerDamageEvent
import com.xenotactic.korge.events.UpgradeTowerSpeedEvent
import com.xenotactic.korge.events.UpgradedTowerDamageEvent
import com.xenotactic.korge.events.UpgradedTowerSpeedEvent
import com.xenotactic.korge.state.GameMapApi

class TowerUpgradeEventListeners(
    val engine: Engine
): EventListener {
    private val gameWorld = engine.gameWorld
    private val world = gameWorld.world
    private val gameMapApi = engine.injections.getSingleton<GameMapApi>()
    val logger = Logger<TowerUpgradeEventListeners>()
    init {
        engine.eventBus.register<UpgradeTowerDamageEvent> {
            handleUpgradeTowerDamageEvent(it)
        }
        engine.eventBus.register<UpgradeTowerSpeedEvent> {
            handleUpgradeTowerSpeedEvent()
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
                engine.eventBus.send(UpgradedTowerDamageEvent(
                    it, newDamage, newDamageUpgrade
                ))
            }
        }
    }

    private fun handleUpgradeTowerSpeedEvent() {
        gameWorld.selectionFamily.getSequence().forEach {
            if (!gameWorld.isTowerEntity(it)) return@forEach
            val speedUpgradeComponent = world[it, SpeedUpgradeComponent::class]
            world.modifyEntity(it) {
                val newSpeedUpgrade = speedUpgradeComponent.numUpgrades + 1
                addOrReplaceComponent(SpeedUpgradeComponent(newSpeedUpgrade))
                val newWeaponSpeedMillis = gameMapApi.calculateWeaponSpeedMillis(it)
                val newAttacksPerSecond = gameMapApi.calculateTowerAttacksPerSecond(it)
                engine.eventBus.send(UpgradedTowerSpeedEvent(
                    it, newWeaponSpeedMillis, newAttacksPerSecond, newSpeedUpgrade
                ))
            }
        }
    }
}