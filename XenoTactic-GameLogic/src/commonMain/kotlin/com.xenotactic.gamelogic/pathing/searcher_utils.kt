package com.xenotactic.gamelogic.pathing


import korlibs.math.geom.cos
import korlibs.math.geom.sin
import com.xenotactic.gamelogic.containers.BlockingPointContainer
import com.xenotactic.gamelogic.utils.PATHING_POINT_PRECISION
import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.model.IRectangleEntity
import com.xenotactic.gamelogic.utils.MapBlockingUtil
import com.xenotactic.gamelogic.utils.angleRadians
import com.xenotactic.gamelogic.utils.horizontalDirectionTo
import com.xenotactic.gamelogic.utils.verticalDirectionTo
import kotlin.math.sqrt

data class CornerPathingPointConfiguration(
    val calculateDiagonal: Boolean = false,
    val calculateP1: Boolean = false,
    val calculateP2: Boolean = false,
) {
    companion object {
        val FULLY_ENABLED = CornerPathingPointConfiguration(true, true, true)
        val DISABLED = CornerPathingPointConfiguration(false, false, false)
        val ONLY_DIAGONAL = CornerPathingPointConfiguration(true, false, false)
    }

    val atLeastOneEnabled: Boolean
        get() = calculateDiagonal || calculateP1 || calculateP2
}

data class PathingPointsForUnitSquareConfiguration(
    val x: Int,
    val y: Int,
    val mapWidth: Int,
    val mapHeight: Int,
    val pathingPointPrecision: Double = PATHING_POINT_PRECISION,
    val calculateTopLeft: CornerPathingPointConfiguration = CornerPathingPointConfiguration.FULLY_ENABLED,
    val calculateTopRight: CornerPathingPointConfiguration = CornerPathingPointConfiguration.FULLY_ENABLED,
    val calculateBottomRight: CornerPathingPointConfiguration = CornerPathingPointConfiguration.FULLY_ENABLED,
    val calculateBottomLeft: CornerPathingPointConfiguration = CornerPathingPointConfiguration.FULLY_ENABLED
) {
    val topY: Int
        get() = y + 1
    val rightX: Int
        get() = x + 1
}

enum class VerticalDirection {
    UP,
    NONE,
    DOWN
}

enum class HorizontalDirection {
    LEFT,
    NONE,
    RIGHT
}

/**
 * Represents a pathing point for a certain block entity.
 *
 * The `entityVerticalDirection` and `entityHorizontalDirection` describes the direction
 * of the block entity with respect to the pathing point `v`.
 *
 * For example, if the pathing point was at the top left (diagonal), then
 * `entityVerticalDirection` would be RIGHT and `entityHorizontalDirection` would be DOWN
 */
data class PathingPoint(
    val v: IPoint,
    val entityVerticalDirection: VerticalDirection,
    val entityHorizontalDirection: HorizontalDirection
) {
    companion object {
        fun create(
            x: Double, y: Double,
            entityVerticalDirection: VerticalDirection,
            entityHorizontalDirection: HorizontalDirection
        ): PathingPoint {
            return PathingPoint(IPoint(x, y), entityVerticalDirection, entityHorizontalDirection)
        }
    }
}

