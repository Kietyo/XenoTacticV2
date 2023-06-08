package com.xenotactic.korge.ui

import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.components.DamageUpgradeComponent
import com.xenotactic.gamelogic.components.RangeComponent
import com.xenotactic.gamelogic.components.SpeedUpgradeComponent
import com.xenotactic.gamelogic.events.RemovedTowerEntityEvent
import com.xenotactic.gamelogic.model.GameWorld
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.model.TowerType
import com.xenotactic.gamelogic.state.GameplayState
import com.xenotactic.gamelogic.state.MutableCurrentlySelectedTowerState
import com.xenotactic.gamelogic.state.MutableGoldState
import com.xenotactic.gamelogic.utils.*
import com.xenotactic.korge.components.MutableShowRangeTimeComponent
import com.xenotactic.korge.events.EntitySelectionChangedEvent
import com.xenotactic.korge.events.UpgradeTowerDamageEvent
import com.xenotactic.korge.events.UpgradeTowerSpeedEvent
import com.xenotactic.korge.listeners_event.RemoveUIEntitiesEvent
import com.xenotactic.korge.state.DeadUIZonesState
import com.xenotactic.korge.state.EditorState
import com.xenotactic.korge.utils.*
import korlibs.image.color.MaterialColors
import korlibs.korge.component.onAttachDetach
import korlibs.korge.input.onClick
import korlibs.korge.input.onOut
import korlibs.korge.input.onOver
import korlibs.korge.ui.uiButton
import korlibs.korge.view.*
import korlibs.korge.view.align.*

enum class ViewType {
    NONE,
    SINGLE_TOWER_SELECTION,
    SINGLE_TOWER_SELECTION_UPGRADE,
    MULTI_TOWER_SELECTION,
    SINGLE_DEPOT_SELECTION
}

