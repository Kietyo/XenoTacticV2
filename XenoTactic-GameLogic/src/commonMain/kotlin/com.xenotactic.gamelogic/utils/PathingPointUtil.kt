package utils



import com.xenotactic.gamelogic.containers.BlockingPointContainer
import com.xenotactic.gamelogic.utils.PATHING_RADIUS
import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.model.IRectangleEntity
import kotlin.math.sqrt

data class EntityMask(
    val topLeftFilled: Boolean = false,
    val topRightFilled: Boolean = false,
    val bottomLeftFilled: Boolean = false,
    val bottomRightFilled: Boolean = false,
)

class PathingPointUtil(val pathingRadius: Double = PATHING_RADIUS) {
    private val diagonalXYDelta = pathingRadius / sqrt(2.0)

    /**
     * Map of mask to pathing point offset from the center of the entity.
     *
     * For example, a mapping of
     * - EntityMask(true, false, false, false) to (diagonalXYDelta to -diagonalXYDelta)
     *
     * means that when the top left square is blocked, then the pathing point will be at
     * (centerX + diagonalXYDelta, centerY - diagonalXYDelta)
     *
     * See documentation/pathing_points_guide.png for a diagram of this.
     */
    private val maskToPathingPointOffset =
        mapOf<EntityMask, List<Pair<Double, Double>>>(
            EntityMask(false, false, false, false) to listOf(0.0 to 0.0),
            // 1 square filled
            EntityMask(true, false, false, false) to listOf(diagonalXYDelta to -diagonalXYDelta),
            EntityMask(false, true, false, false) to listOf(-diagonalXYDelta to -diagonalXYDelta),
            EntityMask(false, false, true, false) to listOf(diagonalXYDelta to diagonalXYDelta),
            EntityMask(false, false, false, true) to listOf(-diagonalXYDelta to diagonalXYDelta),
            // 2 squares filled
            EntityMask(true, false, true, false) to listOf(pathingRadius to 0.0),
            EntityMask(false, true, true, false) to listOf(
                -diagonalXYDelta to diagonalXYDelta,
                diagonalXYDelta to -diagonalXYDelta
            ),
            EntityMask(true, true, false, false) to listOf(0.0 to -pathingRadius),
            EntityMask(true, false, false, true) to listOf(
                diagonalXYDelta to diagonalXYDelta,
                -diagonalXYDelta to -diagonalXYDelta
            ),
            EntityMask(false, true, false, true) to listOf(-pathingRadius to 0.0),
            EntityMask(false, false, true, true) to listOf(0.0 to pathingRadius),
            // 3 Squares filled
            EntityMask(true, true, true, false) to listOf(diagonalXYDelta to -diagonalXYDelta),
            EntityMask(true, true, false, true) to listOf(-diagonalXYDelta to -diagonalXYDelta),
            EntityMask(false, true, true, true) to listOf(-diagonalXYDelta to diagonalXYDelta),
            EntityMask(true, false, true, true) to listOf(diagonalXYDelta to diagonalXYDelta),
            // All squares filled (no pathing points)
            EntityMask(true, true, true, true) to emptyList(),
        )

    fun calculate(entity: IRectangleEntity, blockingPoints: BlockingPointContainer.View): Set<IPoint> {
        require(entity.width.toInt() == 2 && entity.height.toInt() == 2) {
            "Only works with 2x2 entities!"
        }

        val entityMask = EntityMask(
            blockingPoints.contains(entity.x, entity.y + 1),
            blockingPoints.contains(entity.x+ 1, entity.y + 1),
            blockingPoints.contains(entity.x, entity.y),
            blockingPoints.contains(entity.x + 1, entity.y),
        )

        return maskToPathingPointOffset[entityMask]!!.map {
            IPoint(entity.centerPoint.x + it.first, entity.centerPoint.y + it.second)
        }.toSet()
    }

}