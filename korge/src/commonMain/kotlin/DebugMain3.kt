import com.soywiz.kds.setExtra
import com.soywiz.klock.Frequency
import com.soywiz.korge.Korge
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korge.view.animation.imageDataView
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.ASE
import com.soywiz.korim.format.readImageDataContainer
import com.soywiz.korim.format.toProps
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.*
import com.xenotactic.gamelogic.views.EightDirection
import com.xenotactic.gamelogic.views.UIEightDirectionalSprite
import com.xenotactic.korge.korge_utils.getDirection8
import com.xenotactic.korge.korge_utils.kAngleTo
import kotlin.jvm.JvmStatic


object DebugMain3 {

    @JvmStatic
    fun main(args: Array<String>) = runBlockingNoJs {
        Korge(width = 640, height = 480, bgcolor = Colors.LIGHTGRAY) {
//            text("Hello world")

            val midCircle = circle(radius = 20.0) {
                anchor(Anchor.CENTER)
                centerOnStage()
            }

            val mouseCircle = circle(radius = 20.0, fill = Colors.RED) {
                anchor(Anchor.CENTER)
            }

            val info = text("", textSize = 50.0)

            val props = ASE.toProps()
            props.setExtra("disableSlicing", false)
            props.setExtra("useSlicePosition", true)

            val asp = resourcesVfs["tower_sprites.aseprite"].readImageDataContainer(props)

            println(asp)
            println(asp.imageDatas)
            println(asp.imageDatasByName)

            println(asp.imageDatas.first())
            println(asp.imageDatas.first().frames.first())
            println(asp.imageDatas.first().frames.first().layerData.map { it.layer.name })
            val turretLayer = asp.imageDatas.first().frames.first().layerData.last()
            println(turretLayer)
            println(turretLayer.width)
            println(turretLayer.height)

            image(asp.imageDatas.first().frames.first().layerData.last().bitmap) {
                smoothing = false
                scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(300.0, 300.0))
            }




            addUpdater {
                val (mouseX, mouseY) = views.globalMouseXY
                mouseCircle.xy(mouseX, mouseY)
                val angle = midCircle.pos.kAngleTo(mouseCircle.pos)
                val direction = getDirection8(angle)
                info.text = """
                    mouseX: $mouseX, mouseY: $mouseY
                    angle: ${angle}
                    direction: ${direction}
                """.trimIndent()
            }
        }
    }
}