package com.xenotactic.korge.random

import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.GameWorld
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.pathing.SearcherInterface
import com.xenotactic.gamelogic.utils.GAME_HEIGHT
import com.xenotactic.gamelogic.utils.GAME_WIDTH
import com.xenotactic.gamelogic.utils.GameUnit
import korlibs.logger.Logger
import pathing.AStarSearcher
import kotlin.random.Random

class RandomMapGeneratorMaxAttemptsError(
    messageFn: () -> String
) : RuntimeException(messageFn())

data class MapGeneratorConfigurationV2(
    val seed: Long,
    // A list of generators for generating various entities in the map.
    // ORDER DOES MATTER!
    val generators: List<IGenerator>,
    val width: GameUnit = GAME_WIDTH,
    val height: GameUnit = GAME_HEIGHT,
    val searcher: SearcherInterface = AStarSearcher,
    val failureAfterTotalAttempts: Int = 2000
)

data class GenerationContext(
    val width: GameUnit,
    val height: GameUnit,
    val gameWorld: GameWorld,
    val random: Random,
    val failureAfterTotalAttempts: Int
) {
    val world = gameWorld.world
    var numTotalAttempts = 0
        private set

    fun getSizeOfEntity(entityType: MapEntityType): GameUnitTuple {
        return when (entityType) {
            MapEntityType.START -> GameUnitTuple(2, 2)
            MapEntityType.FINISH -> GameUnitTuple(2, 2)
            MapEntityType.CHECKPOINT -> GameUnitTuple(2, 2)
            MapEntityType.ROCK -> TODO()
            MapEntityType.TOWER -> GameUnitTuple(2, 2)
            MapEntityType.TELEPORT_IN -> GameUnitTuple(2, 2)
            MapEntityType.TELEPORT_OUT -> GameUnitTuple(2, 2)
            MapEntityType.SMALL_BLOCKER -> GameUnitTuple(1, 1)
            MapEntityType.SPEED_AREA -> TODO()
            MapEntityType.MONSTER -> TODO()
            MapEntityType.SUPPLY_DEPOT -> GameUnitTuple(2, 2)
        }
    }

    fun incrementNumAttempts(errorMessageFn: () -> String) {
        numTotalAttempts++
        if (numTotalAttempts > failureAfterTotalAttempts) {
            throw RandomMapGeneratorMaxAttemptsError(errorMessageFn)
        }
    }

    fun getRandomPointWithinMapBounds(entitySize: GameUnitTuple): GameUnitTuple {
        return getRandomPointWithinMapBounds(entitySize.first, entitySize.second)
    }

    private fun getRandomPointWithinMapBounds(entityWidth: GameUnit, entityHeight: GameUnit): GameUnitTuple {
        return GameUnitTuple(
            random.nextInt(0, width.toInt() - entityWidth.toInt() + 1),
            random.nextInt(0, height.toInt() - entityHeight.toInt() + 1)
        )
    }

    fun getRandomPointPartiallyInMap(entityWidth: GameUnit, entityHeight: GameUnit): GameUnitTuple {
        return GameUnitTuple(
            random.nextInt(-entityWidth.toInt() + 1, width.toInt() - 1),
            random.nextInt(-entityHeight.toInt() + 1, height.toInt() - 1)
        )
    }
}

interface IGenerator {
    fun run(context: GenerationContext)
}

sealed class MapGeneratorResultV2 {
    abstract val width: GameUnit
    abstract val height: GameUnit
    abstract val gameWorld: GameWorld

    val world get() = gameWorld.world

    data class Success(
        override val width: GameUnit,
        override val height: GameUnit,
        override val gameWorld: GameWorld
    ) : MapGeneratorResultV2()

    data class Failure(
        override val width: GameUnit,
        override val height: GameUnit,
        override val gameWorld: GameWorld,
        val errors: List<String>
    ) : MapGeneratorResultV2()
}

class RandomMapGeneratorV2 private constructor(private val config: MapGeneratorConfigurationV2) {
    private val gameWorld: GameWorld = GameWorld()
    private val random: Random = Random(config.seed)
    private val context: GenerationContext =
        GenerationContext(config.width, config.height, gameWorld, random, config.failureAfterTotalAttempts)

    companion object {
        /**
         * Returns a pair where the first value is a boolean representing whether the generator was
         * successful or not and the second value is the game map.
         *
         * Depending on whether `returnMapAsIsOnFailure` is true, the map may be null.
         */
        fun generate(config: MapGeneratorConfigurationV2): MapGeneratorResultV2 {
            logger.info { "Generating random map for config: $config" }
            return RandomMapGeneratorV2(config).generateInternal()
        }

        val logger = Logger<RandomMapGeneratorV2>()
    }

