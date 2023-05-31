package com.xenotactic.gamelogic.utils

import com.xenotactic.ecs.EntityId
import com.xenotactic.ecs.StagingEntity

sealed class RestrictionResult {
    data class Error(val errorMessage: String): RestrictionResult()
    object Ok: RestrictionResult()
}

fun checkCanPlaceEntity(gameMapApi: GameMapApi, entity: StagingEntity): RestrictionResult {
    if (gameMapApi.checkNewEntitiesBlocksPath(entity)) {
        return RestrictionResult.Error("Blocks path")
    }

    if (gameMapApi.checkNewEntityIntersectsExistingBlockingEntities(entity)) {
        return RestrictionResult.Error("Intersects with another entity")
    }

    return RestrictionResult.Ok
}

fun checkCanPlaceTowerEntity(gameMapApi: GameMapApi, entity: StagingEntity): RestrictionResult {
    when (val it = checkCanPlaceEntity(gameMapApi, entity)) {
        is RestrictionResult.Error -> return it
        RestrictionResult.Ok -> {
            if (gameMapApi.isAtMaxSupply()) {
                return RestrictionResult.Error("Is at max supply")
            }
            return RestrictionResult.Ok
        }
    }
}