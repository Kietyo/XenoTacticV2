package com.xenotactic.korge.random

import com.soywiz.klogger.Logger
import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.StatefulEntity
import com.xenotactic.gamelogic.globals.GAME_HEIGHT
import com.xenotactic.gamelogic.globals.GAME_WIDTH
import com.xenotactic.gamelogic.model.*
import pathing.AStarSearcher
import com.xenotactic.gamelogic.pathing.SearcherInterface
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.intersectRectangles
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.components.BottomLeftPositionComponent
import com.xenotactic.korge.components.EntityFinishComponent
import com.xenotactic.korge.components.EntityStartComponent
import com.xenotactic.korge.components.SizeComponent
import com.xenotactic.korge.models.GameWorld
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

    fun getSizeOfEntity(entityType: MapEntityType): Pair<GameUnit, GameUnit> {
        return when (entityType) {
            MapEntityType.START -> 2.toGameUnit() to 2.toGameUnit()
            MapEntityType.FINISH -> 2.toGameUnit() to 2.toGameUnit()
            MapEntityType.CHECKPOINT -> 2.toGameUnit() to 2.toGameUnit()
            MapEntityType.ROCK -> TODO()
            MapEntityType.TOWER -> 2.toGameUnit() to 2.toGameUnit()
            MapEntityType.TELEPORT_IN -> 2.toGameUnit() to 2.toGameUnit()
            MapEntityType.TELEPORT_OUT -> 2.toGameUnit() to 2.toGameUnit()
            MapEntityType.SMALL_BLOCKER -> 1.toGameUnit() to 1.toGameUnit()
            MapEntityType.SPEED_AREA -> TODO()
            MapEntityType.MONSTER -> TODO()
        }
    }

    fun incrementNumAttempts(errorMessageFn: () -> String) {
        numTotalAttempts++
        if (numTotalAttempts > failureAfterTotalAttempts) {
            throw RandomMapGeneratorMaxAttemptsError(errorMessageFn)
        }
    }

    fun getRandomPointWithinMapBounds(entitySize: Pair<GameUnit, GameUnit>): GameUnitPoint {
        return getRandomPointWithinMapBounds(entitySize.first, entitySize.second)
    }

    fun getRandomPointWithinMapBounds(entityWidth: GameUnit, entityHeight: GameUnit): GameUnitPoint {
        return GameUnitPoint(
            random.nextInt(0, width.toInt() - entityWidth.toInt() + 1),
            random.nextInt(0, height.toInt() - entityHeight.toInt() + 1)
        )
    }
}

interface IGenerator {
    fun run(context: GenerationContext)
}


object StartGenerator : IGenerator {
    override fun run(context: GenerationContext) {
        val size = context.getSizeOfEntity(MapEntityType.START)
        val point = context.getRandomPointWithinMapBounds(size)

        context.world.addEntity {
            addComponentOrThrow(size.toSizeComponent())
            addComponentOrThrow(point.toBottomLeftPositionComponent())
            addComponentOrThrow(EntityStartComponent)
        }
    }
}

object FinishGenerator : IGenerator {
    override fun run(context: GenerationContext) {
        val startEntity = context.world.getFirstStatefulEntityMatching(
            FamilyConfiguration.allOf(
                EntityStartComponent::class
            )
        )
        val size = context.getSizeOfEntity(MapEntityType.FINISH)
        var point: GameUnitPoint
        do {
            context.incrementNumAttempts {
                "Failed to create FINISH entity in a spot that didn't intersect with START."
            }
            point = context.getRandomPointWithinMapBounds(size)
        } while (startEntity.intersectsEntity(point, size))

        context.world.addEntity {
            addComponentOrThrow(size.toSizeComponent())
            addComponentOrThrow(point.toBottomLeftPositionComponent())
            addComponentOrThrow(EntityFinishComponent)
        }
    }
}

private fun StatefulEntity.intersectsEntity(position: GameUnitPoint, size: Pair<GameUnit, GameUnit>): Boolean {
    val thisPosition = get(BottomLeftPositionComponent::class)
    val thisSize = get(SizeComponent::class)
    return intersectRectangles(
        thisPosition.x.toDouble(),
        thisPosition.y.toDouble(),
        thisSize.width.toDouble(),
        thisSize.height.toDouble(),
        position.x.toDouble(),
        position.y.toDouble(),
        size.first.toDouble(),
        size.second.toDouble()
    )
}

