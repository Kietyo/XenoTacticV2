import com.soywiz.korge.Korge
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.ASE
import com.soywiz.korim.format.readImageDataContainer
import com.soywiz.korim.format.toProps
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.file.std.rootLocalVfs
import com.soywiz.korma.geom.Anchor
import com.xenotactic.gamelogic.views.UIEightDirectionalSprite
import kotlin.jvm.JvmStatic

object DebugMain {
    val RESOURCES_FOLDER = rootLocalVfs["XenoTactic-Korge/src/commonMain/resources"]

    @JvmStatic
    fun main(args: Array<String>) = runBlockingNoJs {
        Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"]) {

//            val vfs = rootLocalVfs.listNames()
//
//            println(vfs)
//            println(rootLocalVfs.absolutePath)
//
//            println("Hello orld")
//
//            text("blah blah main")
//
//            val png = RESOURCES_FOLDER["play_button_16x16.png"].readBitmap(PNG)
//            image(png) {
//                scale = 3.0
//            }

            val resources = resourcesVfs.listSimple()
            println(resources)

//            val asp = resourcesVfs["vampire.ase"].readImageDataContainer(ASE.toProps())
            val asp = resourcesVfs["8_directional_character.aseprite"].readImageDataContainer(ASE.toProps())
            println(asp)

            println(
                """
                asp.imageDatas.size: ${asp.imageDatas.size}
                asp.imageDatas: ${asp.imageDatas}
                asp.imageDatasByName.size: ${asp.imageDatasByName.size}
                asp.imageDatasByName: ${asp.imageDatasByName}
            """.trimIndent()
            )



            println()

            val imageData = asp.imageDatasByName["up"]!!


            println(
                """
                imageData.animations.size: ${imageData.animations.size}
                imageData.animationsByName: ${imageData.animationsByName}
                imageData.frames.size: ${imageData.frames.size}
                imageData.framesByName: ${imageData.framesByName}
            """.trimIndent()
            )

//            val imageFrame = imageData.animationsByName["down"]!!
//
//            println(imageFrame)
//
//            println("""
//                imageFrame.frames: ${imageFrame.frames}
//                imageFrame.layers: ${imageFrame.layers}
//            """.trimIndent())

//            image(imageFrame.bitmap)


//            imageAnimationView(imageData.defaultAnimation) {
//                smoothing = false
//                scale = 4.0
//                centerOnStage()
//            }

            val sprite = UIEightDirectionalSprite(asp).addTo(this) {
                scale = 4.0
                anchor(Anchor.CENTER)
//                centerOnXY(0.0, 0.0)
//                xy(0, 0)
            }

        }
    }
}

private fun View.centerOnXY(x: Double, y: Double) {
    this.x = x - scaledWidth / 2.0
    this.y = y - scaledHeight / 2.0
}
