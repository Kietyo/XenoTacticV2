package ui

import com.soywiz.korge.view.Text
import com.soywiz.korge.view.centerOnStage
import com.soywiz.korge.view.centerXOnStage
import engine.Engine

data class NotificationTextUpdateEvent(
    val text: String
)

class UINotificationText(
    val engine: Engine,
    text: String = "N/A"
) : Text(text) {
    init {
        engine.eventBus.register<NotificationTextUpdateEvent> {
            this.text = it.text
            centerXOnStage()
        }
    }
}