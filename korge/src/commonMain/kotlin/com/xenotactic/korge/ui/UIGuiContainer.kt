package com.xenotactic.korge.ui

import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.event_listeners.RemoveUIEntitiesEvent
import com.xenotactic.korge.events.EntitySelectionChangedEvent
import com.xenotactic.korge.korge_utils.alignBottomToBottomOfWindow
import com.xenotactic.korge.korge_utils.distributeVertically
import com.xenotactic.korge.korge_utils.isEmpty
import com.xenotactic.korge.models.GameWorld
import com.xenotactic.korge.state.EditorState
import com.xenotactic.korge.state.GameMapApi

class UIGuiContainer(
    val stage: SContainer,
    val engine: Engine,
    val world: World,
    val gameWorld: GameWorld,
    val gameMapApi: GameMapApi
) {
    val eventBus = engine.eventBus
    val editorState = engine.injections.getSingleton<EditorState>()

    val middleSelectionContainer = stage.container {  }

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

        eventBus.register<EntitySelectionChangedEvent> {
            if (gameWorld.selectionFamily.size == 1 && gameWorld.isTowerEntity(gameWorld.selectionFamily.first())) {
                val towerId = gameWorld.selectionFamily.first()
                println("Selected one tower entity!")
                middleSelectionContainer.apply {
                    removeChildren()
                    val towerDamage = gameMapApi.calculateTowerDamage(towerId)
                    UITowerDetails(towerDamage).addTo(this) {
                        scaleWhileMaintainingAspect(ScalingOption.ByHeight(100.0))
                    }
                    centerXOnStage()
                    alignBottomToBottomOfWindow()
                }
            } else if (gameWorld.selectionFamily.isEmpty) {
                middleSelectionContainer.removeChildren()
            } else {
                middleSelectionContainer.removeChildren()
            }
        }
    }
}