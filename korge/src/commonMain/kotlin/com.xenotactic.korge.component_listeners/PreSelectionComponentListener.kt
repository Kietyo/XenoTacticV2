package com.xenotactic.korge.component_listeners

import korlibs.korge.view.Graphics
import korlibs.korge.view.addTo
import korlibs.korge.view.centerOn
import korlibs.image.color.Colors
import korlibs.math.geom.vector.StrokeInfo
import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.utils.toWorldDimensions
import com.xenotactic.gamelogic.components.PreSelectionComponent
import com.xenotactic.gamelogic.components.SizeComponent
import com.xenotactic.gamelogic.components.UIEntityContainerComponent
import com.xenotactic.gamelogic.components.UIPreSelectionComponent
import com.xenotactic.gamelogic.engine.Engine
import com.xenotactic.korge.ui.UIMapV2

class PreSelectionComponentListener(
    val engine: Engine
) : ComponentListener<PreSelectionComponent> {
    val world = engine.gameWorld.world
    val uiMap = engine.injections.getSingleton<UIMapV2>()
    val IN_PROCESS_SELECTION_COLOR = Colors.YELLOW.withAd(0.5)


    override fun onAdd(entityId: EntityId, component: PreSelectionComponent) {
        val uiEntityContainerComponent = world[entityId, UIEntityContainerComponent::class]
        val sizeComponent = world[entityId, SizeComponent::class]

        val (worldWidth, worldHeight) = toWorldDimensions(sizeComponent.width, sizeComponent.height, uiMap.gridSize)
        val selectionBox = Graphics().addTo(uiEntityContainerComponent.container).apply {
            updateShape {
                stroke(IN_PROCESS_SELECTION_COLOR, StrokeInfo(6.0)) {
                    this.rectHole(0.0, 0.0, worldWidth.value, worldHeight.value)
                }
            }
            centerOn(uiEntityContainerComponent.container)
        }

        world.modifyEntity(entityId) {
            addComponentOrThrow(UIPreSelectionComponent(selectionBox))
        }
    }

    override fun onRemove(entityId: EntityId, component: PreSelectionComponent) {
        val uiEntityContainerComponent = world[entityId, UIEntityContainerComponent::class]
        val uiPreSelectionComponent = world[entityId, UIPreSelectionComponent::class]
        uiEntityContainerComponent.container.removeChild(uiPreSelectionComponent.graphics)
        world.modifyEntity(entityId) {
            removeComponent<UIPreSelectionComponent>()
        }
    }

    override fun onExisting(entityId: EntityId, component: PreSelectionComponent) {
        TODO("Not yet implemented")
    }

}