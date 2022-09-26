package com.xenotactic.gamelogic.random

import com.soywiz.klogger.Logger
import com.xenotactic.gamelogic.globals.GAME_HEIGHT
import com.xenotactic.gamelogic.globals.GAME_WIDTH
import com.xenotactic.gamelogic.model.*
import com.xenotactic.gamelogic.model.TeleportPair
import pathing.AStarSearcher
import pathing.PathFinder
import com.xenotactic.gamelogic.pathing.SearcherInterface
import kotlin.random.Random

data class MapGeneratorConfiguration(
    val seed: Long,
    val width: Int = GAME_WIDTH,
    val height: Int = GAME_HEIGHT,
    val checkpoints: Int = 0,
    val rocks: Int = 0,
    val teleports: Int = 0,
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

class RandomMapGenerator {
    private val map: GameMap
    private val random: Random
    private val config: MapGeneratorConfiguration

    companion object {
        /**
         * Returns a pair where the first value is a boolean representing whether the generator was
         * successful or not and the second value is the game map.
         *
         * Depending on whether or not `returnMapAsIsOnFailure` is true, the map may be null.
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

        val start = createEntity(MapEntity.Start(0, 0))
        map.placeEntity(start)

        var finish: MapEntity
        do {
            numTotalAttempts++
            if (numTotalAttempts >= config.failureAfterTotalAttempts) {
                return failure("Failed to create FINISH entity in a spot that didn't intersect with START.")
            }
            finish = createEntity(MapEntity.Finish(0, 0))
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
            val attemptedPlacementPoints = mutableSetOf<IntPoint>()
            do {
                numTotalAttempts++
                if (numTotalAttempts >= config.failureAfterTotalAttempts) {
                    return failure("Failed to place TELEPORT OUT $i. Attempted points: $attemptedPlacementPoints")
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
                ) == null
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
                rock = MapEntity.Rock(
                    getRandomPointWithinMapBounds(rockType), rockType.width,
                    rockType.height
                )
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

        return MapGeneratorResult.Success(map)
    }


    fun createEntity(entityType: MapEntity): MapEntity {
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
        }
    }

    fun getRandomPointWithinMapBounds(entityType: MapEntity): IntPoint {
        return IntPoint(
            random.nextInt(0, map.width - entityType.width + 1),
            random.nextInt(0, map.height - entityType.height + 1)
        )
    }

}

