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

    val right get() = x + width
    val top get() = y + height

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
        entities: Iterable<IRectangleEntity>
    ): Boolean {
        return entities.any {
            isFullyCoveredBy(it)
        }
    }

    fun isFullyCoveredBy(
        other: IRectangleEntity
    ): Boolean {
        return com.xenotactic.gamelogic.utils.isFullyCoveredBy(
            this.x.toDouble(),
            this.y.toDouble(),
            this.width.toDouble(),
            this.height.toDouble(),
            other.x.toDouble(),
            other.y.toDouble(),
            other.width.toDouble(),
            other.height.toDouble(),
        )
    }
}

data class RectangleEntity(
    override val x: GameUnit,
    override val y: GameUnit,
    override val width: GameUnit,
    override val height: GameUnit
) : IRectangleEntity