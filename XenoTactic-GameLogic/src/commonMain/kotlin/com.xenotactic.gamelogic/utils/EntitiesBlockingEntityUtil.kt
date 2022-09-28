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
            blockingPoints.contains(it, entity.topY.value)
        }
        val bottomPartiallyBlocking = (entity.x until entity.rightX).any {
            blockingPoints.contains(it, entity.y.value - 1)
        }
        val leftPartiallyBlocking = (entity.y until entity.topY).any {
            blockingPoints.contains(entity.x.value - 1, it)
        }
        val rightPartiallyBlocking = (entity.y until entity.topY).any {
            blockingPoints.contains(entity.rightX.value, it)
        }


        return EntitiesBlockingEntityResult(
            blockingPoints.contains(entity.x.value - 1, entity.topY.value),
            blockingPoints.contains(entity.rightX.value, entity.topY.value),
            blockingPoints.contains(entity.x.value - 1, entity.y.value - 1),
            blockingPoints.contains(entity.rightX.value, entity.y.value - 1),
            topPartiallyBlocking,
            bottomPartiallyBlocking,
            leftPartiallyBlocking,
            rightPartiallyBlocking
        )
    }
}