import com.soywiz.korge.Korge
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.ImageDataContainer
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korma.geom.*
import kotlin.jvm.JvmStatic

// Returns the angle from one point to another, with respect to
// bottom left coordinate system rather than top left coordinate system.
fun IPoint.kAngleTo(other: IPoint): Angle {
    val yDiff = other.y - this.y
    return Angle.between(this.x, this.y, other.x, this.y - yDiff)
}

enum class EightDirection {
    UP_LEFT,
    UP,
    UP_RIGHT,
    LEFT,
    RIGHT,
    DOWN_LEFT,
    DOWN,
    DOWN_RIGHT
}

data class DirectionMatcher(
    val closedRange: ClosedRange<Angle>,
    val direction: EightDirection
)

val DIRECTION_MATCHERS = listOf(
    DirectionMatcher(330.degrees..(30.degrees), EightDirection.RIGHT),
    DirectionMatcher(60.degrees..(120.degrees), EightDirection.UP),
    DirectionMatcher(150.degrees..(210.degrees), EightDirection.LEFT),
    DirectionMatcher(240.degrees..(300.degrees), EightDirection.DOWN),
    DirectionMatcher(30.degrees..(60.degrees), EightDirection.UP_RIGHT),
    DirectionMatcher(120.degrees..(150.degrees), EightDirection.UP_LEFT),
    DirectionMatcher(210.degrees..(240.degrees), EightDirection.DOWN_LEFT),
    DirectionMatcher(300.degrees..(330.degrees), EightDirection.DOWN_RIGHT),
)

fun getDirection8(angle: Angle): EightDirection {
    val matcher = DIRECTION_MATCHERS.first {
        angle.inBetween(it.closedRange)
    }
    return matcher.direction
}

operator fun ClosedRange<Angle>.contains(angle: Angle): Boolean = angle.inBetween(this.start, this.endInclusive, inclusive = true)

object DebugMain2 {

    @JvmStatic
    fun main(args: Array<String>) = runBlockingNoJs {
        Korge(width = 1280, height = 720) {
//            text("Hello world")

            val midCircle = circle(radius = 20.0) {
                anchor(Anchor.CENTER)
                centerOnStage()
            }

            val mouseCircle = circle(radius = 20.0, fill = Colors.RED) {
                anchor(Anchor.CENTER)
            }

            val info = text("", textSize = 100.0)


            addUpdater {
                val (mouseX, mouseY) = views.globalMouseXY
                mouseCircle.xy(mouseX, mouseY)
                val angle = midCircle.pos.kAngleTo(mouseCircle.pos)
                val direction = getDirection8(angle)
                info.text = """
                    angle: ${angle}
                    direction: ${direction}
                """.trimIndent()
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