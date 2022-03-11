package input_processors

import com.soywiz.korev.MouseButton
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.component.MouseComponent
import com.soywiz.korge.view.Camera
import com.soywiz.korge.view.View
import com.soywiz.korge.view.Views
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.plus
import events.EventBus
import events.LeftControlAndEqual
import events.LeftControlAndMinus
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

const val ZOOM_DELTA = 0.04

class CameraInputProcessor(override val view: View, val eventBus: EventBus) : MouseComponent {
    var touchedDownX = 0.0
    var touchedDownY = 0.0
    var isMouseTouchedDown = false

    var originalCameraPosition = Point.Zero

    init {
        eventBus.register<LeftControlAndMinus> {
            zoomOutCamera()
        }
        eventBus.register<LeftControlAndEqual> {
            zoomInCamera()
        }
    }

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        //        println("went here? $event")
        when (event.type) {
            MouseEvent.Type.DRAG -> {
                //				println(
                //					"view.x: ${view.x}, view.y: ${view.y}," +
                //							" view.width: ${view.width}, view.height: ${view
                //							.height}, " +
                //							"view.globalXY(): ${view.globalXY()}, view.scale:
                //							${view.scale}, " +
                //							"view.scaledHeight: ${view.scaledHeight}, view
                //							.scaledWidth: ${
                //								view
                //									.scaledWidth
                //							}, " +
                //							"\nview.mouse.currentPosGlobal: ${view.mouse
                //							.currentPosGlobal}, " +
                //							"view.mouse.currentPosLocal: ${view.mouse
                //							.currentPosLocal}, " +
                //							"view.mouse.currentPosStage: ${view.mouse
                //							.currentPosStage}"
                //				)
                //				println(it)
                touchDragged(event.x, event.y)
            }
            MouseEvent.Type.UP -> touchUp()
            MouseEvent.Type.DOWN -> touchDown(
                event.x, event.y,
                event.button
            )
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
        originalCameraPosition = Point(view.x, view.y)
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
            val multiplier = sqrt(view.scale)
            val deltaX = (screenX.toDouble() - touchedDownX) * multiplier
            val deltaY = (screenY.toDouble() - touchedDownY) * multiplier

            val newCameraPosition = originalCameraPosition.plus(
                Point(deltaX, deltaY)
            )

            view.x = newCameraPosition.x
            view.y = newCameraPosition.y
        }
    }

    fun scrolled(amountY: Double) {
        when {
            amountY > -0.5 -> {
                zoomOutCamera()

            }
            amountY < 0.5 -> {
                zoomInCamera()
            }
        }
    }

    fun zoomOutCamera() {
        val newZoomFactor = max(0.05, view.scale - ZOOM_DELTA)
        setZoomFactor(newZoomFactor)
    }

    fun zoomInCamera() {
        val newZoomFactor = min(1.5, view.scale + ZOOM_DELTA)
        setZoomFactor(newZoomFactor)
    }

    fun setZoomFactor(newZoomFactor: Double) {
        val prevZoomFactor = view.scale
        view.scale = newZoomFactor

        // Make the view re-center after scaling
        view.x += (view.width * (prevZoomFactor - newZoomFactor)) / 2
        view.y += (view.height * (prevZoomFactor - newZoomFactor)) / 2
    }
}