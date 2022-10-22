package com.xenotactic.korge.korge_utils

import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.IPoint
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.geom.inBetween
import com.xenotactic.gamelogic.model.GameUnitPoint
import com.xenotactic.gamelogic.views.EightDirection
import com.xenotactic.korge.components.BottomLeftPositionComponent
import com.xenotactic.korge.components.SizeComponent

fun getCenterPoint(
    bottomLeftPositionComponent: BottomLeftPositionComponent,
    sizeComponent: SizeComponent
): GameUnitPoint {
    return GameUnitPoint(
        bottomLeftPositionComponent.x + sizeComponent.width / 2.0,
        bottomLeftPositionComponent.y + sizeComponent.height / 2.0
    )
}

// Returns the angle from one point to another, with respect to
// bottom left coordinate system rather than top left coordinate system.
fun IPoint.kAngleTo(other: IPoint): Angle {
    val yDiff = other.y - this.y
    return Angle.between(this.x, this.y, other.x, this.y - yDiff)
}

data class DirectionMatcher(
    val closedRange: ClosedRange<Angle>,
    val direction: EightDirection
)

val DIRECTION_MATCHERS = listOf(
    DirectionMatcher(330.degrees..(30.degrees), EightDirection.RIGHT),
    DirectionMatcher(60.degrees..(120.degrees), EightDirection.UP),
    DirectionMatcher(150.degrees..(210.degrees), EightDirection.LEFT),
    DirectionMatcher(240.degrees..(300.degrees), EightDirection.DOWN),
    DirectionMatcher(30.degrees..(60.degrees), EightDirection.UP_RIGHT),
    DirectionMatcher(120.degrees..(150.degrees), EightDirection.UP_LEFT),
    DirectionMatcher(210.degrees..(240.degrees), EightDirection.DOWN_LEFT),
    DirectionMatcher(300.degrees..(330.degrees), EightDirection.DOWN_RIGHT),
)

fun getDirection8(angle: Angle): EightDirection {
    val matcher = DIRECTION_MATCHERS.first {
        angle.inBetween(it.closedRange)
    }
    return matcher.direction
}
