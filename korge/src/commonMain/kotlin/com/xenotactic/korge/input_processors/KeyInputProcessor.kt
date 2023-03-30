package com.xenotactic.korge.input_processors

import com.soywiz.korev.EventListener
import com.soywiz.korev.Key
import com.soywiz.korev.KeyEvent
import com.soywiz.korev.ReshapeEvent
import korlibs.korge.view.BaseView
import korlibs.korge.view.Views
import com.xenotactic.gamelogic.engine.Engine
import com.xenotactic.korge.events.EscapeButtonActionEvent
import com.xenotactic.korge.events.LeftControlAndEqual
import com.xenotactic.korge.events.LeftControlAndMinus

class KeyInputProcessor( val view: BaseView,
                        val engine: Engine
) {
    fun setup(eventListener: EventListener) {
        eventListener.onEvents(*KeyEvent.Type.ALL) {
            onKeyEvent(it)
        }
    }

    var isLeftControlHeldDown = false
    fun onKeyEvent(event: KeyEvent) {
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