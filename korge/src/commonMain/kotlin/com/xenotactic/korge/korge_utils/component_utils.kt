package com.xenotactic.korge.korge_utils

import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.IPoint
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.geom.inBetween
import com.xenotactic.ecs.StagingEntity
import com.xenotactic.ecs.StatefulEntity
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.IRectangleEntity
import com.xenotactic.gamelogic.model.RectangleEntity
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.intersectRectangles
import com.xenotactic.gamelogic.views.EightDirection
import com.xenotactic.korge.components.BottomLeftPositionComponent
import com.xenotactic.korge.components.SizeComponent

fun getCenterPoint(
    bottomLeftPositionComponent: BottomLeftPositionComponent,
    sizeComponent: SizeComponent
): GameUnitTuple {
    return GameUnitTuple(
        bottomLeftPositionComponent.x + sizeComponent.width / 2.0,
        bottomLeftPositionComponent.y + sizeComponent.height / 2.0
    )
}

fun GameUnitTuple.toBottomLeftPositionComponent(): BottomLeftPositionComponent {
    return BottomLeftPositionComponent(x, y)
}

fun GameUnitTuple.toSizeComponent(): SizeComponent {
    return SizeComponent(first, second)
}

fun StatefulEntity.intersectsEntity(position: GameUnitTuple, size: GameUnitTuple): Boolean {
    val thisPosition = get(BottomLeftPositionComponent::class)
    val thisSize = get(SizeComponent::class)
    return intersectRectangles(
        thisPosition.x.toDouble(),
        thisPosition.y.toDouble(),
        thisSize.width.toDouble(),
        thisSize.height.toDouble(),
        position.x.toDouble(),
        position.y.toDouble(),
        size.first.toDouble(),
        size.second.toDouble()
    )
}

fun StatefulEntity.isFullyCoveredBy(entities: Iterable<IRectangleEntity>): Boolean {
    return entities.any {
        this.isFullyCoveredBy(it)
    }
}

fun StatefulEntity.isFullyCoveredBy(other: IRectangleEntity): Boolean {
    val thisPosition = get(BottomLeftPositionComponent::class)
    val thisSize = get(SizeComponent::class)
    return com.xenotactic.gamelogic.utils.isFullyCoveredBy(
        thisPosition.x.toDouble(),
        thisPosition.y.toDouble(),
        thisSize.width.toDouble(),
        thisSize.height.toDouble(),
        other.x.toDouble(),
        other.y.toDouble(),
        other.width.toDouble(),
        other.height.toDouble(),
    )
}

fun intersectRectangles(thisPosition: GameUnitTuple, thisSize: GameUnitTuple,
                        otherPosition: GameUnitTuple, otherSize: GameUnitTuple
): Boolean {
    return intersectRectangles(
        thisPosition.x.toDouble(),
        thisPosition.y.toDouble(),
        thisSize.first.toDouble(),
        thisSize.second.toDouble(),
        otherPosition.x.toDouble(),
        otherPosition.y.toDouble(),
        otherSize.first.toDouble(),
        otherSize.second.toDouble()
    )
}

fun StagingEntity.toRectangleEntity(): RectangleEntity {
    val thisPosition = get(BottomLeftPositionComponent::class)
    val thisSize = get(SizeComponent::class)
    return RectangleEntity(thisPosition.x, thisPosition.y, thisSize.width, thisSize.height)
}

fun StatefulEntity.toRectangleEntity(): RectangleEntity {
    val thisPosition = get(BottomLeftPositionComponent::class)
    val thisSize = get(SizeComponent::class)
    return RectangleEntity(thisPosition.x, thisPosition.y, thisSize.width, thisSize.height)
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

fun IRectangleEntity.getBottomLeftPositionComponent(): BottomLeftPositionComponent {
    return BottomLeftPositionComponent(x, y)
}

fun IRectangleEntity.getSizeComponent(): SizeComponent {
    return SizeComponent(width, height)
}
