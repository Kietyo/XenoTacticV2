package com.xenotactic.korge.ui

import com.soywiz.korev.Key
import com.soywiz.korge.annotations.KorgeExperimental
import com.soywiz.korge.component.onAttachDetach
import com.soywiz.korge.input.keys
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
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
import com.xenotactic.korge.state.*

enum class ViewType {
    NONE,
    SINGLE_TOWER_SELECTION,
    MULTI_TOWER_SELECTION
}

@OptIn(KorgeExperimental::class)
class UIGuiContainer(
    val stage: SContainer,
    val engine: Engine,
    val world: World,
    val gameWorld: GameWorld,
    val gameMapApi: GameMapApi
) {
    val eventBus = engine.eventBus
    private val editorState = engine.stateInjections.getSingleton<EditorState>()
    private val gameplayState = engine.stateInjections.getSingleton<GameplayState>()
    private val deadUIZonesState = engine.stateInjections.getSingleton<DeadUIZonesState>()
    private val mutableResourcesState = engine.stateInjections.getSingleton<MutableResourcesState>()

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

        val bottomRightGridWidth = 230.0
        val bottomRightGridHeight = bottomRightGridWidth / 2
        val bottomRightGridHorizontalPadding = 5.0

        val bottomRightGrid = UIFixedGrid(
            4, 2, bottomRightGridWidth, bottomRightGridHeight, bottomRightGridHorizontalPadding, 5.0,
            backgroundColor = MaterialColors.TEAL_600
        ).addTo(stage) {
            name = "Bottom right grid"
            alignBottomToBottomOfWindow()
            alignRightToRightOfWindow()
        }

        val topRightResources = stage.container {
            val goldSection = container {
                val i = image(GlobalResources.GOLD_ICON) {
                    smoothing = false
                }
                val t = text(
                    mutableResourcesState.currentGold.toString(), font = GlobalResources.FONT_ATKINSON_BOLD,
                    textSize = 40.0
                ) {
                    scaleWhileMaintainingAspect(ScalingOption.ByHeight(i.scaledHeight))
                    alignLeftToRightOf(i, padding = 5.0)
                    centerYOn(i)
                }
            }

            val supplySection = container {
                val i = image(GlobalResources.SUPPLY_ICON) {
                    smoothing = false
                }
                val t = text(
                    "${mutableResourcesState.currentSupply}/${mutableResourcesState.initialMaxSupply}",
                    font = GlobalResources.FONT_ATKINSON_BOLD,
                    textSize = 40.0
                ) {
                    scaleWhileMaintainingAspect(ScalingOption.ByHeight(i.scaledHeight))
                    alignLeftToRightOf(i, padding = 5.0)
                    centerYOn(i)
                }

                alignLeftToRightOf(goldSection, padding = 40.0)
            }


            scaleWhileMaintainingAspect(ScalingOption.ByHeight(25.0))

            y += 5.0
            alignRightToRightOfWindow(padding = 10.0)
        }


        val multiTowerSelectGridWidth = 400.0
        val multiTowerSelectGridHeight = multiTowerSelectGridWidth / 3

        val multiTowerSelectGrid = UIFixedGrid(
            10, 3, multiTowerSelectGridWidth, multiTowerSelectGridHeight,
            3.0, 3.0
        )

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

        val tooltips = mutableListOf<View>()

        fun clearTooltips() {
            tooltips.forEach { it.removeFromParent() }
            tooltips.clear()
        }

        val addTowerTooltip = UITooltipDescription(gameplayState.basicTowerCost)

        val addTowerView = UITextRect(
            "Add\nTower",
            50.0, 50.0, 5.0, GlobalResources.FONT_ATKINSON_BOLD
        ).apply {
            onClick {
                editorState.toggle(MapEntityType.TOWER)
            }
            onOver {
                val tooltip = addTowerTooltip.addTo(this@UIGuiContainer.stage) {
                    scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(150.0, 150.0))
                    alignBottomToTopOf(this@apply, padding = 5.0)
                    centerXOn(this@apply)
                }
                tooltips.add(tooltip)
            }
            onOut {
                clearTooltips()
            }
        }

        val incomeUpgradeView = UITextRect(
            "Income\nUpgrade",
            50.0, 50.0, 5.0, GlobalResources.FONT_ATKINSON_BOLD
        ).apply {
        }

        val towerDamageUpgradeView = Container().apply {
            var numUpgrades = 1
            val img = image(GlobalResources.DAMAGE_ICON) {
                smoothing = false
                scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(50.0, 50.0))
            }
            val t = UITextWithShadow("+1").addTo(this) {
                scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(40.0, 40.0))
                centerOn(img)
            }

            fun setNumUpgrades(newNumUpgrades: Int) {
                numUpgrades = newNumUpgrades
                t.text = "+$newNumUpgrades"
                t.centerOn(img)
            }

            keys {
                justDown(Key.LEFT_SHIFT) {
//                    println("Down shift just down")
                    setNumUpgrades(5)
                }
                up(Key.LEFT_SHIFT) {
//                    println("up shift")
                    setNumUpgrades(1)
                }
            }

            onAttachDetach(onDetach = {
                setNumUpgrades(1)
//                println("on detach")
            })

            onClick {
                eventBus.send(UpgradeTowerDamageEvent(numUpgrades))
            }
        }

        val towerSpeedUpgradeView = Container().apply {
            var numUpgrades = 1
            val img = image(GlobalResources.COOLDOWN_ICON) {
                smoothing = false
                scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(50.0, 50.0))
            }
            val t = UITextWithShadow("+1").addTo(this) {
                scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(40.0, 40.0))
                centerOn(img)
            }

            fun setNumUpgrades(newNumUpgrades: Int) {
                numUpgrades = newNumUpgrades
                t.text = "+$newNumUpgrades"
                t.centerOn(img)
            }

            keys {
                justDown(Key.LEFT_SHIFT) {
//                    println("Down shift just down")
                    setNumUpgrades(5)
                }
                up(Key.LEFT_SHIFT) {
//                    println("up shift")
                    setNumUpgrades(1)
                }
            }

            onAttachDetach(onDetach = {
                setNumUpgrades(1)
//                println("on detach")
            })

            onClick {
                eventBus.send(UpgradeTowerSpeedEvent(numUpgrades))
            }
        }

        val holdShiftText = Text(
            "HOLD SHIFT (+5)",
            font = GlobalResources.FONT_ATKINSON_BOLD,
            textSize = 12.0
        )

        fun resetInitial() {
            bottomRightGrid.setEntry(0, 0, globalDamageUpgradeView)
            bottomRightGrid.setEntry(1, 0, globalRangeUpgradeView)
            bottomRightGrid.setEntry(2, 0, incomeUpgradeView)

            bottomRightGrid.setEntry(0, 1, addTowerView)
        }

        deadUIZonesState.zones.add(bottomRightGrid)

        var currentTowerId: EntityId? = null

        var currentViewType = ViewType.NONE

        fun resetView() {
            when (currentViewType) {
                ViewType.NONE -> Unit
                ViewType.SINGLE_TOWER_SELECTION -> {
                    currentTowerId = null
                    middleSelectionContainer.removeChildren()
                    holdShiftText.removeFromParent()
                    bottomRightGrid.clearEntry(0, 1)
                    bottomRightGrid.clearEntry(1, 1)
                }

                ViewType.MULTI_TOWER_SELECTION -> {
                    middleSelectionContainer.removeChildren()
                }
            }
            resetInitial()
        }

        resetInitial()

        eventBus.register<EntitySelectionChangedEvent> {
            if (gameWorld.selectionFamily.size == 1 && gameWorld.isTowerEntity(gameWorld.selectionFamily.first())) {
                resetView()
                currentViewType = ViewType.SINGLE_TOWER_SELECTION
                val towerId = gameWorld.selectionFamily.first()
                currentTowerId = towerId
                println("Selected one tower entity!")
                middleSelectionContainer.apply {
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

                holdShiftText.addTo(stage) {
                    alignLeftToLeftOf(bottomRightGrid, padding = bottomRightGridHorizontalPadding / 2.0)
                    alignBottomToTopOf(bottomRightGrid)
                }
            } else if (gameWorld.selectionFamily.size > 1) {
                resetView()
                currentViewType = ViewType.MULTI_TOWER_SELECTION
                middleSelectionContainer.apply {
                    multiTowerSelectGrid.addTo(this)
                    centerXOnStage()
                    alignBottomToBottomOfWindow()
                }
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