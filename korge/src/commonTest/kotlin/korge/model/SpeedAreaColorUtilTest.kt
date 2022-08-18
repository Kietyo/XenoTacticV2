package korge.model

import com.soywiz.korim.color.MaterialColors
import com.xenotactic.gamelogic.korge_utils.SpeedAreaColorUtil
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SpeedAreaColorUtilTest {

    @Test
    fun getSlowSpeedAreaColor_defaultLowAndHigh() {
        val slowColors = listOf(
            MaterialColors.BLUE_100,
            MaterialColors.BLUE_200,
            MaterialColors.BLUE_300,
            MaterialColors.BLUE_400,
        )

        assertEquals(
            MaterialColors.BLUE_100,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(1.1, slowColors)
        )
        assertEquals(
            MaterialColors.BLUE_100,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(1.0, slowColors)
        )
        assertEquals(
            MaterialColors.BLUE_100,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.8, slowColors)
        )
        assertEquals(
            MaterialColors.BLUE_200,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.75, slowColors)
        )
        assertEquals(
            MaterialColors.BLUE_200,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.7, slowColors)
        )
        assertEquals(
            MaterialColors.BLUE_300,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.5, slowColors)
        )
        assertEquals(
            MaterialColors.BLUE_300,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.45, slowColors)
        )
        assertEquals(
            MaterialColors.BLUE_400,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.25, slowColors)
        )
        assertEquals(
            MaterialColors.BLUE_400,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.123, slowColors)
        )
        assertEquals(
            MaterialColors.BLUE_400,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.0, slowColors)
        )
        assertEquals(
            MaterialColors.BLUE_400,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(-0.1, slowColors)
        )
    }

    @Test
    fun getSlowSpeedAreaColor() {
        val slowColors = listOf(
            MaterialColors.BLUE_100,
            MaterialColors.BLUE_200,
            MaterialColors.BLUE_300,
            MaterialColors.BLUE_400,
        )

        assertEquals(
            MaterialColors.BLUE_100,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(1.0, slowColors, low = 0.2, high = 0.6)
        )
        assertEquals(
            MaterialColors.BLUE_100,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.6, slowColors, low = 0.2, high = 0.6)
        )
        assertEquals(
            MaterialColors.BLUE_100,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.55, slowColors, low = 0.2, high = 0.6)
        )
        assertEquals(
            MaterialColors.BLUE_200,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.5, slowColors, low = 0.2, high = 0.6)
        )
        assertEquals(
            MaterialColors.BLUE_200,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.45, slowColors, low = 0.2, high = 0.6)
        )
        assertEquals(
            MaterialColors.BLUE_300,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.4, slowColors, low = 0.2, high = 0.6)
        )
        assertEquals(
            MaterialColors.BLUE_300,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.35, slowColors, low = 0.2, high = 0.6)
        )
        assertEquals(
            MaterialColors.BLUE_400,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.3, slowColors, low = 0.2, high = 0.6)
        )
        assertEquals(
            MaterialColors.BLUE_400,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.25, slowColors, low = 0.2, high = 0.6)
        )
        assertEquals(
            MaterialColors.BLUE_400,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.2, slowColors, low = 0.2, high = 0.6)
        )
        assertEquals(
            MaterialColors.BLUE_400,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.15, slowColors, low = 0.2, high = 0.6)
        )
        assertEquals(
            MaterialColors.BLUE_400,
            SpeedAreaColorUtil.getSlowSpeedAreaColor(0.0, slowColors, low = 0.2, high = 0.6)
        )
    }

    @Test
    fun getFastSpeedAreaColor_defaultLowAndHigh() {
        val fastColors = listOf(
            MaterialColors.RED_100,
            MaterialColors.RED_200,
            MaterialColors.RED_300,
            MaterialColors.RED_400,
        )

        assertEquals(
            MaterialColors.RED_100,
            SpeedAreaColorUtil.getFastSpeedAreaColor(
                0.9, fastColors,
                low = 1.0, high = 2.0
            )
        )
        assertEquals(
            MaterialColors.RED_100,
            SpeedAreaColorUtil.getFastSpeedAreaColor(
                1.0, fastColors,
                low = 1.0, high = 2.0
            )
        )
        assertEquals(
            MaterialColors.RED_100,
            SpeedAreaColorUtil.getFastSpeedAreaColor(
                1.1, fastColors,
                low = 1.0, high = 2.0
            )
        )
        assertEquals(
            MaterialColors.RED_100,
            SpeedAreaColorUtil.getFastSpeedAreaColor(
                1.2, fastColors,
                low = 1.0, high = 2.0
            )
        )
        assertEquals(
            MaterialColors.RED_200,
            SpeedAreaColorUtil.getFastSpeedAreaColor(
                1.25, fastColors,
                low = 1.0, high = 2.0
            )
        )
        assertEquals(
            MaterialColors.RED_200,
            SpeedAreaColorUtil.getFastSpeedAreaColor(
                1.3, fastColors,
                low = 1.0, high = 2.0
            )
        )
        assertEquals(
            MaterialColors.RED_200,
            SpeedAreaColorUtil.getFastSpeedAreaColor(
                1.35, fastColors,
                low = 1.0, high = 2.0
            )
        )
        assertEquals(
            MaterialColors.RED_300,
            SpeedAreaColorUtil.getFastSpeedAreaColor(
                1.5, fastColors,
                low = 1.0, high = 2.0
            )
        )
        assertEquals(
            MaterialColors.RED_300,
            SpeedAreaColorUtil.getFastSpeedAreaColor(
                1.6, fastColors,
                low = 1.0, high = 2.0
            )
        )
        assertEquals(
            MaterialColors.RED_300,
            SpeedAreaColorUtil.getFastSpeedAreaColor(
                1.7, fastColors,
                low = 1.0, high = 2.0
            )
        )
        assertEquals(
            MaterialColors.RED_400,
            SpeedAreaColorUtil.getFastSpeedAreaColor(
                1.75, fastColors,
                low = 1.0, high = 2.0
            )
        )
        assertEquals(
            MaterialColors.RED_400,
            SpeedAreaColorUtil.getFastSpeedAreaColor(
                1.8, fastColors,
                low = 1.0, high = 2.0
            )
        )
        assertEquals(
            MaterialColors.RED_400,
            SpeedAreaColorUtil.getFastSpeedAreaColor(
                1.85, fastColors,
                low = 1.0, high = 2.0
            )
        )
        assertEquals(
            MaterialColors.RED_400,
            SpeedAreaColorUtil.getFastSpeedAreaColor(
                2.0, fastColors,
                low = 1.0, high = 2.0
            )
        )
    }
}