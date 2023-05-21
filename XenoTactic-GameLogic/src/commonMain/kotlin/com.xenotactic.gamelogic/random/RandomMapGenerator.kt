package com.xenotactic.gamelogic.random

import korlibs.logger.Logger
import com.xenotactic.gamelogic.utils.GAME_HEIGHT
import com.xenotactic.gamelogic.utils.GAME_WIDTH
import com.xenotactic.gamelogic.model.*
import com.xenotactic.gamelogic.pathing.PathFindingResult
import com.xenotactic.gamelogic.pathing.SearcherInterface
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit
import pathing.AStarSearcher
import pathing.PathFinder
import kotlin.random.Random

data class MapGeneratorConfiguration(
    val seed: Long,
    val width: GameUnit = GAME_WIDTH,
    val height: GameUnit = GAME_HEIGHT,
    val checkpoints: Int = 0,
    val rocks: Int = 0,
    val teleports: Int = 0,
    val speedAreas: Int = 0,
    val mineralSpots: Int = 0,
    val searcher: SearcherInterface = AStarSearcher,
    val failureAfterTotalAttempts: Int = 2000
)

sealed class MapGeneratorResult {
    abstract val map: GameMap

    data class Success(
        override val map: GameMap
    ) : MapGeneratorResult()

    data class Failure(
        override val map: GameMap,
        val errors: List<String>
    ) : MapGeneratorResult()
}

@Deprecated("Please use RandomMapGeneratorV2 instead.")
class RandomMapGenerator {
    private val map: GameMap
    private val random: Random
    private val config: MapGeneratorConfiguration

    companion object {
        /**
         * Returns a pair where the first value is a boolean representing whether the generator was
         * successful or not and the second value is the game map.
         *
         * Depending on whether `returnMapAsIsOnFailure` is true, the map may be null.
         */
        fun generate(config: MapGeneratorConfiguration): MapGeneratorResult {
            logger.info { "Generating random map for config: $config" }
            return RandomMapGenerator(config).generateInternal()
        }

        val logger = Logger<RandomMapGenerator>()
    }

    private constructor(config: MapGeneratorConfiguration) {
        this.map = GameMap(config.width, config.height)
        this.config = config
        this.random = Random(config.seed)
    }

    private fun failure(errorString: String): MapGeneratorResult.Failure {
        return MapGeneratorResult.Failure(map, listOf(errorString))
    }

