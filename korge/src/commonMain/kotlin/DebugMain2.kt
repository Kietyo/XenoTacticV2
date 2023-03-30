import com.soywiz.klock.Frequency
import com.soywiz.korge.Korge
import com.soywiz.korge.KorgeConfig
import com.soywiz.korge.ui.uiButton
import korlibs.korge.view.*
import korlibs.image.color.Colors
import korlibs.image.format.ASE
import korlibs.image.format.readImageDataContainer
import korlibs.image.format.toProps
import korlibs.io.async.runBlockingNoJs
import korlibs.io.file.std.resourcesVfs
import korlibs.math.geom.*
import com.xenotactic.gamelogic.views.EightDirection
import com.xenotactic.gamelogic.views.UIEightDirectionalSprite
import com.xenotactic.korge.korge_utils.getDirection8
import com.xenotactic.korge.korge_utils.kAngleTo
import kotlin.jvm.JvmStatic

operator fun ClosedRange<Angle>.contains(angle: Angle): Boolean =
    angle.inBetween(this.start, this.endInclusive, inclusive = true)

object DebugMain2 {

    @JvmStatic
    fun main(args: Array<String>) = runBlockingNoJs {
        Korge(
            KorgeConfig(
                bgcolor = Colors.LIGHTGRAY,
                virtualSize = SizeInt(1280, 720)
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

            val info = text("", textSize = 50.0)

            val asp = resourcesVfs["8_directional_character.aseprite"].readImageDataContainer(ASE.toProps())

            val sprite = UIEightDirectionalSprite(asp).addTo(this) {
                scale = 8.0
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