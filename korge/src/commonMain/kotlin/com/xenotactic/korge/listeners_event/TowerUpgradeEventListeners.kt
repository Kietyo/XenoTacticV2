package com.xenotactic.korge.listeners_event

import com.xenotactic.gamelogic.components.DamageUpgradeComponent
import com.xenotactic.gamelogic.components.SpeedUpgradeComponent
import com.xenotactic.gamelogic.state.GameplayState
import com.xenotactic.gamelogic.state.MutableGoldState
import com.xenotactic.gamelogic.utils.*
import com.xenotactic.korge.events.UpgradeTowerDamageEvent
import com.xenotactic.korge.events.UpgradeTowerSpeedEvent
import com.xenotactic.korge.events.UpgradedTowerDamageEvent
import com.xenotactic.korge.events.UpgradedTowerSpeedEvent
import korlibs.logger.Logger

class TowerUpgradeEventListeners(
    val engine: Engine
) : EventListener {
    private val gameWorld = engine.gameWorld
    private val world = gameWorld.world
    private val gameMapApi = engine.injections.getSingleton<GameMapApi>()
    private val gamePlayState = engine.stateInjections.getSingleton<GameplayState>()
    private val mutableGoldState = engine.stateInjections.getSingleton<MutableGoldState>()
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
            val costOfUpgrades = calculateCostOfUpgrades(
                damageUpgradeComponent.numUpgrades, gamePlayState.initialDamageUpgradeCost, event.numUpgrades
            )
            if (costOfUpgrades > mutableGoldState.currentGold) {
                return@forEach
            }
            world.modifyEntity(it) {
                val newDamageUpgrade = damageUpgradeComponent.numUpgrades + event.numUpgrades
                mutableGoldState.currentGold -= costOfUpgrades
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
            if (speedUpgradeComponent.numUpgrades + event.numUpgrades > gamePlayState.maxSpeedUpgrades) {
                return@forEach
            }
            val upgradeDecision = calculateUpgradeDecision(
                mutableGoldState.currentGold,
                speedUpgradeComponent.numUpgrades,
                gamePlayState.maxSpeedUpgrades,
                gamePlayState.initialSpeedUpgradeCost,
                event.numUpgrades
            )
            if (upgradeDecision.maxPossibleUpgradesDelta == event.numUpgrades) {
                world.modifyEntity(it) {
                    val newSpeedUpgrade =
                        speedUpgradeComponent.numUpgrades + upgradeDecision.maxPossibleUpgradesDelta
                    addOrReplaceComponent(SpeedUpgradeComponent(newSpeedUpgrade))
                    mutableGoldState.currentGold -= upgradeDecision.upgradesCost
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


}