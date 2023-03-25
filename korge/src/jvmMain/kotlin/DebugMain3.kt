import com.soywiz.kds.setExtra
import com.soywiz.korge.Korge
import com.soywiz.korge.KorgeConfig
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.bitmap.Bitmap32Context2d
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.ASE
import com.soywiz.korim.format.readImageDataContainer
import com.soywiz.korim.format.toProps
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.*
import com.xenotactic.gamelogic.utils.toAsepriteModel
import com.xenotactic.korge.korge_utils.getDirection8
import com.xenotactic.korge.korge_utils.kAngleTo
import kotlin.jvm.JvmStatic

fun coolPrint(vararg objs: Any) {
    val test = Throwable()
    println(test)
}

object DebugMain3 {

    @JvmStatic
    fun main(args: Array<String>) = runBlockingNoJs {
            Korge(
                KorgeConfig(
                    bgcolor = Colors.LIGHTGRAY,
                    virtualSize = SizeInt(640, 480)
                )
            ) {
//            text("Hello world")

            val midCircle = circle(radius = 20.0) {
                anchor(Anchor.CENTER)
                centerOnStage()
            }

            val mouseCircle = circle(radius = 20.0, fill = Colors.RED) {
                anchor(Anchor.CENTER)
            }

            val info = text("", textSize = 25.0)

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
            println(turretLayer.targetX)
            println(turretLayer.targetY)

            val aseModel = asp.toAsepriteModel()

            val gunFrames = aseModel.getAsepriteLayerWithAllFrames("gun3")
            val gunBmp = gunFrames.frames.first()

            val uncroppedBitmap = gunBmp.computeUncroppedBitmap()

            val gun = image(uncroppedBitmap) {
                smoothing = false
                scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(100.0, 100.0))
                xy(320, 240)
                anchor(Anchor.CENTER)
            }


            val a = 111
            val b = "test string"
            val c = 0.15

            coolPrint(a, b, c)

// Want output: "a=111,b=test string,c=0.15,b.length=11"

            val angleOffset = Angle.fromDegrees(90)

            addUpdater {
                val (mouseX, mouseY) = views.globalMousePos
                mouseCircle.xy(mouseX, mouseY)


                val bottomLeftAngle = midCircle.pos.kAngleTo(mouseCircle.pos)
                val direction = getDirection8(bottomLeftAngle)
                val topLeftAngle = midCircle.pos.angleTo(mouseCircle.pos)
                gun.rotation(topLeftAngle)
                info.text = """
                    mouseX: $mouseX, mouseY: $mouseY
                    bottomLeftAngle: ${bottomLeftAngle}
                    topLeftAngle: $topLeftAngle
                    direction: ${direction}
                """.trimIndent()
            }
        }
    }
}