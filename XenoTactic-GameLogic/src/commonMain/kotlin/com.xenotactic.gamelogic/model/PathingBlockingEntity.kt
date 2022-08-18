package com.xenotactic.gamelogic.model

import com.soywiz.korma.geom.Rectangle
import kotlinx.serialization.Serializable

interface PathingBlockingEntity{
    val x: Int
    val y: Int
    val width: Int
    val height: Int

    val blockIntPoints: Set<IntPoint> get() {
        val intPoints = mutableSetOf<IntPoint>()
        for (i in 0 until width) {
            for (j in 0 until height) {
                intPoints.add(IntPoint(x + i, y + j))
            }
        }
        return intPoints.toSet()
    }

    val topLeftUnitSquareIntPoint: IntPoint
        get() = IntPoint(x, y + height - 1)

    val topRightUnitSquareIntPoint: IntPoint
        get() = IntPoint(x + width - 1, y + height - 1)

    val bottomLeftUnitSquareIntPoint: IntPoint
        get() = IntPoint(x, y)

    val bottomRightUnitSquareIntPoint: IntPoint
        get() = IntPoint(x + width - 1, y)

    fun getRectangle(): Rectangle {
        return Rectangle(x, y, width, height)
    }
}