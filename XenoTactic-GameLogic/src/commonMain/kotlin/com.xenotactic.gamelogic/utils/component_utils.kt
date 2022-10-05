package com.xenotactic.gamelogic.utils

import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.components.BottomLeftPositionComponent
import com.xenotactic.gamelogic.components.SizeComponent
import com.xenotactic.gamelogic.model.GameUnitPoint

fun getCenterPoint(
    bottomLeftPositionComponent: BottomLeftPositionComponent,
    sizeComponent: SizeComponent
): GameUnitPoint {
    return GameUnitPoint(
        bottomLeftPositionComponent.x + sizeComponent.width / 2.0,
        bottomLeftPositionComponent.y + sizeComponent.height / 2.0
    )
}