    private fun failure(errorString: String): MapGeneratorResultV2.Failure {
        return MapGeneratorResultV2.Failure(config.width, config.height, gameWorld, listOf(errorString))
    }

    fun generateInternal(): MapGeneratorResultV2 {
        for (it in config.generators) {
            try {
                it.run(context)
            } catch (e: RandomMapGeneratorMaxAttemptsError) {
                return MapGeneratorResultV2.Failure(
                    config.width, config.height, gameWorld, listOf(e.message!!)
                )
            }
        }

        //        var currentPath = PathFinder.getShortestPath(gameWorld, config.searcher)!!
        //        var attemptNum = 0
        //        val addedRocks = mutableListOf<MapEntity.Rock>()
        //        for (i in 0 until config.rocks) {
        //            var rock: MapEntity.Rock
        //            var newRockList: List<MapEntity.Rock>
        //
        //            // Keep generating candidates for the rock
        //            do {
        //                numTotalAttempts++
        //                attemptNum++
        //                //                if (attemptNum > 100) {
        //                //                    println(
        //                //                        """
        //                //                        start: $start,
        //                //                        finish: $finish,
        //                //                        addedCheckPoints: $addedCheckPoints,
        //                //                        addedTpOut: $addedTpOut,
        //                //                        addedRocks: $addedRocks
        //                //                    """.trimIndent()
        //                //                    )
        //                //                }
        //                if (numTotalAttempts >= config.failureAfterTotalAttempts) {
        //                    return failure("Failed to create place ROCK $i.")
        //                }
        //                val rockType = if (random.nextBoolean()) MapEntity.ROCK_2X4 else MapEntity.ROCK_4X2
        //                val (rockX, rockY) = getRandomPointWithinMapBounds(rockType)
        //                rock = rockType.copy(x = rockX, y = rockY)
        //                newRockList = addedRocks + rock
        //                if (
        //                    start.isFullyCoveredBy(newRockList) ||
        //                    finish.isFullyCoveredBy(newRockList) ||
        //                    addedCheckpoints.any { it.isFullyCoveredBy(newRockList) } ||
        //                    // TODO: This is not enough, rocks still get placed on top of tp in/outs
        //                    addedTpIns.any { it.isFullyCoveredBy(newRockList) } ||
        //                    addedTpOuts.any { it.isFullyCoveredBy(newRockList) }
        //                ) {
        //                    continue
        //                }
        //
        //                if (currentPath.intersectsRectangle(rock.getRectangle())
        //                ) {
        //                    val possibleNewPath = PathFinder.getShortestPathWithBlockingEntities(
        //                        gameWorld,
        //                        listOf(rock)
        //                    ) ?: continue
        //                    currentPath = possibleNewPath
        //                }
        //                break
        //            } while (true)
        //            addedRocks.add(rock)
        //            gameWorld.placeEntity(rock)
        //        }
        //
        //        repeat(config.speedAreas) {
        //            val radius = random.nextInt(1, 11)
        //            val diameter = radius * 2
        //            val speedEffect = random.nextDouble(0.25, 1.90)
        //
        //            val (speedX, speedY) = getRandomPointPartiallyInMap(diameter, diameter)
        //            gameWorld.placeEntity(MapEntity.SpeedArea(speedX, speedY, radius.toGameUnit(), speedEffect))
        //        }

        return MapGeneratorResultV2.Success(config.width, config.height, gameWorld)
    }

    //    fun createEntity(entityType: MapEntity): MapEntity {
    //        return when (entityType) {
    //            is MapEntity.Start -> MapEntity.Start(getRandomPointWithinMapBounds(entityType))
    //            is MapEntity.Finish -> MapEntity.Finish(getRandomPointWithinMapBounds(entityType))
    //            is MapEntity.Checkpoint -> TODO()
    //            is MapEntity.Rock -> TODO()
    //            is MapEntity.Tower -> TODO()
    //            is MapEntity.TeleportIn -> TODO()
    //            is MapEntity.TeleportOut -> TODO()
    //            is MapEntity.SmallBlocker -> TODO()
    //            is MapEntity.SpeedArea -> TODO()
    //        }
    //    }

    //    fun getRandomPointPartiallyInMap(entityWidth: Int, entityHeight: Int): GameUnitPoint {
    //        return GameUnitPoint(
    //            random.nextInt(-entityWidth + 1, gameWorld.width.toInt() - 1),
    //            random.nextInt(-entityHeight + 1, gameWorld.height.toInt() - 1)
    //        )
    //    }
    //
    //    fun getRandomPointWithinMapBounds(entityType: MapEntity): GameUnitPoint {
    //        return GameUnitPoint(
    //            random.nextInt(0, gameWorld.width.toInt() - entityType.width.toInt() + 1),
    //            random.nextInt(0, gameWorld.height.toInt() - entityType.height.toInt() + 1)
    //        )
    //    }

}

