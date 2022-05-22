package input_processors

import com.soywiz.klock.TimeProvider
import com.soywiz.korev.MouseButton
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.component.MouseComponent
import com.soywiz.korge.input.DraggableInfo
import com.soywiz.korge.view.View
import com.soywiz.korge.view.Views
import com.soywiz.korge.view.xy
import com.soywiz.korma.geom.Point


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

enum class MouseDragState {
    UNKNOWN,
    START,
    DRAG,
    END
}

data class MouseDragKomponentSettings(
    var allowLeftClickDragging: Boolean = true,
    var allowRightClickDragging: Boolean = true,
    var allowMiddleClickDragging: Boolean = true,
)

data class MouseDragKomponent(
    override val view: View,
    val settings: MouseDragKomponentSettings = MouseDragKomponentSettings()
) : MouseComponent {
    val autoMove = true
    val info = DraggableInfo(view)

    private val ALLOWED_EVENTS = setOf(
        MouseEvent.Type.DOWN,
        MouseEvent.Type.DRAG,
        MouseEvent.Type.UP,
    )

    fun adjustSettings(fn: MouseDragKomponentSettings.() -> Unit) {
        fn(settings)
        reset()
    }

    // This is used to prevent other buttons from accidentally "closing" the current
    // drag state. For example, right clicking while doing a left click drag.
    private var activeButton = MouseButton.LEFT

    private fun getState(event: MouseEvent): MouseDragState {
        when (event.type) {
            MouseEvent.Type.DOWN -> {
                if (event.button == MouseButton.LEFT) {
                    return MouseDragState.START
                }
                if (event.button == MouseButton.MIDDLE) {
                    return MouseDragState.START
                }
                if (event.button == MouseButton.RIGHT) {
                    return MouseDragState.START
                }
                return MouseDragState.UNKNOWN
            }
            MouseEvent.Type.UP -> {
                if (event.button == MouseButton.LEFT) {
                    return MouseDragState.END
                }
                if (event.button == MouseButton.MIDDLE) {
                    return MouseDragState.END
                }
                if (event.button == MouseButton.RIGHT) {
                    return MouseDragState.END
                }
                return MouseDragState.UNKNOWN
            }
            MouseEvent.Type.DRAG -> return MouseDragState.DRAG
            else -> return MouseDragState.UNKNOWN
        }
    }

    private val currentPosition = Point()

    var startX = 0.0
    var startY = 0.0

    var dragging = false

    private fun reset() {
        dragging = false
    }

    override fun onMouseEvent(views: Views, event: MouseEvent) {
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

        println(
            """
            dragging: $dragging,
            settings: $settings
            event: $event,
            state: $state
            info: ${info.asString()}
        """.trimIndent()
        )

        if (state == MouseDragState.UNKNOWN) {
            require(!dragging)
            return
        }

        require(state != MouseDragState.UNKNOWN)

        currentPosition.copyFrom(views.globalMouseXY)

        when (state) {
            MouseDragState.START -> {
                require(!dragging)
                activeButton = event.button
                dragging = true
                startX = currentPosition.x
                startY = currentPosition.y
                info.reset()
            }
            MouseDragState.END -> {
                dragging = false
            }
            else -> Unit
        }

        val deltaX = currentPosition.x - startX
        val deltaY = currentPosition.y - startY

        info.set(
            deltaX,
            deltaY,
            state == MouseDragState.START,
            state == MouseDragState.END,
            TimeProvider.now()
        )

        if (dragging) {
            handle(event)
        }
    }

    fun handle(event: MouseEvent) {
        val state = getState(event)
        val view = view
        if (state == MouseDragState.START) {
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