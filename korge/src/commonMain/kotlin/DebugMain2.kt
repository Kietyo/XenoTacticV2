import com.soywiz.korge.Korge
import com.soywiz.korge.component.docking.dockedTo
import com.soywiz.korge.ui.uiProgressBar
import com.soywiz.korge.view.*
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korma.geom.Anchor
import kotlin.jvm.JvmStatic

object DebugMain2 {

    @JvmStatic
    fun main(args: Array<String>) = runBlockingNoJs {
        Korge(width = 1280, height = 720) {
//            text("Hello world")

            lateinit var circle: View
            val cont = container {
                circle = circle(50.0) {
                    anchor(Anchor.CENTER)
                }

                uiProgressBar(100.0, 20.0, 40.0) {
                    y = -75.0
                    x -= width / 2.0
                }


//                val rect = solidRect(100.0, 20.0) {
//                    anchor(Anchor.CENTER)
//                    y = -75.0
//                }
            }



            addUpdater {
                val (mouseX, mouseY) = views.globalMouseXY
                cont.xy(mouseX, mouseY)
            }
        }
    }
}