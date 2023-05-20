package com.xenotactic.korge.input_processors

import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.minus
import com.xenotactic.gamelogic.utils.times
import com.xenotactic.gamelogic.utils.toScale
import com.xenotactic.korge.events.LeftControlAndEqual
import com.xenotactic.korge.events.LeftControlAndMinus
import korlibs.event.EventListener
import korlibs.event.MouseButton
import korlibs.event.MouseEvent
import korlibs.korge.view.View
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

const val ZOOM_DELTA = 0.04

class CameraInputProcessor(val view: View, val engine: Engine) {
    var touchedDownX = 0.0
    var touchedDownY = 0.0
    var isMouseTouchedDown = false

    var originalCameraPosition = IPoint.ZERO

    init {
        engine.eventBus.register<LeftControlAndMinus> {
            zoomOutCamera()
        }
        engine.eventBus.register<LeftControlAndEqual> {
            zoomInCamera()
        }
    }

    fun setup(eventListener: EventListener) {
        eventListener.onEvents(MouseEvent.Type.DRAG, MouseEvent.Type.UP, MouseEvent.Type.DOWN, MouseEvent.Type.SCROLL) {
            onMouseEvent(it)
        }
    }

    private fun onMouseEvent(event: MouseEvent) {
        //        println("went here? $event")
        when (event.type) {
            //            MouseEvent.Type.DRAG -> {
            //                //				println(
            //                //					"view.x: ${view.x}, view.y: ${view.y}," +
            //                //							" view.width: ${view.width}, view.height: ${view
            //                //							.height}, " +
            //                //							"view.globalXY(): ${view.globalXY()}, view.scale:
            //                //							${view.scale}, " +
            //                //							"view.scaledHeight: ${view.scaledHeight}, view
            //                //							.scaledWidth: ${
            //                //								view
            //                //									.scaledWidth
            //                //							}, " +
            //                //							"\nview.mouse.currentPosGlobal: ${view.mouse
            //                //							.currentPosGlobal}, " +
            //                //							"view.mouse.currentPosLocal: ${view.mouse
            //                //							.currentPosLocal}, " +
            //                //							"view.mouse.currentPosStage: ${view.mouse
            //                //							.currentPosStage}"
            //                //				)
            //                //				println(it)
            //                touchDragged(event.x, event.y)
            //            }
            //            MouseEvent.Type.UP -> touchUp()
            //            MouseEvent.Type.DOWN -> touchDown(
            //                event.x, event.y,
            //                event.button
            //            )
            MouseEvent.Type.SCROLL -> scrolled(event.scrollDeltaYPixels)
            else -> Unit
        }
    }

    fun touchDown(screenX: Int, screenY: Int, button: MouseButton) {
        //        println(
        //            "touchDown: screenX: $screenX, screenY: $screenY, " +
        //                    "button: $button"
        //        )
        touchedDownX = screenX.toDouble()
        touchedDownY = screenY.toDouble()
        isMouseTouchedDown = true
        originalCameraPosition = IPoint(view.x, view.y)
    }

    fun touchUp() {
        //        println("touchedDownX: $touchedDownX, touchedDownY: $touchedDownY")
        //        println(
        //            "touchUp: screenX: $screenX, screenY: $screenY, pointer: $pointer, " +
        //                    "button: $button"
        //        )
        //        val deltaX = screenX.toFloat() - touchedDownX
        //        val deltaY = screenY.toFloat() - touchedDownY
        //        println("deltaX: $deltaX, deltaY: $deltaY")
        isMouseTouchedDown = false
    }

    fun touchDragged(screenX: Int, screenY: Int) {
        if (isMouseTouchedDown) {
            //            val multiplier = 1.0 / zoomFactor
            //            val multiplier = (0.9 / zoomFactor).clamp(0.1, 0.9)
            //            val multiplier = zoomFactor.clamp(0.1, 1.0)
            val multiplier = sqrt(view.scale.avgD)
            val deltaX = (screenX.toDouble() - touchedDownX) * multiplier
            val deltaY = (screenY.toDouble() - touchedDownY) * multiplier

            val newCameraPosition = originalCameraPosition.plus(
                IPoint(deltaX, deltaY)
            )

            view.x = newCameraPosition.x.toFloat()
            view.y = newCameraPosition.y.toFloat()
        }
    }

    fun scrolled(amountY: Float) {
        when {
            amountY > -0.5 -> {
                zoomOutCamera()

            }

            amountY < 0.5 -> {
                zoomInCamera()
            }
        }
    }

    // Max zoom out. Lower value = more zoom out.
    val MAX_ZOOM_OUT = 0.2
    val MAX_ZOOM_IN = 2.0

    private fun zoomOutCamera() {
        val newZoomFactor = max(MAX_ZOOM_OUT, view.scale.avgD - ZOOM_DELTA)
        setZoomFactor(newZoomFactor)
    }

    private fun zoomInCamera() {
        val newZoomFactor = min(MAX_ZOOM_IN, view.scale.avgD + ZOOM_DELTA)
        setZoomFactor(newZoomFactor)
    }

    fun setZoomFactor(newZoomFactor: Double) {
        val prevZoomFactor = view.scale
        view.scale = newZoomFactor.toScale()

        // Make the view re-center after scaling
        view.x += (view.width * (prevZoomFactor - newZoomFactor)) / 2
        view.y += (view.height * (prevZoomFactor - newZoomFactor)) / 2
    }
}

