package com.xenotactic.gamelogic.utils

import korlibs.image.color.MaterialColors
import korlibs.image.color.RGBA
import korlibs.math.roundDecimalPlaces
import com.xenotactic.gamelogic.model.MapEntity
import korlibs.memory.clamp
import kotlin.math.floor

object SpeedAreaColorUtil {
    val FAST_AREA_COLORS = listOf(
        MaterialColors.RED_400,
        MaterialColors.RED_500,
        MaterialColors.RED_600,
        MaterialColors.RED_700,
        MaterialColors.RED_800,
    )

    val SLOW_AREA_COLORS = listOf(
        MaterialColors.BLUE_400,
        MaterialColors.BLUE_500,
        MaterialColors.BLUE_600,
        MaterialColors.BLUE_700,
        MaterialColors.BLUE_800,
    )

    operator fun invoke(
        speedArea: MapEntity.SpeedArea,
        slowAreaColors: List<RGBA> = SLOW_AREA_COLORS,
        fastAreaColors: List<RGBA> = FAST_AREA_COLORS,
        slowLow: Double = 0.0,
        slowHigh: Double = 1.0,
        fastLow: Double = 1.0,
        fastHigh: Double = 2.0
    ): RGBA {
        return invoke(
            speedArea.speedEffect,
            slowAreaColors,
            fastAreaColors,
            slowLow,
            slowHigh,
            fastLow,
            fastHigh
        )
    }

    operator fun invoke(
        speedEffect: Double,
        slowAreaColors: List<RGBA> = SLOW_AREA_COLORS,
        fastAreaColors: List<RGBA> = FAST_AREA_COLORS,
        slowLow: Double = 0.0,
        slowHigh: Double = 1.0,
        fastLow: Double = 1.0,
        fastHigh: Double = 2.0
    ): RGBA {
        return if (speedEffect <= 1.0) {
            getSlowSpeedAreaColor(speedEffect, slowAreaColors, slowLow, slowHigh)
        } else getFastSpeedAreaColor(speedEffect, fastAreaColors, fastLow, fastHigh)
    }

    /**
     * Returns the color of the slow speed area (a speed area where the `speedEffect` <= 1.0).
     *
     * The slow area colors are provided via the `slowAreaColors` parameter. The colors should be
     * ordered by intensity of the slow.
     *
     * The `low` and `high` basically define the range in which the slow area colors should be
     * partitioned.
     *
     * For example, if we have:
     * - `low` = 0.2
     * - `high` = 0.6
     * - `slowAreaColors` = [COLOR_1, COLOR_2, COLOR_3, COLOR_4]
     *
     * Then the colors will be partitioned like so:
     *      0.1     0.2     0.3     0.4     0.5     0.6     0.7
     *       |-------|-------|-------|-------|-------|-------|
     *                COLOR_4 COLOR_3 COLOR_2 COLOR_1
     *
     *  So `speedEffect` (x) where:
     *  - x <= 0.2 -> COLOR_4
     *  - x in (exclusive) (0.2, 0.3] (inclusive) -> COLOR_4 (highest intensity color)
     *  - x in (exclusive) (0.3, 0.4] (inclusive) -> COLOR_3
     *  - x in (exclusive) (0.4, 0.5] (inclusive) -> COLOR_2
     *  - x in (exclusive) (0.5, 0.6] (inclusive) -> COLOR_1 (lowest intensity color)
     *  - x >= 0.6 -> COLOR_1
     *
     *  Notice that if the `speedEffect` is out of the range, then it is clamped to the nearest
     *  boundary.
     */
    fun getSlowSpeedAreaColor(
        speedEffect: Double, slowAreaColors: List<RGBA>,
        low: Double = 0.0, high: Double = 1.0
    ): RGBA {
        val colorIncrements = ((high - low) / slowAreaColors.size).roundDecimalPlaces(2)
        val inversedSlow = (high - speedEffect.clamp(low, high)).roundDecimalPlaces(2)
        val colorIndex = floor((inversedSlow / colorIncrements).roundDecimalPlaces(2)).toInt()
            .clamp(0, slowAreaColors.size - 1)
        return slowAreaColors[colorIndex]
    }

    /**
     * Returns the color of the fast speed area (a speed area where the `speedEffect` > 1.0).
     *
     * The fast area colors are provided via the `fastAreaColors` parameter. The colors should be
     * ordered by intensity of the speed.
     *
     * The `low` and `high` basically define the range in which the fast area colors should be
     * partitioned.
     *
     * For example, if we have:
     * - `low` = 1.2
     * - `high` = 1.6
     * - `fastAreaColors` = [COLOR_1, COLOR_2, COLOR_3, COLOR_4]
     *
     * Then the colors will be partitioned like so:
     *      1.1     1.2     1.3     1.4     1.5     1.6     1.7
     *       |-------|-------|-------|-------|-------|-------|
     *                COLOR_1 COLOR_2 COLOR_3 COLOR_4
     *
     *  So `speedEffect` (x) where:
     *  - x < 1.2 -> COLOR_1
     *  - x in (inclusive) [1.2, 1.3) (exclusive) -> COLOR_1 (lowest intensity color)
     *  - x in (inclusive) [1.3, 1.4) (exclusive) -> COLOR_2
     *  - x in (inclusive) [1.4, 1.5) (exclusive) -> COLOR_3
     *  - x in (inclusive) [1.5, 1.6) (exclusive) -> COLOR_4 (highest intensity color)
     *  - X >= 1.6 -> COLOR_4
     *
     *  Notice that if the `speedEffect` is out of the range, then it is clamped to the nearest
     *  boundary.
     */
    fun getFastSpeedAreaColor(
        speedEffect: Double, fastAreaColors: List<RGBA>,
        low: Double = 1.0, high: Double = 2.0
    ): RGBA {
        val colorIncrements = (high - low) / fastAreaColors.size
        val normalizedSpeedEffect = speedEffect - low
        val colorIndex = floor(normalizedSpeedEffect / colorIncrements).toInt()
            .clamp(0, fastAreaColors.size - 1)
        return fastAreaColors[colorIndex]
    }
}