private fun GameUnitPoint.toBottomLeftPositionComponent(): BottomLeftPositionComponent {
    return BottomLeftPositionComponent(x, y)
}

private fun Pair<GameUnit, GameUnit>.toSizeComponent(): SizeComponent {
    return SizeComponent(first, second)
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

class RandomMapGeneratorV2 {
    private val gameWorld: GameWorld = GameWorld()
    private val random: Random
    private val config: MapGeneratorConfigurationV2
    private val context: GenerationContext

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

    private constructor(config: MapGeneratorConfigurationV2) {
        this.config = config
        this.random = Random(config.seed)
        context = GenerationContext(config.width, config.height, gameWorld, random, config.failureAfterTotalAttempts)
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

//        var finish: MapEntity
//        do {
//            numTotalAttempts++
//            if (numTotalAttempts >= config.failureAfterTotalAttempts) {
//                return failure("Failed to create FINISH entity in a spot that didn't intersect with START.")
//            }
//            finish = createEntity(MapEntity.Finish(0.toGameUnit(), 0.toGameUnit()))
//        } while (start.intersectsEntity(finish))
//        gameWorld.placeEntity(finish)
//
//        val addedCheckpoints = mutableListOf<MapEntity.Checkpoint>()
//        for (i in 0 until config.checkpoints) {
//            var checkpoint: MapEntity.Checkpoint
//            do {
//                numTotalAttempts++
//                if (numTotalAttempts >= config.failureAfterTotalAttempts) {
//                    return failure("Failed to create place CHECKPOINT $i.")
//                }
//                checkpoint =
//                    MapEntity.Checkpoint(i, getRandomPointWithinMapBounds(MapEntity.CHECKPOINT))
//            } while (
//                start.intersectsEntity(checkpoint) ||
//                finish.intersectsEntity(checkpoint) ||
//                addedCheckpoints.any { it.intersectsEntity(checkpoint) }
//            )
//            addedCheckpoints.add(checkpoint)
//            gameWorld.placeEntity(checkpoint)
//        }
//
//        val addedTpIns = mutableListOf<MapEntity.TeleportIn>()
//        val addedTpOuts = mutableListOf<MapEntity.TeleportOut>()
//
//        for (i in 0 until config.teleports) {
//            var teleportIn: MapEntity.TeleportIn
//            do {
//                numTotalAttempts++
//                if (numTotalAttempts >= config.failureAfterTotalAttempts) {
//                    return failure("Failed to create place TELEPORT IN $i.")
//                }
//                teleportIn =
//                    MapEntity.TeleportIn(i, getRandomPointWithinMapBounds(MapEntity.TELEPORT_IN))
//            } while (
//                start.intersectsEntity(teleportIn) ||
//                finish.intersectsEntity(teleportIn) ||
//                addedCheckpoints.any { it.intersectsEntity(teleportIn) } ||
//                addedTpIns.any { it.intersectsEntity(teleportIn) } ||
//                addedTpOuts.any { it.intersectsEntity(teleportIn) }
//            )
//            addedTpIns.add(teleportIn)
//
//            var teleportOut: MapEntity.TeleportOut
//            val attemptedPlacementPoints = mutableSetOf<GameUnitPoint>()
//            do {
//                numTotalAttempts++
//                if (numTotalAttempts >= config.failureAfterTotalAttempts) {
//                    return failure("""
//                        Failed to place TELEPORT OUT $i.
//                        stagedTeleportIn: $teleportIn
//                        Attempted points: $attemptedPlacementPoints
//                    """.trimIndent())
//                }
//                val randomPoint = getRandomPointWithinMapBounds(MapEntity.TELEPORT_OUT)
//                teleportOut =
//                    MapEntity.TeleportOut(i, randomPoint)
//                attemptedPlacementPoints.add(randomPoint)
//            } while (
//                start.intersectsEntity(teleportOut) ||
//                finish.intersectsEntity(teleportOut) ||
//                addedTpIns.any { it.intersectsEntity(teleportOut) } ||
//                addedCheckpoints.any { it.intersectsEntity(teleportOut) } ||
//                PathFinder.getShortestPathWithTeleportPair(
//                    gameWorld, TeleportPair(
//                        teleportIn,
//                        teleportOut,
//                        teleportIn.sequenceNumber
//                    )
//                ) is PathFindingResult.Success
//            )
//            addedTpOuts.add(teleportOut)
//
//            gameWorld.placeEntity(teleportIn)
//            gameWorld.placeEntity(teleportOut)
//        }
//
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

