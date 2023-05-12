package com.xenotactic.gamelogic.utils

import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity

data class EntitiesBlockingEntityResult(
    val topLeftDiagonalBlocking: Boolean = false,
    val topRightDiagonalBlocking: Boolean = false,
    val bottomLeftDiagonalBlocking: Boolean = false,
    val bottomRightDiagonalBlocking: Boolean = false,
    val topPartiallyBlocking: Boolean = false,
    val bottomPartiallyBlocking: Boolean = false,
    val leftPartiallyBlocking: Boolean = false,
    val rightPartiallyBlocking: Boolean = false
) {
    val anyPartiallyBlocking: Boolean
        get() {
            return topLeftDiagonalBlocking || topRightDiagonalBlocking || bottomLeftDiagonalBlocking
                    || bottomRightDiagonalBlocking || topPartiallyBlocking || bottomPartiallyBlocking
                    || leftPartiallyBlocking || rightPartiallyBlocking
        }
}

object EntitiesBlockingEntityUtil {
    operator fun invoke(entity: MapEntity, gameMap: GameMap): EntitiesBlockingEntityResult {
        val blockingPoints = gameMap.blockingPointsView()

        val topPartiallyBlocking = (entity.x until entity.rightX).any {
            blockingPoints.contains(it, entity.topY)
        }
        val bottomPartiallyBlocking = (entity.x until entity.rightX).any {
            blockingPoints.contains(it, entity.y - 1)
        }
        val leftPartiallyBlocking = (entity.y until entity.topY).any {
            blockingPoints.contains(entity.x - 1, it)
        }
        val rightPartiallyBlocking = (entity.y until entity.topY).any {
            blockingPoints.contains(entity.rightX, it)
        }


        return EntitiesBlockingEntityResult(
            blockingPoints.contains(entity.x.toInt() - 1, entity.topY.toInt()),
            blockingPoints.contains(entity.rightX.toInt(), entity.topY.toInt()),
            blockingPoints.contains(entity.x.toInt() - 1, entity.y.toInt() - 1),
            blockingPoints.contains(entity.rightX.toInt(), entity.y.toInt() - 1),
            topPartiallyBlocking,
            bottomPartiallyBlocking,
            leftPartiallyBlocking,
            rightPartiallyBlocking
        )
    }
}