package com.xenotactic.gamelogic.model

import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.until

interface IRectangleEntity {
    val x: GameUnit
    val y: GameUnit
    val width: GameUnit
    val height: GameUnit

    val left get() = x
    val right get() = x + width
    val bottom get() = y
    val top get() = y + height

    val blockGameUnitPoints: Set<GameUnitTuple>
        get() {
            val gameUnitPoints = mutableSetOf<GameUnitTuple>()
            for (i in 0 until width) {
                for (j in 0 until height) {
                    gameUnitPoints.add(GameUnitTuple(x + i, y + j))
                }
            }
            return gameUnitPoints.toSet()
        }

    val topLeftUnitSquareGameUnitPoint: GameUnitTuple
        get() = GameUnitTuple(x, y + height - 1)

    val topRightUnitSquareGameUnitPoint: GameUnitTuple
        get() = GameUnitTuple(x + width - 1, y + height - 1)

    val bottomLeftUnitSquareGameUnitPoint: GameUnitTuple
        get() = GameUnitTuple(x, y)

    val bottomRightUnitSquareGameUnitPoint: GameUnitTuple
        get() = GameUnitTuple(x + width - 1, y)

    val centerPoint: IPoint
        get() = IPoint(x.value + width.value / 2f, y.value + height.value / 2f)

    fun getRectangle(): IRectangle {
        return IRectangle(x.value, y.value, width.value, height.value)
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