package com.xenotactic.gamelogic.utils

import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.components.BottomLeftPositionComponent
import com.xenotactic.gamelogic.components.SizeComponent

fun getCenterPoint(
    bottomLeftPositionComponent: BottomLeftPositionComponent,
    sizeComponent: SizeComponent
): Point {
    return Point(
        bottomLeftPositionComponent.x.value + sizeComponent.width.value.toFloat() / 2f,
        bottomLeftPositionComponent.y.value + sizeComponent.height.value / 2f
    )
}