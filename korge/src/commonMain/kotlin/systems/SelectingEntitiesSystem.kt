package systems

import engine.Engine
import input_processors.SelectedUIEntitiesEvent
import input_processors.SelectionType
import ui.UIEntity

class SelectingEntitiesSystem(
    val engine: Engine
) {



    init {

        engine.eventBus.register<SelectedUIEntitiesEvent> {
            when (it.type) {
                SelectionType.IN_PROCESS -> {
                    it.previousSelectionSnapshot.forEach(UIEntity::cancelSelection)
                    for (entity in it.newSelectionSnapshot) {
                        entity.doInProcessSelection()
                    }
                }
                SelectionType.END -> {
                    it.previousSelectionSnapshot.forEach(UIEntity::cancelSelection)
                    it.newSelectionSnapshot.forEach(UIEntity::doEndSelection)
                }
            }

        }
    }
}