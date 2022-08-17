package com.xenotactic.gamelogic.utils

import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.components.BottomLeftPositionComponent
import com.xenotactic.gamelogic.components.SizeComponent

fun getCenterPoint(
    bottomLeftPositionComponent: BottomLeftPositionComponent,
    sizeComponent: SizeComponent
): Point {
    return Point(
        bottomLeftPositionComponent.x + sizeComponent.width / 2f,
        bottomLeftPositionComponent.y + sizeComponent.height / 2f
    )
}