package com.xenotactic.korge.utils

import com.xenotactic.ecs.AbstractEntity
import com.xenotactic.ecs.StatefulEntity
import com.xenotactic.gamelogic.components.BottomLeftPositionComponent
import com.xenotactic.gamelogic.components.SizeComponent
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.IRectangleEntity
import com.xenotactic.gamelogic.utils.*
import com.xenotactic.gamelogic.views.EightDirection
import korlibs.korge.view.*
import korlibs.math.geom.Anchor
import korlibs.math.geom.Angle
import korlibs.math.geom.Point
import korlibs.math.geom.degrees

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

fun AbstractEntity.isFullyCoveredBy(rectangleEntities: Iterable<IRectangleEntity>): Boolean {
    val thisPosition = get(BottomLeftPositionComponent::class)
    val thisSize = get(SizeComponent::class)

    val entityPoints = mutableSetOf<GameUnitTuple>()
    for (i in 0 until thisSize.width) {
        for (j in 0 until thisSize.height) {
            entityPoints.add(GameUnitTuple(thisPosition.x + i, thisPosition.y + j))
        }
    }

    println("entityPoints: $entityPoints")

    for (rectangleEntity in rectangleEntities) {
        entityPoints -= rectangleEntity.blockGameUnitPoints
        if (entityPoints.isEmpty()) return true
    }

    return false
}

fun AbstractEntity.isFullyCoveredBy(other: IRectangleEntity): Boolean {
    val thisPosition = get(BottomLeftPositionComponent::class)
    val thisSize = get(SizeComponent::class)
    return isFullyCoveredBy(
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

// Returns the angle from one point to another, with respect to
// bottom left coordinate system rather than top left coordinate system.
fun Point.kAngleTo(other: Point): Angle {
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

const val GUN_VIEW_NAME = "gun"

fun createUIEntityContainerForTower(
    worldWidth: WorldUnit,
    worldHeight: WorldUnit,
    uiEntityContainer: Container = Container()): Container {
    uiEntityContainer.image(GlobalResources.TOWER_BASE_SPRITE) {
        smoothing = false
        scaledWidth = worldWidth.toFloat()
        scaledHeight = worldHeight.toFloat()
    }
    uiEntityContainer.image(GlobalResources.TOWER_BASE_DETAIL_SPRITE) {
        smoothing = false
        scaledWidth = worldWidth.toFloat()
        scaledHeight = worldHeight.toFloat()
    }
    val gunImage = uiEntityContainer.image(GlobalResources.GUN_SPRITE) {
        smoothing = false
        anchor(Anchor.CENTER)
        xy(worldWidth / 2, worldHeight / 2)
        //                        scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(worldWidth.toFloat() * 20.0/32, worldHeight.toFloat() * 8.0/32))
        scaleWhileMaintainingAspect(
            ScalingOption.ByWidthAndHeight(
                worldWidth.toDouble(),
                worldHeight.toDouble()
            )
        )
        name(GUN_VIEW_NAME)
    }
    return uiEntityContainer
}
