import com.soywiz.korge.Korge
import com.soywiz.korge.view.image
import com.soywiz.korge.view.text
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.PNG
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.file.std.rootLocalVfs
import kotlinx.coroutines.runBlocking

object DebugMain {
    val RESOURCES_FOLDER = rootLocalVfs["XenoTactic-Korge/src/commonMain/resources"]

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"]) {

            val vfs = rootLocalVfs.listNames()

            println(vfs)
            println(rootLocalVfs.absolutePath)

            println("Hello orld")

            text("blah blah main")

            val png = RESOURCES_FOLDER["play_button_16x16.png"].readBitmap(PNG)
            image(png) {
                scale = 3.0
            }
        }
    }
}