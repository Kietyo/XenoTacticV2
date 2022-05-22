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

data class MouseDragKomponent(
    override val view: View,
) : MouseComponent {
    var allowLeftClickDrag = true
    var allowRightClickDrag = true
    val autoMove = true
    val info = DraggableInfo(view)

    private val ALLOWED_EVENTS = setOf(
        MouseEvent.Type.DOWN,
        MouseEvent.Type.DRAG,
        MouseEvent.Type.UP,
    )

    // This is used to prevent other buttons from accidentally "closing" the current
    // drag state. For example, right clicking while doing a left click drag.
    private var activeButton = MouseButton.LEFT

    fun getState(event: MouseEvent): MouseDragState {
        when (event.type) {
            MouseEvent.Type.DOWN -> {
                if (allowLeftClickDrag && event.button == MouseButton.LEFT) {
                    return MouseDragState.START
                }
                if (allowRightClickDrag && event.button == MouseButton.RIGHT) {
                    return MouseDragState.START
                }
                return MouseDragState.UNKNOWN
            }
            MouseEvent.Type.UP -> {
                if (allowLeftClickDrag && event.button == MouseButton.LEFT) {
                    return MouseDragState.END
                }
                if (allowRightClickDrag && event.button == MouseButton.RIGHT) {
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

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        if (event.type !in ALLOWED_EVENTS) {
            return
        }

        val state = getState(event)
        println(
            """
            event: $event,
            state: $state
            info: ${info.asString()}
        """.trimIndent()
        )
        if (state == MouseDragState.UNKNOWN) {
            return
        }

        if (state != MouseDragState.START && !dragging) return
        if (dragging && state != MouseDragState.DRAG) {
            if (event.button != activeButton) {
                return
            }
        }

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

        handle(event)
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