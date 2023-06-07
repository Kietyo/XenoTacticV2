package com.xenotactic.korge.ui

import com.xenotactic.gamelogic.events.AddedEntityEvent
import com.xenotactic.gamelogic.events.GoldStateUpdated
import com.xenotactic.gamelogic.events.RemovedSupplyDepotEntityEvent
import com.xenotactic.gamelogic.events.RemovedTowerEntityEvent
import com.xenotactic.gamelogic.state.GameplayState
import com.xenotactic.gamelogic.state.MutableGoldState
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.GlobalResources
import korlibs.korge.view.*
import korlibs.korge.view.align.alignLeftToRightOf
import korlibs.korge.view.align.centerYOn

class UITopRightResourcesGui(val engine: Engine): Container() {
    val gameWorld = engine.gameWorld
    val world = gameWorld.world
    val eventBus = engine.eventBus

    private val gameplayState = engine.stateInjections.getSingleton<GameplayState>()
    private val mutableGoldState = engine.stateInjections.getSingleton<MutableGoldState>()

    init {
        val goldSection = container {
            val i = image(GlobalResources.GOLD_ICON) {
                smoothing = false
            }
            val calculateTextFn = { gold: Int -> gold.toString() }
            val t = text(
                calculateTextFn(mutableGoldState.currentGold), font = GlobalResources.FONT_ATKINSON_BOLD,
                textSize = 40f
            ) {
                scaleWhileMaintainingAspect(ScalingOption.ByHeight(i.scaledHeight.toDouble()))
                alignLeftToRightOf(i, padding = 5.0)
                centerYOn(i)
            }

            eventBus.register<GoldStateUpdated> {
                t.text = calculateTextFn(it.current)
            }
        }

        val supplySection = container {
            val i = image(GlobalResources.SUPPLY_ICON) {
                smoothing = false
            }
            val calculateTextFn = { "${gameWorld.currentSupplyUsage}/${gameWorld.calculateMaxSupply(
                gameplayState.initialSupply, gameplayState.supplyPerDepot, gameplayState.maxSupply
            )}" }
            val t = text(
                calculateTextFn(),
                font = GlobalResources.FONT_ATKINSON_BOLD,
                textSize = 40f
            ) {
                scaleWhileMaintainingAspect(ScalingOption.ByHeight(i.scaledHeight.toDouble()))
                alignLeftToRightOf(i, padding = 5.0)
                centerYOn(i)
            }

            eventBus.register<AddedEntityEvent> {
                t.text = calculateTextFn()
            }
            eventBus.register<RemovedTowerEntityEvent> {
                t.text = calculateTextFn()
            }
            eventBus.register<RemovedSupplyDepotEntityEvent> {
                t.text = calculateTextFn()
            }
            alignLeftToRightOf(goldSection, padding = 40.0)
        }


    }
}