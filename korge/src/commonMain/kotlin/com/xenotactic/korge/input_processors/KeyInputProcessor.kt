package com.xenotactic.korge.input_processors

import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.korge.events.EscapeButtonActionEvent
import com.xenotactic.korge.events.LeftControlAndEqual
import com.xenotactic.korge.events.LeftControlAndMinus
import korlibs.event.EventListener
import korlibs.event.Key
import korlibs.event.KeyEvent
import korlibs.korge.view.BaseView

class KeyInputProcessor(val view: BaseView,
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