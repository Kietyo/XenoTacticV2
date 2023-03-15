package com.xenotactic.gamelogic.utils



import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.pathing.Segment

/**
 * Util for calculating the intersection of a circle and a line segment.
 */
object CircleIntersectionUtil {
    sealed class Result {
        // The circle and line do not intersect with each other.
        object NoIntersection : Result()

        // The line is completely covered by the circle
        object CircleFullyCovers : Result()

        // The segment partially intersects with the circle.
        // This means that there's only 1 intersection point between the segment and the circle.
        // One of the segment points must be outside of the circle.
        data class PartialIntersection(
            val firstPointInside: Boolean,
            val secondPointInside: Boolean,
            val intersectionPoint: IPoint
        ) : Result()

        // The segment fully intersects with the circle.
        // That means there are 2 intersection points between the segment and the circle.
        // This also means that both the first and second point must be outside of the circle
        // for this to be the case.
        //
        // The `firstIntersectionPoint` will always be the point closest to the first point in
        // the segment.
        data class FullIntersection(
            val firstIntersectionPoint: IPoint,
            val secondIntersectionPoint: IPoint
        ) : Result()

        // The circle is just touching EITHER the first or second point of the segment.
        data class Touching(
            val touchesFirstPoint: Boolean,
            val touchesSecondPoint: Boolean
        ) : Result() {
            init {
                require(touchesFirstPoint != touchesSecondPoint)
            }
        }

        // The line is tangent to the circle at a point.
        data class Tangent(
            val tangentPoint: IPoint
        ) : Result()
    }

    operator fun invoke(
        segment: Segment,
        circleCenter: IPoint,
        radius: Double
    ): Result {
        return invoke(segment.point1.toPoint(), segment.point2.toPoint(), circleCenter, radius)
    }

    operator fun invoke(
        p1: IPoint,
        p2: IPoint,
        circleCenter: IPoint,
        radius: Double
    ): Result {
        val p1ToCenterDst = p1.distanceTo(circleCenter)
        val p2ToCenterDst = p2.distanceTo(circleCenter)
        if (p1ToCenterDst <= radius && p2ToCenterDst <= radius) {
            return Result.CircleFullyCovers
        }

        val isP1InsideCircle = p1ToCenterDst < radius
        val isP2InsideCircle = p2ToCenterDst < radius
        val isP1TouchingCircle = p1ToCenterDst == radius
        val isP2TouchingCircle = p2ToCenterDst == radius

        val intersectionPoints = getIntersectionPointsOfLineSegmentAndCircle(
            p1,
            p2,
            circleCenter,
            radius
        ).sortedBy {
            p1.distanceTo(it)
        }

        if (!isP1InsideCircle && !isP2InsideCircle) {
            return if (intersectionPoints.isEmpty()) {
                Result.NoIntersection
            } else {
                if (intersectionPoints.size == 2) {
                    Result.FullIntersection(
                        intersectionPoints[0],
                        intersectionPoints[1]
                    )
                } else {
                    val intersectionPoint = intersectionPoints.first()
                    if (intersectionPoint == p1) {
                        Result.Touching(
                            touchesFirstPoint = true,
                            touchesSecondPoint = false
                        )
                    } else if (intersectionPoint == p2) {
                        Result.Touching(
                            touchesFirstPoint = false,
                            touchesSecondPoint = true
                        )
                    } else {
                        Result.Tangent(intersectionPoint)

                    }
                }
            }
        }

        require(intersectionPoints.size == 1)
        val intersectionPoint = intersectionPoints.first()
        if (intersectionPoint == p1) {
            return Result.Touching(
                touchesFirstPoint = true,
                touchesSecondPoint = false
            )
        }
        if (intersectionPoint == p2) {
            return Result.Touching(
                touchesFirstPoint = false,
                touchesSecondPoint = true
            )
        }

        require(!isP1TouchingCircle && !isP2TouchingCircle)
        if (isP1InsideCircle) {
            return Result.PartialIntersection(
                true,
                false,
                intersectionPoint
            )
        }

        if (isP2InsideCircle) {
            require(isP2InsideCircle)
            return Result.PartialIntersection(
                false,
                true,
                intersectionPoint
            )
        }
        TODO("Shouldn't ever go here")
    }
}