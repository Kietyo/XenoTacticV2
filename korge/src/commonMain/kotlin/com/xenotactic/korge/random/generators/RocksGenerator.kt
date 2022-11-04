package com.xenotactic.korge.random.generators

import com.xenotactic.ecs.StatefulEntity
import com.xenotactic.korge.random.GenerationContext
import com.xenotactic.korge.random.IGenerator
import pathing.PathFinder

class RocksGenerator(
    val numRocks: Int
) : IGenerator {
    override fun run(context: GenerationContext) {
        var currentPath = PathFinder.getShortestPath(gameWorld)!!
        var attemptNum = 0
        val addedRocks = mutableListOf<StatefulEntity>()
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