package ui

import com.soywiz.korge.view.*
import com.soywiz.korim.text.TextAlignment
import components.ObjectPlacementComponent
import engine.Engine
import events.EventBus
import events.PointerActionChangeEvent
import input_processors.PointerAction

inline fun Container.uiActiveTextNotifier(
    engine: Engine,
    eventBus: EventBus
): UIActiveTextNotifier =
    UIActiveTextNotifier(engine, eventBus).addTo(this)

class UIActiveTextNotifier(
    val engine: Engine,
    val eventBus: EventBus
) : Container() {
    val placementComponent = engine.getOneTimeComponent<ObjectPlacementComponent>()
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
            activeButtonText.alpha = 0.0
            return
        }

        activeButtonText.alpha = 1.0
        when (pointerAction) {
            PointerAction.Inactive -> TODO()
            is PointerAction.HighlightForPlacement -> {
                activeButtonText.text = "Placing entity: ${pointerAction.mapEntity.friendlyName}"
            }
            is PointerAction.RemoveRockAtPlace -> {
                activeButtonText.text = "Removing rock"
            }
            is PointerAction.RemoveTowerAtPlace -> {
                activeButtonText.text = "Removing tower"
            }
        }
    }
}