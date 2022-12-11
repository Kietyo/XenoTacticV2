package com.xenotactic.korge.korge_utils

import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.radians
import com.xenotactic.ecs.StagingEntity
import com.xenotactic.gamelogic.globals.ALLOWABLE_DIRECTION_DIFF
import com.xenotactic.gamelogic.model.GRectInt
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.pathing.HorizontalDirection
import com.xenotactic.gamelogic.pathing.VerticalDirection
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.WorldUnit
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.gamelogic.utils.toWorldCoordinates
import com.xenotactic.gamelogic.components.BottomLeftPositionComponent
import com.xenotactic.gamelogic.components.SizeComponent

import kotlin.math.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue



infix fun Int.to(that: Int): GameUnitTuple = GameUnitTuple(this, that)

fun <T> Sequence<T>.isEmpty(): Boolean {
    return this.firstOrNull() == null
}


fun toWorldCoordinates(gridSize: Double, entity: StagingEntity, gameWidth: GameUnit, gameHeight: GameUnit): Pair<WorldUnit, WorldUnit> {
    val position = entity[com.xenotactic.gamelogic.components.BottomLeftPositionComponent::class]
    val size = entity[com.xenotactic.gamelogic.components.SizeComponent::class]
    return toWorldCoordinates(
        gridSize,
        position.toTuple(), gameHeight, entityHeight = size.height
    )
}

fun toWorldCoordinates(gridSize: Double, entity: MapEntity, gameWidth: GameUnit, gameHeight: GameUnit): Pair<WorldUnit, WorldUnit> {
    return toWorldCoordinates(
        gridSize,
        entity.gameUnitPoint, gameHeight, entityHeight = entity.height
    )
}

//fun <E> Iterable<E>.sumOf(selector: (E) -> Float): Float {
//    var sum = 0f
//    for (element in this) {
//        sum += selector(element)
//    }
//    return sum
//}




fun main() {
    //     println(getIntersectionPoints(_root_ide_package_.com.soywiz.korma.geom.Point(0f, 0f), _root_ide_package_.com.soywiz.korma.geom.Point(8f, 8f), _root_ide_package_.com.soywiz.korma.geom.Point(3f, 3f), 3f))
    //    println(sqrt(2f) + 3)
    //    println(-sqrt(2f) + 3)

    val num = 12.65745

    val rounded = (num * 100).toInt() / 100.0

    println(rounded)

}