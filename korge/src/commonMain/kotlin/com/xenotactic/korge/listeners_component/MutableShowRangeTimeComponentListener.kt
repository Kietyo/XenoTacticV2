package com.xenotactic.korge.listeners_component

import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.korge.components.MutableShowRangeTimeComponent
import com.xenotactic.korge.ui.UIMapV2

class MutableShowRangeTimeComponentListener(
    val engine: Engine
): ComponentListener<MutableShowRangeTimeComponent> {
    val world = engine.gameWorld.world
    val uiMap = engine.injections.getSingleton<UIMapV2>()
    override fun onAddOrReplace(entityId: EntityId, old: MutableShowRangeTimeComponent?,
        new: MutableShowRangeTimeComponent) {
        require(old == null)
        require(new.showTimeRemainingMillis > 0)

    }
}