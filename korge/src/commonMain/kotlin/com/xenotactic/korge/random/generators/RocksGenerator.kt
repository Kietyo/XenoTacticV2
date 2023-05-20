package com.xenotactic.korge.random.generators

import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.IRectangleEntity
import com.xenotactic.gamelogic.model.RectangleEntity
import com.xenotactic.gamelogic.pathing.PathFindingResult
import com.xenotactic.korge.random.GenerationContext
import com.xenotactic.korge.random.IGenerator
import com.xenotactic.korge.utils.StagingEntityUtils
import com.xenotactic.korge.utils.isFullyCoveredBy

class RocksGenerator(
    val numRocks: Int
) : IGenerator {
    private val ROCK_2X4_DIMENSIONS = GameUnitTuple(2, 4)
    private val ROCK_4X2_DIMENSIONS = GameUnitTuple(4, 2)

    override fun run(context: GenerationContext) {
        val width = context.width
        val height = context.height
        val startEntity = context.gameWorld.startEntity
        val finishEntity = context.gameWorld.finishEntity
        val addedCheckpoints = context.gameWorld.checkpoints
        val addedTpIns = context.gameWorld.teleportIns
        val addedTpOuts = context.gameWorld.teleportOuts

        var currentPath = context.gameWorld.getPathFindingResult(width, height).let {
            require(it is PathFindingResult.Success)
            it.gamePath.toPathSequence()
        }
        val addedRocks = mutableListOf<IRectangleEntity>()
        for (i in 0 until numRocks) {
            var rock: IRectangleEntity

            // Keep generating candidates for the rock
            do {
                context.incrementNumAttempts {
                    "Failed to create place ROCK $i."
                }
                val rockDimensions = if (context.random.nextBoolean()) ROCK_2X4_DIMENSIONS else ROCK_4X2_DIMENSIONS
                val rockPosition = context.getRandomPointWithinMapBounds(rockDimensions)
                rock = RectangleEntity(
                    rockPosition.x, rockPosition.y,
                    rockDimensions.first, rockDimensions.second
                )
                val newRockList: List<IRectangleEntity> = addedRocks + rock
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

                if (currentPath.intersectsRectangle(rock.getRectangle())) {
                    val possibleNewPath = context.gameWorld.getPathFindingResult(
                        width, height, blockingEntities = listOf(rock)
                    )
                    when (possibleNewPath) {
                        is PathFindingResult.Failure -> continue
                        is PathFindingResult.Success -> currentPath = possibleNewPath.gamePath.toPathSequence()
                    }
                }
                break
            } while (true)
            addedRocks.add(rock)
            context.world.addEntity {
                addComponentsFromStagingEntity(StagingEntityUtils.createRock(rock))
            }
        }
    }

}

