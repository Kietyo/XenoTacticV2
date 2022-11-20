package com.xenotactic.gamelogic.model

import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.Rectangle
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.until

interface IRectangleEntity {
    val x: GameUnit
    val y: GameUnit
    val width: GameUnit
    val height: GameUnit

    val blockGameUnitPoints: Set<GameUnitPoint>
        get() {
            val gameUnitPoints = mutableSetOf<GameUnitPoint>()
            for (i in 0 until  width) {
                for (j in 0 until height) {
                    gameUnitPoints.add(GameUnitPoint(x + i, y + j))
                }
            }
            return gameUnitPoints.toSet()
        }

    val topLeftUnitSquareGameUnitPoint: GameUnitPoint
        get() = GameUnitPoint(x, y + height - 1)

    val topRightUnitSquareGameUnitPoint: GameUnitPoint
        get() = GameUnitPoint(x + width - 1, y + height - 1)

    val bottomLeftUnitSquareGameUnitPoint: GameUnitPoint
        get() = GameUnitPoint(x, y)

    val bottomRightUnitSquareGameUnitPoint: GameUnitPoint
        get() = GameUnitPoint(x + width - 1, y)

    val centerPoint: Point
        get() = Point(x.value + width.value / 2f, y.value + height.value / 2f)

    fun getRectangle(): Rectangle {
        return Rectangle(x.value, y.value, width.value, height.value)
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
            return blockGameUnitPoints.intersect(entities.first().blockGameUnitPoints).size == blockGameUnitPoints.size
        }
        val visibleBlocks = this.blockGameUnitPoints.toMutableSet()
        for (mapEntity in entities) {
            val intersect = visibleBlocks.intersect(mapEntity.blockGameUnitPoints)
            visibleBlocks.removeAll(intersect)
            if (visibleBlocks.isEmpty()) return true
        }
        return false
    }

//    fun isFullyCoveredBy(
//        entity: IRectangleEntity
//    ): Boolean {
//        return
//    }
}

data class RectangleEntity(
    override val x: GameUnit,
    override val y: GameUnit,
    override val width: GameUnit,
    override val height: GameUnit
) : IRectangleEntity