package com.xenotactic.gamelogic.utils

import com.xenotactic.ecs.AbstractEntity
import com.xenotactic.gamelogic.components.BottomLeftPositionComponent
import com.xenotactic.gamelogic.components.SizeComponent
import com.xenotactic.gamelogic.model.*
import com.xenotactic.gamelogic.pathing.HorizontalDirection
import com.xenotactic.gamelogic.pathing.VerticalDirection
import korlibs.math.geom.*
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

const val LINE_INTERSECTION_DIFF_THRESHOLD = 0.01
fun Point(x: WorldUnit, y: WorldUnit): Point = Point(x.toDouble(), y.toDouble())

fun <T> sequenceOfNullable(element: T?) =
    if (element == null) emptySequence<T>() else sequenceOf(element)

fun isFullyCoveredBy(
    x1: Double,
    y1: Double,
    w1: Double,
    h1: Double,
    x2: Double,
    y2: Double,
    w2: Double,
    h2: Double,
): Boolean {
    if (w1 > w2) return false
    if (h1 > h2) return false

    return x1 >= x2 && (x1 + w1) <= (x2 + w2) &&
            y1 >= y2 && (y1 + h1) <= (y2 + h2)
}

fun intersectRectangles(
    x1: Double,
    y1: Double,
    w1: Double,
    h1: Double,
    x2: Double,
    y2: Double,
    w2: Double,
    h2: Double,
): Boolean {
    return x1 < (x2 + w2) &&
            y1 < (y2 + h2) &&
            (x1 + w1) > x2 &&
            (y1 + h1) > y2
}

fun IPoint.verticalDirectionTo(v: IPoint, allowableDifference: Double = ALLOWABLE_DIRECTION_DIFF):
        VerticalDirection {
    if (abs(this.y - v.y) < allowableDifference) {
        return VerticalDirection.NONE
    }
    if (this.y > v.y) {
        return VerticalDirection.DOWN
    } else {
        return VerticalDirection.UP
    }
}

fun IPoint.horizontalDirectionTo(v: IPoint, allowableDifference: Double = ALLOWABLE_DIRECTION_DIFF):
        HorizontalDirection {
    if (abs(this.x - v.x) < allowableDifference) {
        return HorizontalDirection.NONE
    }
    if (this.x > v.x) {
        return HorizontalDirection.LEFT
    } else {
        return HorizontalDirection.RIGHT
    }
}

@OptIn(ExperimentalTime::class)
inline fun <T> measureTime(
    message: String? = null,
    printMessage: Boolean = true,
    block: () -> T
):
        Pair<Long, T> {
    val ret = measureTimedValue {
        block()
    }
    if (printMessage) {
        if (message == null) {
            println("Took ${ret.duration}")
        } else {
            println("$message: Took ${ret.duration}")
        }
    }
    return Pair(ret.duration.inWholeNanoseconds, ret.value)
}

fun String.removeAllIndents(): String {
    val lines = lines()
    return lines.joinToString(separator = "\n") {
        it.trimIndent()
    }
}

fun rectangleIntersects(a: IRectangleEntity, b: IRectangleEntity): Boolean {
    return a.left < b.right && a.right > b.left &&
            a.bottom < b.top && a.top > b.bottom
}

fun getIntersectionPointsOfLineSegmentAndCircle(
    p1: IPoint,
    p2: IPoint,
    circleCenter: IPoint,
    radius: Double
): Set<IPoint> {
    val lowerX = kotlin.math.min(p1.x, p2.x)
    val upperX = kotlin.math.max(p1.x, p2.x)
    val lowerY = kotlin.math.min(p1.y, p2.y)
    val upperY = kotlin.math.max(p1.y, p2.y)
    val intersectionPoints =
        getIntersectionPointsOfLineEquationFromPointsAndCircle(p1, p2, circleCenter, radius)
    println("lowerX: $lowerX, upperX: $upperX, lowerY: $lowerY, upperY: $upperY")
    println(intersectionPoints)
    return intersectionPoints.filter {
        it.x in lowerX..upperX && it.y in lowerY..upperY
    }.toSet()
}

