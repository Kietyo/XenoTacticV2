package com.xenotactic.korge.listeners_component

import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.korge.components.UIShowRangeComponent
import com.xenotactic.korge.ui.UIMapV2

class UIShowRangeComponentListener(val engine: Engine) : ComponentListener<UIShowRangeComponent> {
    val uiMap = engine.injections.getSingleton<UIMapV2>()

    override fun onRemove(entityId: EntityId, component: UIShowRangeComponent) {
        uiMap.rangeIndicatorLayer.removeChild(component.view)
    }
}