fun getPathingPointsForUnitSquare(
    config: PathingPointsForUnitSquareConfiguration,
    blockingPoints: BlockingPointContainer.View = BlockingPointContainer.View.EMPTY
): Set<PathingPoint> {
    val pathingPoints = mutableSetOf<PathingPoint>()

    val x = config.x
    val y = config.y
    val pathingPointPrecision = config.pathingPointPrecision

    val mapBlockingResults = MapBlockingUtil(x, y, 1, 1, config.mapWidth, config.mapHeight)

    val hasBlockingTopLeft = blockingPoints.contains(x - 1, y + 1)
    val hasBlockingTop = blockingPoints.contains(x, y + 1)
    val hasBlockingTopRight = blockingPoints.contains(x + 1, y + 1)

    val hasBlockingLeft = blockingPoints.contains(x - 1, y)
    val hasBlockingRight = blockingPoints.contains(x + 1, y)

    val hasBlockingBottomLeft = blockingPoints.contains(x - 1, y - 1)
    val hasBlockingBottom = blockingPoints.contains(x, y - 1)
    val hasBlockingBottomRight = blockingPoints.contains(x + 1, y - 1)

    //    println(
    //        """
    //            hasBlockingTopLeft: $hasBlockingTopLeft
    //            hasBlockingTop: $hasBlockingTop
    //            hasBlockingTopRight: $hasBlockingTopRight
    //
    //            hasBlockingLeft: $hasBlockingLeft
    //            hasBlockingRight: $hasBlockingRight
    //
    //            hasBlockingBottomLeft: $hasBlockingBottomLeft
    //            hasBlockingBottom: $hasBlockingBottom
    //            hasBlockingBottomRight: $hasBlockingBottomRight
    //        """.trimIndent()
    //    )

    // The diagonal pathing point precision would be the hypotenuse, so the x and y should be
    // should be shifted by k / sqrt(2).
    val diagonalPointPrecision = pathingPointPrecision / sqrt(2.0)

    if (config.calculateTopLeft.atLeastOneEnabled && !mapBlockingResults.isBlockingTop &&
        !mapBlockingResults.isBlockingLeft &&
        !hasBlockingTopLeft && !hasBlockingTop
    ) {
        // Top left - diagonal
        if (config.calculateTopLeft.calculateDiagonal && !hasBlockingLeft) {
            pathingPoints.add(
                PathingPoint.create(
                    x - diagonalPointPrecision, y + 1 +
                            diagonalPointPrecision,
                    VerticalDirection.DOWN,
                    HorizontalDirection.RIGHT
                )
            )
        }
        // Top left - bottom (P1)
        if (config.calculateTopLeft.calculateP1 && !hasBlockingLeft) {
            pathingPoints.add(
                PathingPoint.create(
                    x - pathingPointPrecision, y + 1.0,
                    VerticalDirection.NONE, HorizontalDirection.RIGHT
                )
            )
        }
        // Top left - right (P2)
        if (config.calculateTopLeft.calculateP2 && !hasBlockingLeft) {
            pathingPoints.add(
                PathingPoint.create(
                    x.toDouble(), y + 1 + pathingPointPrecision,
                    VerticalDirection.DOWN, HorizontalDirection.NONE
                )
            )
        }
    }

    if (config.calculateTopRight.atLeastOneEnabled && !mapBlockingResults.isBlockingTop && !hasBlockingTop &&
        !mapBlockingResults.isBlockingRight &&
        !hasBlockingTopRight
    ) {
        // Top right - diagonal
        if (config.calculateTopRight.calculateDiagonal && !hasBlockingRight) {
            pathingPoints.add(
                PathingPoint.create(
                    x + 1 + diagonalPointPrecision,
                    y + 1 + diagonalPointPrecision,
                    VerticalDirection.DOWN,
                    HorizontalDirection.LEFT
                )
            )
        }
        // Top right - left (P1)
        if (config.calculateTopRight.calculateP1 && !hasBlockingRight) {
            pathingPoints.add(
                PathingPoint.create(
                    x + 1.0, y + 1 + pathingPointPrecision,
                    VerticalDirection.DOWN, HorizontalDirection.NONE
                )
            )
        }
        // Top right - bottom (P2)
        if (config.calculateTopRight.calculateP2 && !hasBlockingRight) {
            pathingPoints.add(
                PathingPoint.create(
                    x + 1 + pathingPointPrecision, y + 1.0,
                    VerticalDirection.NONE, HorizontalDirection.LEFT
                )
            )
        }
    }

    if (config.calculateBottomRight.atLeastOneEnabled && !mapBlockingResults.isBlockingBottom &&
        !mapBlockingResults.isBlockingRight &&
        !hasBlockingBottom && !hasBlockingBottomRight
    ) {
        // Bottom right - diagonal
        if (config.calculateBottomRight.calculateDiagonal && !hasBlockingRight) {
            pathingPoints.add(
                PathingPoint.create(
                    x + 1 + diagonalPointPrecision, y -
                            diagonalPointPrecision,
                    VerticalDirection.UP,
                    HorizontalDirection.LEFT
                )
            )
        }
        // Bottom right - top (P1)
        if (config.calculateBottomRight.calculateP1 && !hasBlockingRight) {
            pathingPoints.add(
                PathingPoint.create(
                    x + 1 + pathingPointPrecision, y.toDouble(),
                    VerticalDirection.NONE, HorizontalDirection.LEFT
                )
            )
        }
        // Bottom right - left (P2)
        if (config.calculateBottomRight.calculateP2 && !hasBlockingBottom && !hasBlockingRight) {
            pathingPoints.add(
                PathingPoint.create(
                    x + 1.0, y - pathingPointPrecision,
                    VerticalDirection.UP, HorizontalDirection.NONE
                )
            )
        }
    }


    if (config.calculateBottomLeft.atLeastOneEnabled && !mapBlockingResults.isBlockingBottom &&
        !mapBlockingResults.isBlockingLeft &&
        !hasBlockingBottom && !hasBlockingBottomLeft
    ) {
        // Bottom left - diagonal
        if (config.calculateBottomLeft.calculateDiagonal && !hasBlockingLeft) {
            pathingPoints.add(
                PathingPoint.create(
                    x - diagonalPointPrecision, y -
                            diagonalPointPrecision,
                    VerticalDirection.UP,
                    HorizontalDirection.RIGHT
                )
            )
        }
        // Bottom left - right (P1)
        if (config.calculateBottomLeft.calculateP1 && !hasBlockingBottom && !hasBlockingLeft) {
            pathingPoints.add(
                PathingPoint.create(
                    x.toDouble(), y - pathingPointPrecision,
                    VerticalDirection.UP, HorizontalDirection.NONE
                )
            )
        }
        // Bottom left - top (P2)
        if (config.calculateBottomLeft.calculateP2 && !hasBlockingLeft) {
            pathingPoints.add(
                PathingPoint.create(
                    x - pathingPointPrecision, y.toDouble(),
                    VerticalDirection.NONE,
                    HorizontalDirection.RIGHT
                )
            )
        }
    }
    return pathingPoints.toSet()
}