fun getIntersectionPointsOfLineEquationFromPointsAndCircle(
    p1: IPoint,
    p2: IPoint,
    circleCenter: IPoint,
    radius: Double
): Set<IPoint> {
    // Equation for a circle is: (x - u)^2 + (y - v)^2 = r^2
    // Equation for a line is: y = mx + b
    // where m = (y2 - y1) / (x2 - x1)
    // and   b = y1 - m * x1

    if (p1.x == p2.x && p1.y == p2.y) {
        return setOf()
    }

    val xDiff = abs(p2.x - p1.x)
    println("xDiff: $xDiff, meets threshold: ${xDiff < LINE_INTERSECTION_DIFF_THRESHOLD}")
    if (xDiff <= LINE_INTERSECTION_DIFF_THRESHOLD) {
        println("Vertical line")
        return getIntersectionPointsOfVerticalLineAndCircle(p1.x, circleCenter, radius)
    }

    if (p1.y == p2.y) {
        println("Horizontal line")
        return getIntersectionPointsOfHorizontalLineAndCircle(p1.y, circleCenter, radius)
    }

    val circleU = circleCenter.x
    val circleV = circleCenter.y

    println("normal line")
    println("p2.x - p1.x: ${p2.x - p1.x}")
    val yLineM = (p2.y - p1.y) / (p2.x - p1.x)
    val yLineB = p1.y - yLineM * p1.x

    val k = yLineB - circleV

    // ax^2 + bx + c
    val a = 1 + yLineM * yLineM
    val b = 2 * yLineM * k - 2 * circleU
    val c = circleU * circleU + k * k - radius * radius

    // Solving for a quadratic:
    // x = (-b + sqrt(b^2 - 4ac)) / 2a
    // x = (-b - sqrt(b^2 - 4ac)) / 2a
    println("a: $a, b: $b, c: $c")

    // b^2 - 4ac
    val sqrtTerm = b * b - 4 * a * c
    println("sqrtTerm: $sqrtTerm")
    if (sqrtTerm < 0) return setOf<IPoint>()

    val xFromPositive = (-b + sqrt(sqrtTerm)) / (2 * a)
    println("xFromPositive: $xFromPositive, yFromPositive: ${yLineM * xFromPositive + yLineB}")
    // There's only 1 term
    if (sqrtTerm == 0.0) {
        return setOf(IPoint(xFromPositive, yLineM * xFromPositive + yLineB))
    }
    val xFromNegative = (-b - sqrt(sqrtTerm)) / (2 * a)
    println("xFromNegative: $xFromNegative, yFromNegative: ${yLineM * xFromNegative + yLineB}")
    return setOf(
        IPoint(xFromPositive, yLineM * xFromPositive + yLineB),
        IPoint(xFromNegative, yLineM * xFromNegative + yLineB)
    )
}

fun getIntersectionPointsOfHorizontalLineAndCircle(
    b: Double, circleCenter: IPoint,
    radius: Double
): Set<IPoint> {
    // Horizontal line: y = b for all x
    // The circle equation: (x - u)^2 + (y - v)^2 = r^2
    // becomes: (x - u)^2 + (b - v)^2 = r^2
    //
    // Solving for x yields:
    // (positive case) x = sqrt(r^2 - (b - v)^2) + u
    // (negative case) x = -sqrt(r^2 - (b - v)^2) + u
    //
    // The intersection points would be ((postive case) x, b), ((negative case) x, b)
    val circleU = circleCenter.x
    val circleV = circleCenter.y
    val sqrtTerm = radius * radius - (b - circleV).pow(2)
    if (sqrtTerm < 0) return setOf()
    if (sqrtTerm == 0.0) return setOf(IPoint(circleU, b))
    val sqrt = sqrt(sqrtTerm)
    return setOf(IPoint(circleU + sqrt, b), IPoint(circleU - sqrt, b))
}

fun getIntersectionPointsOfVerticalLineAndCircle(
    a: Double, circleCenter: IPoint,
    radius: Double
): Set<IPoint> {
    // Vertical line: x = a for all y
    // The circle equation: (x - u)^2 + (y - v)^2 = r^2
    // becomes: (a - u)^2 + (y - v)^2 = r^2
    //
    // Solving for y yields:
    // (positive case) y = sqrt(r^2 - (a - u)^2) + v
    // (negative case) y = -sqrt(r^2 - (a - u)^2) + v
    //
    // The intersection points would be (a, (positive case) y), (a, (negative case) y)
    val circleU = circleCenter.x
    val circleV = circleCenter.y
    val sqrtTerm = radius * radius - (a - circleU).pow(2)
    if (sqrtTerm < 0) return setOf()
    if (sqrtTerm == 0.0) return setOf(IPoint(a, circleV))
    val sqrt = sqrt(sqrtTerm)
    return setOf(IPoint(a, circleV + sqrt), IPoint(a, circleV - sqrt))
}

