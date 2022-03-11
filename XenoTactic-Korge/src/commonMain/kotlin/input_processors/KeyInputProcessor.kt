package input_processors

import com.soywiz.korev.Key
import com.soywiz.korev.KeyEvent
import com.soywiz.korge.baseview.BaseView
import com.soywiz.korge.component.KeyComponent
import com.soywiz.korge.view.Views
import events.EscapeButtonActionEvent
import events.EventBus
import events.LeftControlAndEqual
import events.LeftControlAndMinus

class KeyInputProcessor(override val view: BaseView, val eventBus: EventBus) : KeyComponent {
    var isLeftControlHeldDown = false
    override fun Views.onKeyEvent(event: KeyEvent) {
        println(event)
        when (event.type) {
            KeyEvent.Type.UP -> when (event.key) {
                Key.ESCAPE -> {
                    eventBus.send(EscapeButtonActionEvent)
                }
                Key.LEFT_CONTROL -> {
                    isLeftControlHeldDown = false
                }
            }
            KeyEvent.Type.DOWN -> when (event.key) {
                Key.LEFT_CONTROL -> {
                    isLeftControlHeldDown = true
                }
                Key.MINUS -> {
                    if (isLeftControlHeldDown) {
                        eventBus.send(LeftControlAndMinus)
                    }
                }
                Key.EQUAL -> {
                    if (isLeftControlHeldDown) {
                        eventBus.send(LeftControlAndEqual)
                    }
                }
            }
            KeyEvent.Type.TYPE -> Unit
        }
    }
}