class UIGuiContainer(
    val hstage: SContainer,
    val engine: Engine,
    val gameMapApi: GameMapApi
) {
    val gameWorld: GameWorld = engine.gameWorld
    val world = gameWorld.world
    val eventBus = engine.eventBus
    private val editorState = engine.stateInjections.getSingleton<EditorState>()
    private val gameplayState = engine.stateInjections.getSingleton<GameplayState>()
    private val deadUIZonesState = engine.stateInjections.getSingleton<DeadUIZonesState>()
    private val mutableGoldState = engine.stateInjections.getSingleton<MutableGoldState>()
    private val gameSimulator = engine.injections.getSingletonOrNull<GameSimulator>()

    val stage = hstage.container { }
    private val middleSelectionContainer = stage.container { }
    val settingsWindows = stage.container { }

    val bottomRightGridWidth = 400.0
    val tooltipSize = bottomRightGridWidth / 1.5

    val gridSquareLength = 50.0

    var currentViewType = ViewType.NONE
    val mutableCurrentlySelectedTowerState = MutableCurrentlySelectedTowerState(null)
    val bottomRightGrid: UIFixedGrid
    val holdShiftText: Text

    val globalDamageUpgradeView = UITextRect(
        "Global\nDamage\nUpgrade",
        gridSquareLength, gridSquareLength, 5.0, GlobalResources.FONT_ATKINSON_BOLD
    )

    val globalRangeUpgradeView = UITextRect(
        "Global\nRange\nUpgrade",
        gridSquareLength, gridSquareLength, 5.0, GlobalResources.FONT_ATKINSON_BOLD
    )

    val incomeUpgradeView = UITextRect(
        "Income\nUpgrade",
        gridSquareLength, gridSquareLength, 5.0, GlobalResources.FONT_ATKINSON_BOLD
    )

    val highDamageTowerSprite = createUIEntityContainerForTower(
        gridSquareLength.toWorldUnit(),
        gridSquareLength.toWorldUnit(),
        TowerType.HIGH_DAMAGE
    )

    val addTowerView = UITextRect(
        "Add\nTower",
        gridSquareLength, gridSquareLength, 5.0, GlobalResources.FONT_ATKINSON_BOLD
    ).apply {
        onClick {
            editorState.toggle(MapEntityType.TOWER)
        }
        val tooltip = UITooltipDescription(gameplayState.basicTowerCost)
        onOver {
            tooltip.addTo(this@UIGuiContainer.stage) {
                scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(tooltipSize, tooltipSize))
                alignBottomToTopOf(this@apply, padding = 5.0)
                centerXOn(this@apply)
            }
        }
        onOut {
            tooltip.removeFromParent()
        }
    }

    val addSupplyDepotView = UITextRect(
        "Add\nSupply\nDepot",
        gridSquareLength, gridSquareLength, 5.0, GlobalResources.FONT_ATKINSON_BOLD
    ).apply {
        onClick {
            editorState.toggle(MapEntityType.SUPPLY_DEPOT)
        }
        val tooltip = UITooltipDescription(
            goldCost = gameplayState.supplyDepotCost,
            supplyCost = 0,
            titleText = "SUPPLY DEPOT",
            descriptionText = "Adds ${gameplayState.supplyPerDepot} supply.")
        onOver {
            tooltip.addTo(this@UIGuiContainer.stage) {
                scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(tooltipSize, tooltipSize))
                alignBottomToTopOf(this@apply, padding = 5.0)
                centerXOn(this@apply)
            }
        }
        onOut {
            tooltip.removeFromParent()
        }
    }

    init {
        val topLeftPanel = stage.container {
            uiButton(label = "Settings") {
                onClick {

                }
            }
            scale = 1.5.toScale()
        }

        val uiDebugInfo = engine.injections.getSingleton<UIDebugInfo>()
        uiDebugInfo.addTo(stage) {
            alignTopToBottomOf(topLeftPanel)
        }

        val buttonsPanel = stage.container {
            val spawnCreepButton = uiButton("Spawn creep") {
                onClick {
                    println("Spawn creep button clicked!")
                    gameMapApi.spawnCreep()

                }
            }

            val printWorldButton = uiButton("Print world") {
                onClick {
                    println("print world button clicked!")
                    println(world)
                }
            }

            val printEventLog = uiButton("Print event log") {
                onClick {
                    gameSimulator?.printEventLog()
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

            distributeVertically(
                listOf(
                    spawnCreepButton,
                    printWorldButton,
                    printEventLog,
                    deleteEntitiesButton
                )
            )
            scale = 2.0.toScale()
        }
        buttonsPanel.alignBottomToBottomOfWindow()

        val bottomRightGridHeight = bottomRightGridWidth / 2
        val bottomRightGridHorizontalPadding = 5.0

        bottomRightGrid = UIFixedGrid(
            4, 2, bottomRightGridWidth, bottomRightGridHeight, bottomRightGridHorizontalPadding, 5.0,
            backgroundColor = MaterialColors.TEAL_600
        ).addTo(stage) {
            name = "Bottom right grid"
            alignBottomToBottomOfWindow()
            alignRightToRightOfWindow()
        }

        val topRightResources = UITopRightResourcesGui(engine).addTo(stage) {
            scaleWhileMaintainingAspect(ScalingOption.ByHeight(25.0))
            y += 5f
            alignRightToRightOfWindow(padding = 10f)
        }

        val multiTowerSelectGridWidth = 400.0
        val multiTowerSelectGridHeight = multiTowerSelectGridWidth / 3

        val multiTowerSelectGrid = UIFixedGrid(
            10, 3, multiTowerSelectGridWidth, multiTowerSelectGridHeight,
            3.0, 3.0
        )

        val sellEntitiesView = UITextRect(
            "Sell\nEntities",
            gridSquareLength, gridSquareLength, 5.0, GlobalResources.FONT_ATKINSON_BOLD
        ).apply {
            onClick {
                eventBus.send(RemoveUIEntitiesEvent(gameWorld.selectionFamily.getSequence().toSet()))
            }
            val tooltip = UITooltipDescription(
                null, null, "SELL",
                "Sell tower(s), refunds 100% of the\nbasic tower cost, but not upgrades."
            )
            onOver {
                tooltip.addTo(this@UIGuiContainer.stage) {
                    alignBottomToTopOf(this@apply, padding = 5.0)
                    alignRightToRightOf(this@apply)
                }
            }
            onAttachDetach {
                tooltip.removeFromParent()
            }
            onOut {
                tooltip.removeFromParent()
            }
        }

        engine.stateInjections.setSingletonOrThrow(mutableCurrentlySelectedTowerState)

        val showRangeView = UITextRect(
            "Show\nRange",
            gridSquareLength, gridSquareLength, 5.0, GlobalResources.FONT_ATKINSON_BOLD
        ).apply {
            onClick {
                mutableCurrentlySelectedTowerState.currentTowerId?.also {
                    world.modifyEntity(it) {
                        val defaultShowTimeMillis = 4000L
                        val comp = getComponentOrAdd {
                            MutableShowRangeTimeComponent(defaultShowTimeMillis)
                        }
                        comp.showTimeRemainingMillis = defaultShowTimeMillis
                    }
                }
            }
            val tooltip = UITooltipDescription(
                null, null, "SHOW RANGE",
                "Show the range of the selected tower(s)."
            )
            onOver {
                tooltip.addTo(this@UIGuiContainer.stage) {
                    alignBottomToTopOf(this@apply, padding = 5.0)
                    alignRightToRightOf(this@apply)
                }
            }
            onAttachDetach {
                tooltip.removeFromParent()
            }
            onOut {
                tooltip.removeFromParent()
            }
        }

        val tooltipUpgradeDamage = UITooltipDescription(
            gameplayState.initialDamageUpgradeCost,
            null,
            "Upgrade Damage",
            "Upgrade tower damage."
        )
        val tooltipUpgradeSpeed = UITooltipDescription(
            gameplayState.initialSpeedUpgradeCost,
            null,
            "Upgrade Speed",
            "Upgrade tower speed."
        )

        val towerUpgradeView = Container().apply {
            image(GlobalResources.UPGRADE_TOWER_ICON) {
                width = gridSquareLength.toFloat()
                height = gridSquareLength.toFloat()
            }
            onClick {
                setUpgradeSingleTowerView()
            }
            val tooltip = UITooltipDescription(
                null,
                null,
                "Upgrade Tower",
                "Choose from a selection\nof tower upgrades."
            )
            onOver {
                tooltip.addTo(this@UIGuiContainer.stage) {
                    scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(tooltipSize, tooltipSize))
                    alignBottomToTopOf(this@apply, padding = 5.0)
                    centerXOn(this@apply)
                }
            }
            onOut {
                tooltip.removeFromParent()
            }
            onAttachDetach {
                tooltip.removeFromParent()
            }
        }

        val towerDamageUpgradeView = UITowerUpgradeIcon(
            engine,
            tooltipUpgradeDamage,
            tooltipSize,
            this@UIGuiContainer,
            GlobalResources.DAMAGE_ICON,
            gameplayState.initialDamageUpgradeCost,
            {
                world[it, DamageUpgradeComponent::class].numUpgrades
            },
            { eventBus.send(UpgradeTowerDamageEvent(it)) }
        )

        val towerSpeedUpgradeView = UITowerUpgradeIcon(
            engine,
            tooltipUpgradeSpeed,
            tooltipSize,
            this@UIGuiContainer,
            GlobalResources.COOLDOWN_ICON,
            gameplayState.initialSpeedUpgradeCost,
            { world[it, SpeedUpgradeComponent::class].numUpgrades },
            { eventBus.send(UpgradeTowerSpeedEvent(it)) }
        )



        val textHeightSize = bottomRightGridHeight / 7
        holdShiftText = Text(
            "HOLD SHIFT (+5)",
            font = GlobalResources.FONT_ATKINSON_BOLD,
            textSize = 12f
        ).apply {
            scaleWhileMaintainingAspect(ScalingOption.ByHeight(textHeightSize))
        }

        deadUIZonesState.zones.add(bottomRightGrid)

        resetInitial()

        gameWorld.world.componentService.addComponentListener(
            object : ComponentListener<DamageUpgradeComponent> {
                override fun onAddOrReplace(
                    entityId: EntityId,
                    old: DamageUpgradeComponent?,
                    new: DamageUpgradeComponent) {
                    if (entityId == mutableCurrentlySelectedTowerState.currentTowerId) {
                        towerDamageUpgradeView.setNumTowerUpgradesText(towerDamageUpgradeView.numUpgrades)
                    }
                }
            })
        gameWorld.world.componentService.addComponentListener(
            object : ComponentListener<SpeedUpgradeComponent> {
                override fun onAddOrReplace(
                    entityId: EntityId,
                    old: SpeedUpgradeComponent?,
                    new: SpeedUpgradeComponent) {
                    if (entityId == mutableCurrentlySelectedTowerState.currentTowerId) {
                        towerSpeedUpgradeView.setNumTowerUpgradesText(towerSpeedUpgradeView.numUpgrades)
                    }
                }
            })

        eventBus.register<EntitySelectionChangedEvent> {
            cleanupView()

            if (gameWorld.selectionFamily.size == 1 && gameWorld.isTowerEntity(gameWorld.selectionFamily.first())) {
                currentViewType = ViewType.SINGLE_TOWER_SELECTION
                val towerId = gameWorld.selectionFamily.first()
                mutableCurrentlySelectedTowerState.currentTowerId = towerId
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
                        scaleWhileMaintainingAspect(ScalingOption.ByHeight(bottomRightGridHeight))
                    }
                    centerXOnStage()
                    alignBottomToBottomOfWindow()

                    tooltipUpgradeDamage.updateMoneyCost(
                        gameplayState.initialDamageUpgradeCost + damageUpgradeComponent.numUpgrades
                    )
                    tooltipUpgradeSpeed.updateMoneyCost(
                        gameplayState.initialSpeedUpgradeCost + speedUpgradeComponent.numUpgrades
                    )
                }

                bottomRightGrid.setEntry(3, 0, showRangeView)
                bottomRightGrid.setEntry(0, 1, towerDamageUpgradeView)
                bottomRightGrid.setEntry(1, 1, towerSpeedUpgradeView)
                bottomRightGrid.setEntry(2, 1, towerUpgradeView)
                bottomRightGrid.setEntry(3, 1, sellEntitiesView)

                holdShiftText.addTo(stage) {
                    alignLeftToLeftOf(bottomRightGrid, padding = bottomRightGridHorizontalPadding / 2.0)
                    alignBottomToTopOf(bottomRightGrid)
                }
            } else if (gameWorld.selectionFamily.size == 1 && gameWorld.isSupplyDepotEntity(gameWorld.selectionFamily.first())) {
                currentViewType = ViewType.SINGLE_DEPOT_SELECTION
                val towerId = gameWorld.selectionFamily.first()
                mutableCurrentlySelectedTowerState.currentTowerId = towerId
                bottomRightGrid.setEntry(3, 1, sellEntitiesView)

                holdShiftText.addTo(stage) {
                    alignLeftToLeftOf(bottomRightGrid, padding = bottomRightGridHorizontalPadding / 2.0)
                    alignBottomToTopOf(bottomRightGrid)
                }
            } else if (gameWorld.selectionFamily.size > 1) {
                currentViewType = ViewType.MULTI_TOWER_SELECTION
                middleSelectionContainer.apply {
                    multiTowerSelectGrid.addTo(this)
                    centerXOnStage()
                    alignBottomToBottomOfWindow()
                }
            }
        }

        eventBus.register<RemovedTowerEntityEvent> {
            if (mutableCurrentlySelectedTowerState.currentTowerId == it.entityId) {
                cleanupView()
            }
        }
    }

    fun resetInitial() {
        holdShiftText.removeFromParent()
        bottomRightGrid.clear()
        bottomRightGrid.setEntry(0, 0, globalDamageUpgradeView)
        bottomRightGrid.setEntry(1, 0, globalRangeUpgradeView)
        bottomRightGrid.setEntry(2, 0, incomeUpgradeView)

        bottomRightGrid.setEntry(0, 1, addTowerView)
        bottomRightGrid.setEntry(1, 1, addSupplyDepotView)
    }

    fun cleanupView() {
        when (currentViewType) {
            ViewType.NONE -> Unit
            ViewType.SINGLE_TOWER_SELECTION -> {
                mutableCurrentlySelectedTowerState.currentTowerId = null
                middleSelectionContainer.removeChildren()
                holdShiftText.removeFromParent()
                bottomRightGrid.clearEntry(0, 1)
                bottomRightGrid.clearEntry(1, 1)
            }

            ViewType.MULTI_TOWER_SELECTION -> {
                middleSelectionContainer.removeChildren()
            }

            ViewType.SINGLE_DEPOT_SELECTION -> {

            }

            ViewType.SINGLE_TOWER_SELECTION_UPGRADE -> Unit
        }
        resetInitial()
    }

    fun setUpgradeSingleTowerView() {
        cleanupView()
        currentViewType = ViewType.SINGLE_TOWER_SELECTION_UPGRADE
        bottomRightGrid.clear()
        bottomRightGrid.setEntry(0,0, highDamageTowerSprite)
    }
}