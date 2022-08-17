package com.xenotactic.korge.systems

import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.fleks.components.SelectionType
import com.xenotactic.korge.input_processors.SelectedUIEntitiesEvent
import com.xenotactic.gamelogic.views.UIEntity

class SelectingEntitiesSystem(
    val engine: Engine
) {

    init {
        engine.eventBus.register<SelectedUIEntitiesEvent> {
            when (it.type) {
                SelectionType.PRE_SELECTION -> {
                    it.previousSelectionSnapshot.forEach(UIEntity::cancelSelection)
                    for (entity in it.newSelectionSnapshot) {
                        entity.doInProcessSelection()
                    }
                }
                SelectionType.SELECTED -> {
                    it.previousSelectionSnapshot.forEach(UIEntity::cancelSelection)
                    it.newSelectionSnapshot.forEach(UIEntity::doEndSelection)
                }
            }

        }
    }
}