    fun generateInternal(): MapGeneratorResult {
        var numTotalAttempts = 0

        val start = createEntity(MapEntity.Start(0.toGameUnit(), 0.toGameUnit()))
        map.placeEntity(start)

        var finish: MapEntity
        do {
            numTotalAttempts++
            if (numTotalAttempts >= config.failureAfterTotalAttempts) {
                return failure("Failed to create FINISH entity in a spot that didn't intersect with START.")
            }
            finish = createEntity(MapEntity.Finish(0.toGameUnit(), 0.toGameUnit()))
        } while (start.intersectsEntity(finish))
        map.placeEntity(finish)

        val addedCheckpoints = mutableListOf<MapEntity.Checkpoint>()
        for (i in 0 until config.checkpoints) {
            var checkpoint: MapEntity.Checkpoint
            do {
                numTotalAttempts++
                if (numTotalAttempts >= config.failureAfterTotalAttempts) {
                    return failure("Failed to create place CHECKPOINT $i.")
                }
                checkpoint =
                    MapEntity.Checkpoint(i, getRandomPointWithinMapBounds(MapEntity.CHECKPOINT))
            } while (
                start.intersectsEntity(checkpoint) ||
                finish.intersectsEntity(checkpoint) ||
                addedCheckpoints.any { it.intersectsEntity(checkpoint) }
            )
            addedCheckpoints.add(checkpoint)
            map.placeEntity(checkpoint)
        }

        val addedTpIns = mutableListOf<MapEntity.TeleportIn>()
        val addedTpOuts = mutableListOf<MapEntity.TeleportOut>()

        for (i in 0 until config.teleports) {
            var teleportIn: MapEntity.TeleportIn
            do {
                numTotalAttempts++
                if (numTotalAttempts >= config.failureAfterTotalAttempts) {
                    return failure("Failed to create place TELEPORT IN $i.")
                }
                teleportIn =
                    MapEntity.TeleportIn(i, getRandomPointWithinMapBounds(MapEntity.TELEPORT_IN))
            } while (
                start.intersectsEntity(teleportIn) ||
                finish.intersectsEntity(teleportIn) ||
                addedCheckpoints.any { it.intersectsEntity(teleportIn) } ||
                addedTpIns.any { it.intersectsEntity(teleportIn) } ||
                addedTpOuts.any { it.intersectsEntity(teleportIn) }
            )
            addedTpIns.add(teleportIn)

            var teleportOut: MapEntity.TeleportOut
            val attemptedPlacementPoints = mutableSetOf<GameUnitTuple>()
            do {
                numTotalAttempts++
                if (numTotalAttempts >= config.failureAfterTotalAttempts) {
                    return failure("""
                        Failed to place TELEPORT OUT $i.
                        stagedTeleportIn: $teleportIn
                        Attempted points: $attemptedPlacementPoints
                    """.trimIndent())
                }
                val randomPoint = getRandomPointWithinMapBounds(MapEntity.TELEPORT_OUT)
                teleportOut =
                    MapEntity.TeleportOut(i, randomPoint)
                attemptedPlacementPoints.add(randomPoint)
            } while (
                start.intersectsEntity(teleportOut) ||
                finish.intersectsEntity(teleportOut) ||
                addedTpIns.any { it.intersectsEntity(teleportOut) } ||
                addedCheckpoints.any { it.intersectsEntity(teleportOut) } ||
                PathFinder.getShortestPathWithTeleportPair(
                    map, TeleportPair(
                        teleportIn,
                        teleportOut,
                        teleportIn.sequenceNumber
                    )
                ) is PathFindingResult.Success
            )
            addedTpOuts.add(teleportOut)

            map.placeEntity(teleportIn)
            map.placeEntity(teleportOut)
        }

        var currentPath = PathFinder.getShortestPath(map, config.searcher)!!
        var attemptNum = 0
        val addedRocks = mutableListOf<MapEntity.Rock>()
        for (i in 0 until config.rocks) {
            var rock: MapEntity.Rock
            var newRockList: List<MapEntity.Rock>

            // Keep generating candidates for the rock
            do {
                numTotalAttempts++
                attemptNum++
                //                if (attemptNum > 100) {
                //                    println(
                //                        """
                //                        start: $start,
                //                        finish: $finish,
                //                        addedCheckPoints: $addedCheckPoints,
                //                        addedTpOut: $addedTpOut,
                //                        addedRocks: $addedRocks
                //                    """.trimIndent()
                //                    )
                //                }
                if (numTotalAttempts >= config.failureAfterTotalAttempts) {
                    return failure("Failed to create place ROCK $i.")
                }
                val rockType = if (random.nextBoolean()) MapEntity.ROCK_2X4 else MapEntity.ROCK_4X2
                val (rockX, rockY) = getRandomPointWithinMapBounds(rockType)
                rock = rockType.copy(x = rockX, y = rockY)
                newRockList = addedRocks + rock
                if (
                    start.isFullyCoveredBy(newRockList) ||
                    finish.isFullyCoveredBy(newRockList) ||
                    addedCheckpoints.any { it.isFullyCoveredBy(newRockList) } ||
                    // TODO: This is not enough, rocks still get placed on top of tp in/outs
                    addedTpIns.any { it.isFullyCoveredBy(newRockList) } ||
                    addedTpOuts.any { it.isFullyCoveredBy(newRockList) }
                ) {
                    continue
                }

                if (currentPath.intersectsRectangle(rock.getRectangle())
                ) {
                    val possibleNewPath = PathFinder.getShortestPathWithBlockingEntities(
                        map,
                        listOf(rock)
                    ) ?: continue
                    currentPath = possibleNewPath
                }
                break
            } while (true)
            addedRocks.add(rock)
            map.placeEntity(rock)
        }

        repeat(config.speedAreas) {
            val radius = random.nextInt(1, 11)
            val diameter = radius * 2
            val speedEffect = random.nextDouble(0.25, 1.90)

            val (speedX, speedY) = getRandomPointPartiallyInMap(diameter, diameter)
            map.placeEntity(MapEntity.SpeedArea(speedX, speedY, radius.toGameUnit(), speedEffect))
        }

        return MapGeneratorResult.Success(map)
    }


    private fun createEntity(entityType: MapEntity): MapEntity {
        return when (entityType) {
            is MapEntity.Start -> MapEntity.Start(getRandomPointWithinMapBounds(entityType))
            is MapEntity.Finish -> MapEntity.Finish(getRandomPointWithinMapBounds(entityType))
            is MapEntity.Checkpoint -> TODO()
            is MapEntity.Rock -> TODO()
            is MapEntity.Tower -> TODO()
            is MapEntity.TeleportIn -> TODO()
            is MapEntity.TeleportOut -> TODO()
            is MapEntity.SmallBlocker -> TODO()
            is MapEntity.SpeedArea -> TODO()
            is MapEntity.SupplyDepot -> TODO()
        }
    }

    private fun getRandomPointPartiallyInMap(entityWidth: Int, entityHeight: Int): GameUnitTuple {
        return GameUnitTuple(
            random.nextInt(-entityWidth + 1, map.width.toInt() - 1),
            random.nextInt(-entityHeight + 1, map.height.toInt() - 1)
        )
    }

    fun getRandomPointWithinMapBounds(entityType: MapEntity): GameUnitTuple {
        return GameUnitTuple(
            random.nextInt(0, map.width.toInt() - entityType.width.toInt() + 1),
            random.nextInt(0, map.height.toInt() - entityType.height.toInt() + 1)
        )
    }

}

