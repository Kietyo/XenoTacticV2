package com.xenotactic.gamelogic.utils

import com.xenotactic.ecs.StagingEntity
import com.xenotactic.gamelogic.components.EntityCostComponent
import com.xenotactic.gamelogic.components.SupplyCostComponent
import com.xenotactic.gamelogic.model.GameWorld
import com.xenotactic.gamelogic.state.MutableGoldState

enum class ErrorType(val description: String, val shortString: String) {
    NONE("No errors", "No errors"),
    BLOCKS_PATH("Placing entity blocks the path.", "Blocks path"),
    INTERSECTS_BLOCKING_ENTITIES(
        "Placing entity intersects with other blocking entities.",
        "Intersects with another entity"
    ),
    MAX_SUPPLY("Unable to place because already at max supply.", "Is at max supply"),
    NOT_ENOUGH_GOLD("Unable to place because there's not enough gold", "Not enough gold."),
    NOT_ENOUGH_SUPPLY("Unable to place because there's not enough supply.", "Not enough supply")
}

sealed class ValidationResult {
    data class Errors(val errors: List<ErrorType>) : ValidationResult() {
        init {
            require(errors.isNotEmpty())
        }
        val firstErrorShortString
            get() = errors.first().shortString
    }
    object Ok : ValidationResult()
}

sealed class ValidatorTypes(val errorType: ErrorType) {
    abstract fun hasError(): Boolean

    object NoError : ValidatorTypes(ErrorType.NONE) {
        override fun hasError(): Boolean = false
    }

    data class CheckEntityBlocksPath(val gameMapApi: GameMapApi, val entity: StagingEntity) :
        ValidatorTypes(ErrorType.BLOCKS_PATH) {
        override fun hasError(): Boolean {
            return gameMapApi.checkNewEntitiesBlocksPath(entity)
        }
    }

    data class CheckIntersectsBlockingEntities(
        val gameMapApi: GameMapApi,
        val entity: StagingEntity
    ) : ValidatorTypes(ErrorType.INTERSECTS_BLOCKING_ENTITIES) {
        override fun hasError(): Boolean {
            return gameMapApi.checkNewEntityIntersectsExistingBlockingEntities(entity)
        }
    }

    data class CheckIsAtMaxSupply(val gameMapApi: GameMapApi) :
        ValidatorTypes(ErrorType.MAX_SUPPLY) {
        override fun hasError(): Boolean {
            return gameMapApi.isAtMaxSupply()
        }
    }

    data class CheckNotEnoughGold(
        val mutableGoldState: MutableGoldState,
        val entity: StagingEntity
    ) : ValidatorTypes(ErrorType.NOT_ENOUGH_GOLD) {
        override fun hasError(): Boolean {
            val cost = entity.get(EntityCostComponent::class)
            return cost.cost > mutableGoldState.currentGold
        }
    }

    data class CheckNotEnoughSupply(
        val gameWorld: GameWorld,
        val stateUtils: StateUtils,
        val entity: StagingEntity
    ) : ValidatorTypes(ErrorType.NOT_ENOUGH_SUPPLY) {
        override fun hasError(): Boolean {
            val supplyCostComponent = entity.get(SupplyCostComponent::class)
            val currentSupplyUsage = gameWorld.currentSupplyUsage
            val currentMaxSupplyCost = stateUtils.currentMaxSupply
            return (supplyCostComponent.cost + currentSupplyUsage) > currentMaxSupplyCost
        }
    }
}

class Validator(val engine: Engine, val entity: StagingEntity) {
    val gameMapApi = engine.injections.getSingleton<GameMapApi>()
    val stateUtils = StateUtils(engine)

    private fun getChecker(errorType: ErrorType): ValidatorTypes {
        return when (errorType) {
            ErrorType.NONE -> ValidatorTypes.NoError
            ErrorType.BLOCKS_PATH -> ValidatorTypes.CheckEntityBlocksPath(gameMapApi, entity)
            ErrorType.INTERSECTS_BLOCKING_ENTITIES ->
                ValidatorTypes.CheckIntersectsBlockingEntities(gameMapApi, entity)
            ErrorType.MAX_SUPPLY -> ValidatorTypes.CheckIsAtMaxSupply(gameMapApi)
            ErrorType.NOT_ENOUGH_GOLD ->
                ValidatorTypes.CheckNotEnoughGold(
                    engine.stateInjections.getSingleton<MutableGoldState>(),
                    entity
                )
            ErrorType.NOT_ENOUGH_SUPPLY ->
                ValidatorTypes.CheckNotEnoughSupply(engine.gameWorld, stateUtils, entity)
        }
    }

    fun validate(errorsToValidate: Iterable<ErrorType>): ValidationResult {
        val errors =
            errorsToValidate.mapNotNull {
                val checker = getChecker(it)
                if (checker.hasError()) {
                    checker.errorType
                } else {
                    null
                }
            }
        return if (errors.isEmpty()) ValidationResult.Ok else ValidationResult.Errors(errors)
    }
}

val PLACEMENT_ERRORS = listOf(ErrorType.BLOCKS_PATH, ErrorType.INTERSECTS_BLOCKING_ENTITIES)

fun checkCanPlaceEntity(engine: Engine, entity: StagingEntity): ValidationResult {
    val validator = Validator(engine, entity)
    return validator.validate(PLACEMENT_ERRORS)
}

fun checkCanPlaceTowerEntity(engine: Engine, entity: StagingEntity): ValidationResult {
    val validator = Validator(engine, entity)
    return validator.validate(PLACEMENT_ERRORS + ErrorType.MAX_SUPPLY + ErrorType.NOT_ENOUGH_GOLD)
}

fun checkCanPlaceSupplyDepotEntity(engine: Engine, entity: StagingEntity): ValidationResult {
    val validator = Validator(engine, entity)
    return validator.validate(PLACEMENT_ERRORS + ErrorType.NOT_ENOUGH_GOLD)
}
