package com.xenotactic.korge.event_listeners

import com.soywiz.klogger.Logger
import com.xenotactic.gamelogic.components.DamageUpgradeComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.engine.EventListener
import com.xenotactic.korge.events.UpgradeTowerDamageEvent
import com.xenotactic.korge.events.UpgradedTowerDamageEvent
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
            handleUpgradeTowerDamageEvent()
        }
    }

    private fun handleUpgradeTowerDamageEvent() {
        gameWorld.selectionFamily.getSequence().forEach {
            if (!gameWorld.isTowerEntity(it)) return@forEach
            val damageUpgradeComponent = world[it, DamageUpgradeComponent::class]
            world.modifyEntity(it) {
                val newDamageUpgrade = damageUpgradeComponent.numUpgrades + 1
                addOrReplaceComponent(DamageUpgradeComponent(newDamageUpgrade))
                val newDamage = gameMapApi.calculateTowerDamage(it)
                engine.eventBus.send(UpgradedTowerDamageEvent(
                    it, newDamage, newDamageUpgrade
                ))
            }
        }
    }
}