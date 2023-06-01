package com.xenotactic.gamelogic.utils

import com.xenotactic.ecs.StagingEntity

enum class ErrorType(val description: String, val shortString: String) {
    NONE("No errors", "No errors"),
    BLOCKS_PATH("Placing entity blocks the path.", "Blocks path"),
    INTERSECTS_BLOCKING_ENTITIES(
        "Placing entity intersects with other blocking entities.",
        "Intersects with another entity"
    ),
    MAX_SUPPLY("Unable to place because already at max supply.", "Is at max supply"),
}

sealed class Validator(val errorType: ErrorType) {
    abstract fun hasError(): Boolean

    class CheckEntityBlocksPath(val gameMapApi: GameMapApi, val entity: StagingEntity) :
        Validator(ErrorType.BLOCKS_PATH) {
        override fun hasError(): Boolean {
            return gameMapApi.checkNewEntitiesBlocksPath(entity)
        }
    }

    class CheckIntersectsBlockingEntities(val gameMapApi: GameMapApi, val entity: StagingEntity) :
        Validator(ErrorType.INTERSECTS_BLOCKING_ENTITIES) {
        override fun hasError(): Boolean {
            return gameMapApi.checkNewEntityIntersectsExistingBlockingEntities(entity)
        }
    }

    class CheckIsAtMaxSupply(val gameMapApi: GameMapApi) : Validator(ErrorType.MAX_SUPPLY) {
        override fun hasError(): Boolean {
            return gameMapApi.isAtMaxSupply()
        }
    }
}

sealed class RestrictionResult {
    data class Errors(
        val errors: List<ErrorType>,
    ) : RestrictionResult() {}

    object Ok : RestrictionResult()
}

sealed class ValidationResult {
    data class Errors(val errors: List<ErrorType>) : ValidationResult() {
        init {
            require(errors.isNotEmpty())
        }
        val firstErrorShortString get() = errors.first().shortString
    }
    object Ok : ValidationResult()
}

fun validate(vararg validators: Validator): ValidationResult {
    val errors =
        validators.mapNotNull {
            if (it.hasError()) {
                it.errorType
            } else {
                null
            }
        }
    return if (errors.isEmpty()) ValidationResult.Ok else ValidationResult.Errors(errors)
}

fun checkCanPlaceEntity(gameMapApi: GameMapApi, entity: StagingEntity): ValidationResult {
    return validate(
        Validator.CheckEntityBlocksPath(gameMapApi, entity),
        Validator.CheckIntersectsBlockingEntities(gameMapApi, entity)
    )
}

fun checkCanPlaceTowerEntity(gameMapApi: GameMapApi, entity: StagingEntity): ValidationResult {
    return when (val it = checkCanPlaceEntity(gameMapApi, entity)) {
        is ValidationResult.Errors -> it
        ValidationResult.Ok -> {
            validate(Validator.CheckIsAtMaxSupply(gameMapApi))
        }
    }
}