fun IPoint.lerp(target: IPoint, alpha: Double): IPoint {
    val invAlpha = (1.0f - alpha).toFloat()
    return IPoint(
        this.x * invAlpha + target.x * alpha,
        this.y * invAlpha + target.y * alpha
    )
}

fun getIntersectionPointsOfLineSegmentAndRectangle(
    p1: IPoint,
    p2: IPoint,
    rectBottomLeft: IPoint,
    width: Float,
    height: Float
): Set<IPoint> {
    val lowerRectX = rectBottomLeft.x
    val upperRectX = rectBottomLeft.x + width
    val lowerRectY = rectBottomLeft.y
    val upperRectY = rectBottomLeft.y + height

    val lowerPointX = kotlin.math.min(p1.x, p2.x)
    val upperPointX = kotlin.math.max(p1.x, p2.x)

    val lowerPointY = kotlin.math.min(p1.y, p2.y)
    val upperPointY = kotlin.math.max(p1.y, p2.y)

    val intersectionPoints = mutableSetOf<IPoint>()

    if (p1.x == p2.x) {
        if (p1.x !in lowerRectX..upperRectX) return emptySet()
        if (lowerRectY in lowerPointY..upperPointY) intersectionPoints.add(
            IPoint(
                p1.x,
                lowerRectY
            )
        )
        if (upperRectY in lowerPointY..upperPointY) intersectionPoints.add(
            IPoint(
                p1.x,
                upperRectY
            )
        )
        return intersectionPoints.toSet()
    }

    if (p1.y == p2.y) {
        if (p1.y !in lowerRectY..upperRectY) return emptySet()
        if (lowerRectX in lowerPointX..upperPointX) intersectionPoints.add(
            IPoint(
                lowerRectX,
                p1.y
            )
        )
        if (upperRectX in lowerPointX..upperPointX) intersectionPoints.add(
            IPoint(
                upperRectX,
                p1.y
            )
        )
        return intersectionPoints.toSet()
    }

    val yLineM = (p2.y - p1.y) / (p2.x - p1.x)
    val yLineB = p1.y - yLineM * p1.x

    val yFunc = { x: Double -> yLineM * x + yLineB }
    val xFunc = { y: Double -> (y - yLineB) / yLineM }

    val rectTopY = rectBottomLeft.y + height
    val rectBotY = rectBottomLeft.y

    val rectLeftX = rectBottomLeft.x
    val rectRightX = rectBottomLeft.x + width

    // Bottom side
    if (rectBotY in lowerPointY..upperPointY) {
        val intersectRectX = xFunc(rectBotY)
        if (intersectRectX in lowerRectX..upperRectX) {
            intersectionPoints.add(IPoint(intersectRectX, rectBotY))
        }
    }

    // Top side
    if (rectTopY in lowerPointY..upperPointY) {
        val intersectRectX = xFunc(rectTopY)
        if (intersectRectX in lowerRectX..upperRectX) {
            intersectionPoints.add(IPoint(intersectRectX, rectTopY))
        }
    }

    // Left side
    if (rectLeftX in lowerPointX..upperPointX) {
        val rectY = yFunc(rectLeftX)
        if (rectY in lowerRectY..upperRectY) {
            intersectionPoints.add(IPoint(rectLeftX, rectY))
        }
    }

    // Right side
    if (rectRightX in lowerPointX..upperPointX) {
        val rectY = yFunc(rectRightX)
        if (rectY in lowerRectY..upperRectY) {
            intersectionPoints.add(IPoint(rectRightX, rectY))
        }
    }


    return intersectionPoints.toSet()
}

fun angleRadians(v1: IPoint, v2: IPoint): Angle {
    return atan2(v2.y - v1.y, v2.x - v1.x).radians
}

fun abs(f1: Float): Float {
    return if (f1 < 0) -f1 else f1
}

