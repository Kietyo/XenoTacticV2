import com.xenotactic.gamelogic.views.UIEightDirectionalSprite
import com.xenotactic.korge.utils.getDirection8
import com.xenotactic.korge.utils.kAngleTo
import korlibs.image.color.Colors
import korlibs.image.format.ASE
import korlibs.image.format.readImageDataContainer
import korlibs.image.format.toProps
import korlibs.io.async.runBlockingNoJs
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.Korge
import korlibs.korge.KorgeConfig
import korlibs.korge.ui.uiButton
import korlibs.korge.view.*
import korlibs.korge.view.align.centerOnStage
import korlibs.math.geom.*
import korlibs.time.Frequency
import kotlin.jvm.JvmStatic

operator fun ClosedRange<Angle>.contains(angle: Angle): Boolean =
    angle.inBetween(this.start, this.endInclusive, inclusive = true)

object DebugMain2 {

    @JvmStatic
    fun main(args: Array<String>) = runBlockingNoJs {
        Korge(
            KorgeConfig(
                backgroundColor = Colors.LIGHTGRAY,
                virtualSize = Size(1280, 720)
            )
        ) {
            //            text("Hello world")

            val midCircle = circle(radius = 20f) {
                anchor(Anchor.CENTER)
                centerOnStage()
            }

            val mouseCircle = circle(radius = 20f, fill = Colors.RED) {
                anchor(Anchor.CENTER)
            }

            val info = text("", textSize = 50f)

            val asp = resourcesVfs["8_directional_character.aseprite"].readImageDataContainer(ASE.toProps())

            val sprite = UIEightDirectionalSprite(asp).addTo(this) {
                scale = Scale(2f)
                anchor(Anchor.CENTER)
                xy(640, 360)
                //                centerOnXY(0.0, 0.0)
                //                xy(0, 0)
            }

            uiButton()

            //            val rect = solidRect(16, 16, Colors.RED) {
            //                scale = 8.0
            //                centerOnStage()
            //                anchor(Anchor.CENTER)
            //            }

            addUpdater {
                val (mouseX, mouseY) = views.globalMousePos
                mouseCircle.xy(mouseX, mouseY)
                val angle = midCircle.pos.kAngleTo(mouseCircle.pos)
                val direction = getDirection8(angle)
                sprite.changeToDirection(direction)
                info.text = """
                    mouseX: $mouseX, mouseY: $mouseY
                    angle: ${angle}
                    direction: ${direction}
                """.trimIndent()
            }

            addFixedUpdater(Frequency(2.0)) {
                sprite.incrementFrame()
            }

            val angleRange1 = Angle.fromDegrees(30.0) until Angle.fromDegrees(-30.0)

            println(angleRange1.contains(Angle.ZERO))
            println(angleRange1.contains(Angle.fromDegrees(15.0)))
            println(angleRange1.contains(Angle.fromDegrees(345.0)))
            println(angleRange1.contains(Angle.fromDegrees(90.0)))
            println()

            val angleRange2 = Angle.fromDegrees(-30.0) until Angle.fromDegrees(30.0)
            println(angleRange2.contains(Angle.ZERO))
            println(angleRange2.contains(Angle.fromDegrees(15.0)))
            println(angleRange2.contains(Angle.fromDegrees(345.0)))
            println(angleRange2.contains(Angle.fromDegrees(90.0)))
            println()

            val angleRange3 = Angle.fromDegrees(330.0) until Angle.fromDegrees(30.0)
            println(angleRange3.contains(Angle.ZERO))
            println(angleRange3.contains(Angle.fromDegrees(15.0)))
            println(angleRange3.contains(Angle.fromDegrees(345.0)))
            println(angleRange3.contains(Angle.fromDegrees(90.0)))
            println(angleRange3.contains(Angle.fromDegrees(-15.0)))
            println()

            val angleRange4 = Angle.fromDegrees(330.0)..Angle.fromDegrees(30.0)
            println(angleRange4.contains(Angle.ZERO))
            println(angleRange4.contains(Angle.fromDegrees(15.0)))
            println(angleRange4.contains(Angle.fromDegrees(345.0)))
            println(angleRange4.contains(Angle.fromDegrees(90.0)))
            println(angleRange4.contains(Angle.fromDegrees(-15.0)))
            println(angleRange4.contains(Angle.fromDegrees(330.0)))
            println(angleRange4.contains(Angle.fromDegrees(30.0)))
            println()
        }
    }
}