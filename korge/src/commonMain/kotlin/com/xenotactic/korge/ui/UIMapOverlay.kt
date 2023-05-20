package com.xenotactic.korge.ui

import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.korge.input_processors.CameraInputProcessor
import korlibs.image.color.Colors
import korlibs.korge.input.onClick
import korlibs.korge.view.*
import korlibs.korge.view.align.centerOn
import korlibs.logger.Logger

object UIMapOverlayOutsideClickedEvent

class UIMapOverlay(
    val engine: Engine
) : Container() {

    var isCurrentlySet = false

    fun setOverlay(view: View) {
        if (isCurrentlySet) return
        val globalAreaDimensions = getVisibleGlobalArea()
        val background = this.solidRect(
            globalAreaDimensions.widthD, globalAreaDimensions.heightD,
            color = Colors.BLACK.withAd(0.7)
        )
        background.onClick {
            logger.info {
                "background clicked!"
            }
            engine.eventBus.send(UIMapOverlayOutsideClickedEvent)
        }

        view.scaleWhileMaintainingAspect(
            ScalingOption.ByWidthAndHeight(
                globalAreaDimensions.widthD,
                globalAreaDimensions.heightD
            )
        )
        view.centerOn(background)
        view.addTo(this)
        // Add an empty onClick listener to "eat" the input if the view itself is clicked.
        // This is to avoid triggering the onClick listener of the background.
        view.onClick { }

        val cameraInputProcessor = CameraInputProcessor(view, engine)
        cameraInputProcessor.setup(this)

        isCurrentlySet = true
    }

    fun clearOverlay() {
        clearEvents()
        removeChildren()
        isCurrentlySet = false
    }

    companion object {
        val logger = Logger<UIMapOverlay>()
    }
}