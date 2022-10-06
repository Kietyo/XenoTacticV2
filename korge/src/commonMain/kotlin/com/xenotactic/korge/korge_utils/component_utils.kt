package com.xenotactic.korge.korge_utils

import com.xenotactic.gamelogic.model.GameUnitPoint
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