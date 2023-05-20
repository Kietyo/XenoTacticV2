package com.xenotactic.korge.listeners_component

import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.components.SelectedComponent
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.toWorldDimensions
import com.xenotactic.korge.events.EntitySelectionChangedEvent
import com.xenotactic.korge.ui.UIMapV2
import korlibs.image.color.Colors
import korlibs.korge.view.Graphics
import korlibs.korge.view.addTo
import korlibs.korge.view.align.centerOn
import korlibs.math.geom.vector.StrokeInfo

class SelectionComponentListener(
    val engine: Engine
) : ComponentListener<SelectedComponent> {
    val world = engine.gameWorld.world
    val uiMap = engine.injections.getSingleton<UIMapV2>()
    private val SELECTION_COLOR = Colors.YELLOW

    override fun onAddOrReplace(entityId: EntityId, old: SelectedComponent?, new: SelectedComponent) {
        val uiEntityContainerComponent =
            world.get(entityId, com.xenotactic.gamelogic.components.UIEntityContainerComponent::class)
        val sizeComponent = world[entityId, com.xenotactic.gamelogic.components.SizeComponent::class]

        val (worldWidth, worldHeight) = toWorldDimensions(sizeComponent.width, sizeComponent.height, uiMap.gridSize)
        val selectionBox = Graphics().addTo(uiEntityContainerComponent.container).apply {
            updateShape {
                stroke(SELECTION_COLOR, StrokeInfo(6f)) {
                    this.rectHole(0f, 0f, worldWidth.toFloat(), worldHeight.toFloat())
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
        val uiEntityContainerComponent =
            world.getOrNull(entityId, com.xenotactic.gamelogic.components.UIEntityContainerComponent::class)
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