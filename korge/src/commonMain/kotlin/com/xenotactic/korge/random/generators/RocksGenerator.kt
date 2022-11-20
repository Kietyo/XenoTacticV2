package com.xenotactic.korge.random.generators

import com.xenotactic.ecs.StatefulEntity
import com.xenotactic.gamelogic.pathing.PathFindingResult
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.random.GenerationContext
import com.xenotactic.korge.random.IGenerator
import pathing.PathFinder

class RocksGenerator(
    val numRocks: Int
) : IGenerator {
    val ROCK_2X4_DIMENSIONS = 2.toGameUnit() to 4.toGameUnit()
    val ROCK_4X2_DIMENSIONS = 4.toGameUnit() to 2.toGameUnit()

    override fun run(context: GenerationContext) {
        val width = context.width
        val height = context.height
        val startEntity = context.gameWorld.startEntity
        val finishEntity = context.gameWorld.finishEntity
        val addedCheckpoints = context.gameWorld.addedCheckpoints

        var currentPath = context.gameWorld.getPathFindingResult(width, height).let {
            require(it is PathFindingResult.Success)
            it.gamePath.toPathSequence()
        }
        val addedRocks = mutableListOf<StatefulEntity>()
        for (i in 0 until numRocks) {
            var rock: MapEntity.Rock
            var newRockList: List<MapEntity.Rock>

            // Keep generating candidates for the rock
            do {
                context.incrementNumAttempts {
                    "Failed to create place ROCK $i."
                }
                val rockDimensions = if (context.random.nextBoolean()) ROCK_2X4_DIMENSIONS else ROCK_4X2_DIMENSIONS
                val (rockX, rockY) = context.getRandomPointWithinMapBounds(rockDimensions)
                rock = rockDimensions.copy(x = rockX, y = rockY)
                newRockList = addedRocks + rock
                if (
                    startEntity.isFullyCoveredBy(newRockList) ||
                    finishEntity.isFullyCoveredBy(newRockList) ||
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
                        gameWorld,
                        listOf(rock)
                    ) ?: continue
                    currentPath = possibleNewPath
                }
                break
            } while (true)
            addedRocks.add(rock)
            gameWorld.placeEntity(rock)
        }
    }

}