/**
 * Returns all available pathing points from the blocking entities.
 */
fun getAvailablePathingPointsFromBlockingEntities(
    blockingEntities: List<IRectangleEntity>,
    mapWidth: Int, mapHeight: Int,
    blockingPoints: BlockingPointContainer.View,
    cornerPathingPointConfiguration: CornerPathingPointConfiguration =
        CornerPathingPointConfiguration.ONLY_DIAGONAL,
    pathingPointPrecision: Double = PATHING_POINT_PRECISION
): Set<PathingPoint> {
    val availablePoints = mutableSetOf<PathingPoint>()
    for (entity in blockingEntities) {
        // Top left
        availablePoints.addAll(
            getPathingPointsForUnitSquare(
                PathingPointsForUnitSquareConfiguration(
                    entity.topLeftUnitSquareGameUnitPoint.x.toInt(),
                    entity.topLeftUnitSquareGameUnitPoint.y.toInt(),
                    mapWidth,
                    mapHeight,
                    calculateTopLeft = cornerPathingPointConfiguration,
                    calculateTopRight = CornerPathingPointConfiguration.DISABLED,
                    calculateBottomLeft = CornerPathingPointConfiguration.DISABLED,
                    calculateBottomRight = CornerPathingPointConfiguration.DISABLED,
                    pathingPointPrecision = pathingPointPrecision
                ), blockingPoints
            )
        )

        availablePoints.addAll(
            getPathingPointsForUnitSquare(
                PathingPointsForUnitSquareConfiguration(
                    entity.topRightUnitSquareGameUnitPoint.x.toInt(),
                    entity.topRightUnitSquareGameUnitPoint.y.toInt(),
                    mapWidth,
                    mapHeight,
                    calculateTopLeft = CornerPathingPointConfiguration.DISABLED,
                    calculateTopRight = cornerPathingPointConfiguration,
                    calculateBottomLeft = CornerPathingPointConfiguration.DISABLED,
                    calculateBottomRight = CornerPathingPointConfiguration.DISABLED,
                    pathingPointPrecision = pathingPointPrecision
                ), blockingPoints
            )
        )

        availablePoints.addAll(
            getPathingPointsForUnitSquare(
                PathingPointsForUnitSquareConfiguration(
                    entity.bottomLeftUnitSquareGameUnitPoint.x.toInt(),
                    entity.bottomLeftUnitSquareGameUnitPoint.y.toInt(),
                    mapWidth,
                    mapHeight,
                    calculateTopLeft = CornerPathingPointConfiguration.DISABLED,
                    calculateTopRight = CornerPathingPointConfiguration.DISABLED,
                    calculateBottomLeft = cornerPathingPointConfiguration,
                    calculateBottomRight = CornerPathingPointConfiguration.DISABLED,
                    pathingPointPrecision = pathingPointPrecision
                ), blockingPoints
            )
        )

        availablePoints.addAll(
            getPathingPointsForUnitSquare(
                PathingPointsForUnitSquareConfiguration(
                    entity.bottomRightUnitSquareGameUnitPoint.x.toInt(),
                    entity.bottomRightUnitSquareGameUnitPoint.y.toInt(),
                    mapWidth,
                    mapHeight,
                    calculateTopLeft = CornerPathingPointConfiguration.DISABLED,
                    calculateTopRight = CornerPathingPointConfiguration.DISABLED,
                    calculateBottomLeft = CornerPathingPointConfiguration.DISABLED,
                    calculateBottomRight = cornerPathingPointConfiguration,
                    pathingPointPrecision = pathingPointPrecision
                ), blockingPoints
            )
        )
    }
    return availablePoints.toSet()
}

