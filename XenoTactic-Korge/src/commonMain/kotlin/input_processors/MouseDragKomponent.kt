package input_processors

import com.soywiz.klock.TimeProvider
import com.soywiz.korev.MouseButton
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.component.MouseComponent
import com.soywiz.korge.input.DraggableInfo
import com.soywiz.korge.input.MouseDragState
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

data class MouseDragKomponent(
    override val view: View,
    val actualView: View
): MouseComponent {
    val autoMove = true
    val info = DraggableInfo(actualView)

    val ALLOWED_EVENTS = setOf(
        MouseEvent.Type.DOWN,
        MouseEvent.Type.DRAG,
        MouseEvent.Type.UP,
    )

    fun getState(event: MouseEvent): MouseDragState {
        return when {
            event.type == MouseEvent.Type.DOWN && event.button == MouseButton.LEFT -> MouseDragState.START
            event.type == MouseEvent.Type.UP && event.button == MouseButton.LEFT -> MouseDragState.END
            event.type == MouseEvent.Type.DRAG -> MouseDragState.DRAG
            else -> TODO()
        }
    }

    val mousePos = Point()

    var startX = 0.0
    var startY = 0.0

    var dragging = false

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        if (event.type !in ALLOWED_EVENTS) {
            return
        }

        val state = getState(event)

        if (state != MouseDragState.START && !dragging) return

        mousePos.copyFrom(views.globalMouseXY)

        val px = mousePos.x
        val py = mousePos.y

        when (state) {
            MouseDragState.START -> {
                dragging = true
                startX = px
                startY = py
                info.reset()
            }
            MouseDragState.END -> {
                dragging = false
            }
        }

        val cx = mousePos.x
        val cy = mousePos.y
        val dx = cx - startX
        val dy = cy - startY

        info.set(dx, dy, state.isStart, state.isEnd, TimeProvider.now())

        handle(event)
//        onDrag?.invoke(info)
        //println("DRAG: $dx, $dy, $start, $end")

    }

    fun handle(event: MouseEvent) {
        val state = getState(event)
        val view = actualView
        println("""
            event: $event,
            info: ${info.asString()}
        """.trimIndent())
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