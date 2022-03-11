package ui

import com.soywiz.klogger.Logger
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import events.EventBus
import input_processors.CameraInputProcessor

object UIMapOverlayOutsideClickedEvent

inline fun Container.uiMapOverlay(
    eventBus: EventBus
): UIMapOverlay = UIMapOverlay(eventBus).addTo(this)

class UIMapOverlay(
    val eventBus: EventBus
) : Container() {

    var isCurrentlySet = false

    fun setOverlay(view: View) {
        if (isCurrentlySet) return
        val globalAreaDimensions = getVisibleGlobalArea()
        val background = this.solidRect(
            globalAreaDimensions.width, globalAreaDimensions.height,
            color = Colors.BLACK.withAd(0.7)
        )
        background.onClick {
            logger.info {
                "background clicked!"
            }
            eventBus.send(UIMapOverlayOutsideClickedEvent)
        }

        view.scaleWhileMaintainingAspect(globalAreaDimensions.width, globalAreaDimensions.height)
        view.centerOn(background)
        view.addTo(this)
        // Add an empty onClick listener to "eat" the input if the view itself is clicked.
        // This is to avoid triggering the onClick listener of the background.
        view.onClick { }

        val cameraInputProcessor = CameraInputProcessor(view, eventBus)
        addComponent(cameraInputProcessor)

        isCurrentlySet = true
    }

    fun clearOverlay() {
        removeAllComponents()
        removeChildren()
        isCurrentlySet = false
    }

    companion object {
        val logger = Logger<UIMapOverlay>()
    }
}