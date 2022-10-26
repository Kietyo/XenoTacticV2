package com.xenotactic.korge.event_listeners

import com.xenotactic.korge.components.AnimationComponent
import com.xenotactic.korge.components.BottomLeftPositionComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.ui.UIMapV2

interface TestEventListenerI<T> {
    fun handle(event: T)
}

class UIMapEventListeners(
    val engine: Engine
) {
    val uiMap = engine.injections.getSingleton<UIMapV2>()
    init {

    }


}