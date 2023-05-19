package com.xenotactic.korge.listeners_component

import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.components.BottomLeftPositionComponent
import com.xenotactic.gamelogic.components.RangeComponent
import com.xenotactic.gamelogic.components.SizeComponent
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.korge.components.MutableShowRangeTimeComponent
import com.xenotactic.korge.components.UIShowRangeComponent
import com.xenotactic.korge.ui.UIMapV2
import korlibs.image.color.Colors
import korlibs.korge.view.Circle
import korlibs.korge.view.addTo

class MutableShowRangeTimeComponentListener(
    val engine: Engine
) : ComponentListener<MutableShowRangeTimeComponent> {
    val world = engine.gameWorld.world
    val uiMap = engine.injections.getSingleton<UIMapV2>()
    override fun onAddOrReplace(entityId: EntityId, old: MutableShowRangeTimeComponent?,
        new: MutableShowRangeTimeComponent) {
        require(old == null)
        require(new.showTimeRemainingMillis > 0)

        val rangeComponent = world[entityId, RangeComponent::class]
        val sizeComponent = world[entityId, SizeComponent::class]
        val bottomLeftPositionComponent = world[entityId, BottomLeftPositionComponent::class]
        val radius = rangeComponent.range.toWorldUnit(uiMap.gridSize)

        val (worldX, worldY) = uiMap.getWorldCoordinates(
            bottomLeftPositionComponent.x - rangeComponent.range + sizeComponent.width / 2,
            bottomLeftPositionComponent.y + rangeComponent.range + sizeComponent.height / 2,
        )

        val rangeView = Circle(
            radius.toFloat(),
            fill = Colors.WHITE.withAd(0.15),
            stroke = Colors.WHITE.withAd(0.75),
            strokeThickness = 5f
        ).addTo(uiMap.rangeIndicatorLayer) {
            x = worldX.toFloat()
            y = worldY.toFloat()
        }

        world.modifyEntity(entityId) {
            addComponentOrThrow(UIShowRangeComponent(rangeView))
        }
    }

    override fun onRemove(entityId: EntityId, component: MutableShowRangeTimeComponent) {
        world.modifyEntity(entityId) {
            removeComponent<UIShowRangeComponent>()
        }
    }
}