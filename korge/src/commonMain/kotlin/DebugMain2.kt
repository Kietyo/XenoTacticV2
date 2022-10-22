import com.soywiz.klock.Frequency
import com.soywiz.korge.Korge
import com.soywiz.korge.view.*
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






operator fun ClosedRange<Angle>.contains(angle: Angle): Boolean = angle.inBetween(this.start, this.endInclusive, inclusive = true)

object DebugMain2 {

    @JvmStatic
    fun main(args: Array<String>) = runBlockingNoJs {
        Korge(width = 1280, height = 720, bgcolor = Colors.LIGHTGRAY) {
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

//            val rect = solidRect(16, 16, Colors.RED) {
//                scale = 8.0
//                centerOnStage()
//                anchor(Anchor.CENTER)
//            }


            addUpdater {
                val (mouseX, mouseY) = views.globalMouseXY
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