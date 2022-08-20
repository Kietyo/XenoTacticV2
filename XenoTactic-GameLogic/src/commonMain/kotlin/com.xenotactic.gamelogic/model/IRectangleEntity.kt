package com.xenotactic.gamelogic.model

import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.Rectangle

interface IRectangleEntity {
    val x: Int
    val y: Int
    val width: Int
    val height: Int

    val blockIntPoints: Set<IntPoint>
        get() {
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

    val centerPoint: Point
        get() = Point(x + width / 2f, y + height / 2f)

    fun getRectangle(): Rectangle {
        return Rectangle(x, y, width, height)
    }

    fun isFullyCoveredBy(
        vararg entities: IRectangleEntity
    ): Boolean {
        return isFullyCoveredBy(entities.asIterable())
    }

    fun isFullyCoveredBy(
        entities: Iterable<IRectangleEntity>
    ): Boolean {
        if (entities.count() == 1) {
            return blockIntPoints.intersect(entities.first().blockIntPoints).size == blockIntPoints.size
        }
        val visibleBlocks = this.blockIntPoints.toMutableSet()
        for (mapEntity in entities) {
            val intersect = visibleBlocks.intersect(mapEntity.blockIntPoints)
            visibleBlocks.removeAll(intersect)
            if (visibleBlocks.isEmpty()) return true
        }
        return false
    }
}

data class RectangleEntity(
    override val x: Int,
    override val y: Int,
    override val width: Int,
    override val height: Int
) : IRectangleEntity