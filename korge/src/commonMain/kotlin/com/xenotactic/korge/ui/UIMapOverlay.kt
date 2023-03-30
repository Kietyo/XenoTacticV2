package com.xenotactic.korge.ui

import korlibs.logger.Logger
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.ScalingOption
import com.soywiz.korge.view.View
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.centerOn
import com.soywiz.korge.view.getVisibleGlobalArea
import com.soywiz.korge.view.scaleWhileMaintainingAspect
import com.soywiz.korge.view.solidRect
import korlibs.image.color.Colors
import com.xenotactic.gamelogic.engine.Engine
import com.xenotactic.korge.input_processors.CameraInputProcessor

object UIMapOverlayOutsideClickedEvent

class UIMapOverlay(
    val engine: Engine
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
            engine.eventBus.send(UIMapOverlayOutsideClickedEvent)
        }

        view.scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(
            globalAreaDimensions.width,
            globalAreaDimensions.height
        ))
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