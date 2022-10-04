import com.soywiz.korge.Korge
import com.soywiz.korge.component.docking.dockedTo
import com.soywiz.korge.view.anchor
import com.soywiz.korge.view.circle
import com.soywiz.korge.view.text
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korma.geom.Anchor
import kotlin.jvm.JvmStatic

object DebugMain2 {

    @JvmStatic
    fun main(args: Array<String>) = runBlockingNoJs {
        Korge(width = 1280, height = 720) {
//            text("Hello world")

            circle(100.0) {
                anchor(Anchor.CENTER)
            }
        }
    }
}