fun toWorldCoordinates(
    gridSize: Number, gameUnitPoint: GameUnitTuple, gameHeight: GameUnit, entityHeight: GameUnit = GameUnit(0)
): Pair<WorldUnit, WorldUnit> =
    toWorldCoordinates(
        gridSize,
        gameUnitPoint.x.value.toDouble(), gameUnitPoint.y.value.toDouble(), gameHeight, entityHeight
    )

fun toWorldCoordinates(
    gridSize: Number, point: IPoint, gameHeight: GameUnit, entityHeight: GameUnit = GameUnit(0)
): Pair<WorldUnit, WorldUnit> =
    toWorldCoordinates(
        gridSize,
        point.x, point.y, gameHeight, entityHeight
    )

fun toWorldCoordinates(
    gridSize: Number, x: Double, y: Double, gameHeight: GameUnit, entityHeight: GameUnit = GameUnit(0)
) = Pair(toWorldUnit(gridSize, x), toWorldUnit(gridSize, (gameHeight.value - y - entityHeight.value)))

fun toWorldUnit(gridSize: Number, value: Double) = value.toGameUnit().toWorldUnit(gridSize)

fun toWorldDimensions(width: GameUnit, height: GameUnit, gridSize: Number) =
    Pair(WorldUnit(width.value * gridSize.toDouble()), WorldUnit(height.value * gridSize.toDouble()))

fun toWorldDimensions(entity: MapEntity, gridSize: Number) =
    toWorldDimensions(entity.width, entity.height, gridSize)

data class RectangleEntityBuilder(
    val position: GameUnitTuple,
    val width: GameUnit
)

// wW = with Width
infix fun GameUnitTuple.wW(width: Number) = RectangleEntityBuilder(
    this, width.toGameUnit()
)

// wH = with Height
infix fun RectangleEntityBuilder.wH(height: Number): IRectangleEntity = RectangleEntity(
    position.x, position.y, width, height.toGameUnit()
)

fun AbstractEntity.toRectangleEntity(): RectangleEntity {
    val thisPosition = get(BottomLeftPositionComponent::class)
    val thisSize = get(SizeComponent::class)
    return RectangleEntity(thisPosition.x, thisPosition.y, thisSize.width, thisSize.height)
}

fun getCenterPoint(
    bottomLeftPositionComponent: BottomLeftPositionComponent,
    sizeComponent: SizeComponent
): GameUnitTuple {
    return GameUnitTuple(
        bottomLeftPositionComponent.x + sizeComponent.width / 2.0,
        bottomLeftPositionComponent.y + sizeComponent.height / 2.0
    )
}

data class UpgradeDecision(
    val maxPossibleUpgradesDelta: Int,
    val upgradesCost: Int
)

fun calculateUpgradeDecision(
    currentMoney: Int, currentNumUpgrades: Int, maxUpgrades: Int, initialUpgradeCost: Int,
    numUpgradesWanted: Int): UpgradeDecision {
    var availableMoney = currentMoney
    var maxPossibleUpgrades = 0
    while ((currentNumUpgrades + maxPossibleUpgrades) < maxUpgrades && maxPossibleUpgrades < numUpgradesWanted) {
        val currentUpgradeCost = initialUpgradeCost + currentNumUpgrades + maxPossibleUpgrades
        if (currentUpgradeCost > availableMoney) {
            break
        } else {
            availableMoney -= currentUpgradeCost
            maxPossibleUpgrades++
        }
    }
    return UpgradeDecision(maxPossibleUpgrades, currentMoney - availableMoney)
}

fun calculateCostOfUpgrades(
    currentNumUpgrades: Int, initialUpgradeCost: Int, numUpgradesWanted: Int
) = (currentNumUpgrades + initialUpgradeCost) * numUpgradesWanted + (numUpgradesWanted - 1) * numUpgradesWanted / 2

infix fun Number.size(right: Number) = Size(this.toFloat(), right.toFloat())
infix fun Number.rectCorner(right: Number) = RectCorners(this.toFloat(), right.toFloat())

fun Number.toScale() = Scale(this.toFloat())
operator fun Scale.minus(other: Number) = Scale(this.avg - other.toFloat())
operator fun Number.times(scale: Scale) = this.toFloat() * scale.avg