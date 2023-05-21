package com.xenotactic.korge.ui

import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.korge.components.ObjectPlacementEComponent
import com.xenotactic.korge.events.PointerActionChangeEvent
import com.xenotactic.korge.input_processors.PointerAction
import korlibs.image.text.TextAlignment
import korlibs.korge.view.*

inline fun Container.uiActiveTextNotifier(
    engine: Engine,
    eventBus: EventBus
): UIActiveTextNotifier =
    UIActiveTextNotifier(engine, eventBus).addTo(this)

class UIActiveTextNotifier(
    val engine: Engine,
    val eventBus: EventBus
) : Container() {
    val placementComponent = engine.injections.getSingleton<ObjectPlacementEComponent>()
    val activeButtonText: Text

    init {
        activeButtonText = text(
            "Hello world", alignment =
            TextAlignment.CENTER
        ).alpha(0.0)

        eventBus.register<PointerActionChangeEvent> {
            afterPointerActionChange()
        }
    }

    private fun afterPointerActionChange() {
        val pointerAction = placementComponent.pointerAction
        if (pointerAction == PointerAction.Inactive) {
            activeButtonText.alpha = 0f
            return
        }

        activeButtonText.alpha = 1f
        when (pointerAction) {
            PointerAction.Inactive -> TODO()
            is PointerAction.HighlightForPlacement -> {
                activeButtonText.text = "Placing entity: ${pointerAction.mapEntity.friendlyName}"
            }

            is PointerAction.RemoveEntityAtPlace -> {
                activeButtonText.text = when (pointerAction.entityType) {
                    MapEntityType.START -> TODO()
                    MapEntityType.FINISH -> TODO()
                    MapEntityType.CHECKPOINT -> TODO()
                    MapEntityType.ROCK -> "Removing rock"
                    MapEntityType.TOWER -> "Removing tower"
                    MapEntityType.TELEPORT_IN -> TODO()
                    MapEntityType.TELEPORT_OUT -> TODO()
                    MapEntityType.SMALL_BLOCKER -> TODO()
                    MapEntityType.SPEED_AREA -> TODO()
                    MapEntityType.MONSTER -> TODO()
                    MapEntityType.SUPPLY_DEPOT -> "Removing supply depot"
                }
            }
        }
    }
}