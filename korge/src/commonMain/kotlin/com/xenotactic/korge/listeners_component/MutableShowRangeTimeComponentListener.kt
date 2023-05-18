package com.xenotactic.korge.listeners_component

import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.components.RangeComponent
import com.xenotactic.gamelogic.components.SizeComponent
import com.xenotactic.gamelogic.components.UIEntityContainerComponent
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.toWorldUnit
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

        val uiEntityContainerComponent = world[entityId, UIEntityContainerComponent::class]
        val rangeComponent = world[entityId, RangeComponent::class]
        val sizeComponent = world[entityId, SizeComponent::class]

        val radius = rangeComponent.range.toWorldUnit(uiMap.gridSize)
        val xOffset = (sizeComponent.width / 2).toWorldUnit(uiMap.gridSize)
        val yOffset = (sizeComponent.height / 2).toWorldUnit(uiMap.gridSize)
        val rangeView = Circle(
            radius.toFloat(), fill = Colors.WHITE.withAd(0.5),
            strokeThickness = 1f
        ).addTo(uiEntityContainerComponent.container) {
            x += xOffset.toFloat() - radius.toFloat()
            y += yOffset.toFloat() - radius.toFloat()
        }

        world.modifyEntity(entityId) {
            addComponentOrThrow(UIShowRangeComponent(rangeView))
        }
    }
}