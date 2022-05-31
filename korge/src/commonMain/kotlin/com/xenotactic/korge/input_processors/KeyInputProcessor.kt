package com.xenotactic.korge.input_processors

import com.soywiz.korev.Key
import com.soywiz.korev.KeyEvent
import com.soywiz.korge.baseview.BaseView
import com.soywiz.korge.component.KeyComponent
import com.soywiz.korge.view.Views
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EscapeButtonActionEvent
import com.xenotactic.korge.events.LeftControlAndEqual
import com.xenotactic.korge.events.LeftControlAndMinus

class KeyInputProcessor(override val view: BaseView,
                        val engine: Engine
) : KeyComponent {
    var isLeftControlHeldDown = false
    override fun Views.onKeyEvent(event: KeyEvent) {
        println(event)
        when (event.type) {
            KeyEvent.Type.DOWN -> when (event.key) {
                Key.LEFT_CONTROL -> {
                    isLeftControlHeldDown = true
                }
                Key.MINUS -> {
                    if (isLeftControlHeldDown) {
                        engine.eventBus.send(LeftControlAndMinus)
                    }
                }
                Key.EQUAL -> {
                    if (isLeftControlHeldDown) {
                        engine.eventBus.send(LeftControlAndEqual)
                    }
                }
                else -> {}
            }
            KeyEvent.Type.UP -> when (event.key) {
                Key.ESCAPE -> {
                    engine.eventBus.send(EscapeButtonActionEvent)
                }
                Key.LEFT_CONTROL -> {
                    isLeftControlHeldDown = false
                }
                else -> {}
            }
            KeyEvent.Type.TYPE -> Unit
        }
    }
}