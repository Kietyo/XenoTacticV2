import com.soywiz.korge.Korge
import com.soywiz.korge.view.text
import com.soywiz.korio.async.runBlockingNoJs
import kotlin.jvm.JvmStatic

object DebugMain2 {

    @JvmStatic
    fun main(args: Array<String>) = runBlockingNoJs {
        Korge(width = 1280, height = 720) {
            text("Hello world")
        }
    }
}