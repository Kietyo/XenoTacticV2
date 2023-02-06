package com.xenotactic.korge.component_listeners

import com.soywiz.korge.view.Graphics
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.centerOn
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.vector.StrokeInfo
import com.soywiz.korma.geom.vector.rectHole
import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.components.SelectedComponent
import com.xenotactic.gamelogic.utils.toWorldDimensions
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EntitySelectionChangedEvent
import com.xenotactic.korge.state.DeadUIZonesState
import com.xenotactic.korge.ui.UIMapV2

class SelectionComponentListener(
    val engine: Engine
) : ComponentListener<SelectedComponent> {
    val world = engine.gameWorld.world
    val uiMap = engine.injections.getSingleton<UIMapV2>()
    private val SELECTION_COLOR = Colors.YELLOW

    override fun onAdd(entityId: EntityId, component: SelectedComponent) {
        val uiEntityContainerComponent = world.get(entityId, com.xenotactic.gamelogic.components.UIEntityContainerComponent::class)
        val sizeComponent = world[entityId, com.xenotactic.gamelogic.components.SizeComponent::class]

        val (worldWidth, worldHeight) = toWorldDimensions(sizeComponent.width, sizeComponent.height, uiMap.gridSize)
        val selectionBox = Graphics().addTo(uiEntityContainerComponent.container).apply {
            updateShape {
                stroke(SELECTION_COLOR, StrokeInfo(6.0)) {
                    this.rectHole(0.0, 0.0, worldWidth.value, worldHeight.value)
                }
            }
            centerOn(uiEntityContainerComponent.container)
        }

        world.modifyEntity(entityId) {
            addComponentOrThrow(com.xenotactic.gamelogic.components.UISelectionComponent(selectionBox))
        }

        engine.eventBus.send(EntitySelectionChangedEvent)
    }

    override fun onRemove(entityId: EntityId, component: SelectedComponent) {
        println("Removed selection for entity: $entityId")
        val uiEntityContainerComponent = world.getOrNull(entityId, com.xenotactic.gamelogic.components.UIEntityContainerComponent::class)
        if (uiEntityContainerComponent == null) {
            // This entity was already removed.
            return
        }
        val uiSelectionComponent = world[entityId, com.xenotactic.gamelogic.components.UISelectionComponent::class]
        uiEntityContainerComponent.container.removeChild(uiSelectionComponent.graphics)
        world.modifyEntity(entityId) {
            removeComponent<com.xenotactic.gamelogic.components.UISelectionComponent>()
        }

        engine.eventBus.send(EntitySelectionChangedEvent)
    }

    override fun onExisting(entityId: EntityId, component: SelectedComponent) {
        TODO("Not yet implemented")
    }
}