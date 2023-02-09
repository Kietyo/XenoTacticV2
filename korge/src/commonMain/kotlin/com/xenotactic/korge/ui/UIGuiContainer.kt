package com.xenotactic.korge.ui

import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korim.color.MaterialColors
import com.xenotactic.ecs.EntityId
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.DamageUpgradeComponent
import com.xenotactic.gamelogic.components.RangeComponent
import com.xenotactic.gamelogic.components.SpeedUpgradeComponent
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.GlobalResources
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.event_listeners.RemoveUIEntitiesEvent
import com.xenotactic.korge.events.EntitySelectionChangedEvent
import com.xenotactic.korge.events.RemovedTowerEntityEvent
import com.xenotactic.korge.events.UpgradeTowerDamageEvent
import com.xenotactic.korge.events.UpgradeTowerSpeedEvent
import com.xenotactic.korge.korge_utils.alignBottomToBottomOfWindow
import com.xenotactic.korge.korge_utils.alignRightToRightOfWindow
import com.xenotactic.korge.korge_utils.distributeVertically
import com.xenotactic.korge.korge_utils.isEmpty
import com.xenotactic.korge.models.GameWorld
import com.xenotactic.korge.state.DeadUIZonesState
import com.xenotactic.korge.state.EditorState
import com.xenotactic.korge.state.GameMapApi
import com.xenotactic.korge.state.GameplayState

class UIGuiContainer(
    val stage: SContainer,
    val engine: Engine,
    val world: World,
    val gameWorld: GameWorld,
    val gameMapApi: GameMapApi
) {
    val eventBus = engine.eventBus
    val editorState = engine.stateInjections.getSingleton<EditorState>()
    val gameplayState = engine.stateInjections.getSingleton<GameplayState>()
    private val deadUIZonesState = engine.stateInjections.getSingleton<DeadUIZonesState>()

    val middleSelectionContainer = stage.container { }

    init {
        val buttonsPanel = stage.container {
            val spawnCreepButton = uiButton("Spawn creep") {
                onClick {
                    println("Spawn creep button clicked!")
                    gameMapApi.spawnCreep()

                }
            }

            val addTowerButton = uiButton("Add tower") {
                onClick {
                    println("Add tower button clicked!")
                    editorState.toggle(MapEntityType.TOWER)
                }
            }

            val printWorldButton = uiButton("Print world") {
                onClick {
                    println("print world button clicked!")
                    println(world)
                }
            }

            val deleteEntitiesButton = uiButton("Delete Entities") {
                disable()
                onClick {
                    println("delete entities button clicked!")
                    eventBus.send(RemoveUIEntitiesEvent(gameWorld.selectionFamily.getSequence().toSet()))
                    disable()
                }
                eventBus.register<EntitySelectionChangedEvent> {
                    if (gameWorld.selectionFamily.getSequence().isEmpty()) {
                        disable()
                    } else {
                        enable()
                    }
                }
            }

            distributeVertically(listOf(spawnCreepButton, addTowerButton, printWorldButton, deleteEntitiesButton))
            alignBottomToBottomOfWindow()
        }

        val gridWidth = 230.0
        val gridHeight = gridWidth / 2

        val bottomRightGrid = UIFixedGrid(
            4, 2, gridWidth, gridHeight, 5.0, 5.0,
            backgroundColor = MaterialColors.TEAL_600
        ).addTo(stage) {
            name = "Bottom right grid"
            alignBottomToBottomOfWindow()
            alignRightToRightOfWindow()
        }

        val globalDamageUpgradeView = UITextRect(
            "Global\nDamage\nUpgrade",
            50.0, 50.0, 5.0, GlobalResources.FONT_ATKINSON_BOLD
        ).apply {
            onCollision { }
        }

        val globalRangeUpgradeView = UITextRect(
            "Global\nRange\nUpgrade",
            50.0, 50.0, 5.0, GlobalResources.FONT_ATKINSON_BOLD
        ).apply {
        }

        val incomeUpgradeView = UITextRect(
            "Income\nUpgrade",
            50.0, 50.0, 5.0, GlobalResources.FONT_ATKINSON_BOLD
        ).apply {
        }

        val towerDamageUpgradeView = UITextRect(
            "Tower\nDamage\nUpgrade",
            50.0, 50.0, 5.0, GlobalResources.FONT_ATKINSON_BOLD
        ).apply {
            onClick {
                eventBus.send(UpgradeTowerDamageEvent)
            }
        }

        val towerSpeedUpgradeView = UITextRect(
            "Tower\nSpeed\nUpgrade",
            50.0, 50.0, 5.0, GlobalResources.FONT_ATKINSON_BOLD
        ).apply {
            onClick {
                eventBus.send(UpgradeTowerSpeedEvent)
            }
        }

        bottomRightGrid.setEntry(0, 0, globalDamageUpgradeView)
        bottomRightGrid.setEntry(1, 0, globalRangeUpgradeView)
        bottomRightGrid.setEntry(2, 0, incomeUpgradeView)

        deadUIZonesState.zones.add(bottomRightGrid)

        var currentTowerId: EntityId? = null

        fun resetView() {
            currentTowerId = null
            middleSelectionContainer.removeChildren()
            bottomRightGrid.clearEntry(0, 1)
            bottomRightGrid.clearEntry(1, 1)
        }

        eventBus.register<EntitySelectionChangedEvent> {
            if (gameWorld.selectionFamily.size == 1 && gameWorld.isTowerEntity(gameWorld.selectionFamily.first())) {
                val towerId = gameWorld.selectionFamily.first()
                currentTowerId = towerId
                println("Selected one tower entity!")
                middleSelectionContainer.apply {
                    removeChildren()
                    val towerDamage = gameMapApi.calculateTowerDamage(towerId)
                    val weaponSpeedMillis = gameMapApi.calculateWeaponSpeedMillis(towerId)
                    val attacksPerSecond = gameMapApi.calculateTowerAttacksPerSecond(towerId, weaponSpeedMillis)
                    val rangeComponent = world[towerId, RangeComponent::class]
                    val damageUpgradeComponent = world[towerId, DamageUpgradeComponent::class]
                    val speedUpgradeComponent = world[towerId, SpeedUpgradeComponent::class]
                    UITowerDetails(
                        towerDamage,
                        weaponSpeedMillis,
                        attacksPerSecond,
                        rangeComponent.range.value,
                        damageUpgradeComponent.numUpgrades,
                        speedUpgradeComponent.numUpgrades,
                        gameplayState.maxSpeedUpgrades,
                        engine
                    ).addTo(this) {
                        scaleWhileMaintainingAspect(ScalingOption.ByHeight(100.0))
                    }
                    centerXOnStage()
                    alignBottomToBottomOfWindow()
                }

                bottomRightGrid.setEntry(0, 1, towerDamageUpgradeView)
                bottomRightGrid.setEntry(1, 1, towerSpeedUpgradeView)
            } else if (gameWorld.selectionFamily.isEmpty) {
                resetView()
            } else {
                resetView()
            }
        }

        eventBus.register<RemovedTowerEntityEvent> {
            if (currentTowerId == it.entityId) {
                resetView()
            }
        }
    }
}