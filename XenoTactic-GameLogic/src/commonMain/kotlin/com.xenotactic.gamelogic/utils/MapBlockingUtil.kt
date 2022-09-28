package com.xenotactic.gamelogic.utils

import com.xenotactic.gamelogic.model.MapEntity

data class MapBlockingUtilResults(
    val isBlockingTop: Boolean,
    val isBlockingBottom: Boolean,
    val isBlockingLeft: Boolean,
    val isBlockingRight: Boolean
) {
    val hasBlockingSide: Boolean
        get() {
            return isBlockingTop || isBlockingBottom || isBlockingLeft || isBlockingRight
        }
}

object MapBlockingUtil {
    // Returns how the map is blocking the given entity.
    operator fun invoke(entity: MapEntity, mapWidth: Int, mapHeight: Int): MapBlockingUtilResults {
        return invoke(entity.x.value, entity.y.value, entity.width.value, entity.height.value, mapWidth, mapHeight)
    }

    operator fun invoke(
        x: Int,
        y: Int,
        entityWidth: Int,
        entityHeight: Int,
        mapWidth: Int,
        mapHeight: Int
    ): MapBlockingUtilResults {
        return MapBlockingUtilResults(
            y + entityHeight >= mapHeight,
            y <= 0,
            x <= 0,
            x + entityWidth >= mapWidth
        )
    }
}