fun Set<PathingPoint>.points(): Set<IPoint> {
    return this.mapTo(mutableSetOf()) { it.v }
}

fun calculateShortestPointFromStartToEndCircle(
    point: IPoint,
    circleCenter: IPoint,
    radius: Double
): IPoint {
    val angleRadians = angleRadians(circleCenter, point)
    return IPoint(
        circleCenter.x + cos(angleRadians) * radius,
        circleCenter.y + sin(angleRadians) * radius
    )
}

fun calculateShortestPointsFromPointsToCircle(
    points: Set<IPoint>, circleCenter: IPoint,
    radius: Double
): Set<IPoint> {
    val shortestPoints = mutableSetOf<IPoint>()
    for (point in points) {
        shortestPoints.add(calculateShortestPointFromStartToEndCircle(point, circleCenter, radius))
    }
    return shortestPoints.toSet()
}

fun getNextPoints(
    currentPoint: IPoint,
    blockingEntities: List<IRectangleEntity>,
    availablePathingPoints: Set<PathingPoint>,
): List<IPoint> {
    val filtered = availablePathingPoints.mapNotNull {
        val verticalDirectionToPathingPoint = currentPoint.verticalDirectionTo(it.v)
        val horizontalDirectionToPathingPoint = currentPoint.horizontalDirectionTo(it.v)
        val lineIntersectsEntity =             lineIntersectsEntities(
            currentPoint,
            it.v,
            blockingEntities
        )
        if ((it.entityVerticalDirection == VerticalDirection.DOWN &&
                    it.entityHorizontalDirection == HorizontalDirection.LEFT &&
                    verticalDirectionToPathingPoint == VerticalDirection.UP &&
                    horizontalDirectionToPathingPoint == HorizontalDirection.RIGHT) ||
            (it.entityVerticalDirection == VerticalDirection.DOWN &&
                    it.entityHorizontalDirection == HorizontalDirection.RIGHT &&
                    verticalDirectionToPathingPoint == VerticalDirection.UP &&
                    horizontalDirectionToPathingPoint == HorizontalDirection.LEFT) ||
            (it.entityVerticalDirection == VerticalDirection.UP &&
                    it.entityHorizontalDirection == HorizontalDirection.LEFT &&
                    verticalDirectionToPathingPoint == VerticalDirection.DOWN &&
                    horizontalDirectionToPathingPoint == HorizontalDirection.RIGHT) ||
            (it.entityVerticalDirection == VerticalDirection.UP &&
                    it.entityHorizontalDirection == HorizontalDirection.RIGHT &&
                    verticalDirectionToPathingPoint == VerticalDirection.DOWN &&
                    horizontalDirectionToPathingPoint == HorizontalDirection.LEFT) ||
            lineIntersectsEntity
        ) {
            null
        } else {
            it.v
        }
    }

    return filtered
}

