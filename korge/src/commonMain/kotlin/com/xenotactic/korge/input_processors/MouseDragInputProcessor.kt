package com.xenotactic.korge.input_processors

import com.soywiz.klock.TimeProvider
import com.soywiz.korev.MouseButton
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.component.MouseComponent
import com.soywiz.korge.input.DraggableInfo
import com.soywiz.korge.view.View
import com.soywiz.korge.view.Views
import com.soywiz.korge.view.xy
import com.soywiz.korma.geom.IPoint
import com.soywiz.korma.geom.MPoint

import com.xenotactic.korge.engine.EComponent


fun DraggableInfo.asString(): String {
    return """
        DraggableInfo(
        viewStartXY: $viewStartXY
        viewPrevXY: $viewPrevXY
        viewNextXY: $viewNextXY
        viewDeltaXY: $viewDeltaXY
        )
    """.trimIndent()
}

enum class MouseDragStateType {
    UNKNOWN,
    START,
    DRAG,
    END
}

data class MouseDragStateSettings(
    var isEnabled: Boolean = true,
    var allowLeftClickDragging: Boolean = true,
    var allowRightClickDragging: Boolean = true,
    var allowMiddleClickDragging: Boolean = true,
)

data class MouseDragInputProcessor(
    override val view: View,
    val settings: MouseDragStateSettings = MouseDragStateSettings()
) : MouseComponent, EComponent {
    private val autoMove = true
    val info = DraggableInfo(view)

    private val ALLOWED_EVENTS = setOf(
        MouseEvent.Type.DOWN,
        MouseEvent.Type.DRAG,
        MouseEvent.Type.UP,
    )

    fun adjustSettings(fn: MouseDragStateSettings.() -> Unit) {
        fn(settings)
        reset()
    }

    // This is used to prevent other buttons from accidentally "closing" the current
    // drag state. For example, right clicking while doing a left click drag.
    private var activeButton = MouseButton.LEFT

    private fun getState(event: MouseEvent): MouseDragStateType {
        when (event.type) {
            MouseEvent.Type.DOWN -> {
                if (event.button == MouseButton.LEFT) {
                    return MouseDragStateType.START
                }
                if (event.button == MouseButton.MIDDLE) {
                    return MouseDragStateType.START
                }
                if (event.button == MouseButton.RIGHT) {
                    return MouseDragStateType.START
                }
                return MouseDragStateType.UNKNOWN
            }
            MouseEvent.Type.UP -> {
                if (event.button == MouseButton.LEFT) {
                    return MouseDragStateType.END
                }
                if (event.button == MouseButton.MIDDLE) {
                    return MouseDragStateType.END
                }
                if (event.button == MouseButton.RIGHT) {
                    return MouseDragStateType.END
                }
                return MouseDragStateType.UNKNOWN
            }
            MouseEvent.Type.DRAG -> return MouseDragStateType.DRAG
            else -> return MouseDragStateType.UNKNOWN
        }
    }

    private val currentPosition = MPoint()

    var startX = 0.0
    var startY = 0.0

    private var dragging = false

    private fun reset() {
        dragging = false
    }

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        if (!settings.isEnabled) return
        if (event.type !in ALLOWED_EVENTS) {
            return
        }

        if (!settings.allowLeftClickDragging && event.button == MouseButton.LEFT) {
            return
        }

        if (!settings.allowRightClickDragging && event.button == MouseButton.RIGHT) {
            return
        }

        if (dragging) {
            if (event.type != MouseEvent.Type.DRAG &&
                    event.button != activeButton) {
                return
            }
        }

        val state = getState(event)

//        println(
//            """
//            dragging: $dragging,
//            settings: $settings
//            event: $event,
//            state: $state
//            info: ${info.asString()}
//        """.trimIndent()
//        )

        if (state == MouseDragStateType.UNKNOWN) {
            require(!dragging)
            return
        }

        require(state != MouseDragStateType.UNKNOWN)

        currentPosition.copyFrom(views.globalMousePos)

        when (state) {
            MouseDragStateType.START -> {
                activeButton = event.button
                dragging = true
                startX = currentPosition.x
                startY = currentPosition.y
                info.reset()
            }
            MouseDragStateType.END -> {
                dragging = false
            }
            else -> Unit
        }

        val deltaX = currentPosition.x - startX
        val deltaY = currentPosition.y - startY

        info.set(
            deltaX,
            deltaY,
            state == MouseDragStateType.START,
            state == MouseDragStateType.END,
            TimeProvider.now(),
            startX,
            startY,
            currentPosition.x,
            currentPosition.y
        )

        if (dragging) {
            handle(event)
        }
    }

    fun handle(event: MouseEvent) {
        val state = getState(event)
        val view = view
        if (state == MouseDragStateType.START) {
            info.viewStartXY.copyFrom(view.pos)
        }
        //println("localDXY=${info.localDX(view)},${info.localDY(view)}")
        info.viewPrevXY.copyFrom(view.pos)
        info.viewNextXY.setTo(
            info.viewStartX + info.localDX(view),
            info.viewStartY + info.localDY(view)
        )
        info.viewDeltaXY.setTo(info.viewNextX - info.viewPrevX, info.viewNextY - info.viewPrevY)
        if (autoMove) {
            view.xy(info.viewNextXY)
        }
    }

}