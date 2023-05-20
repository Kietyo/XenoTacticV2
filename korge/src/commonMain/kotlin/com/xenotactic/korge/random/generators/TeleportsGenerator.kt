package com.xenotactic.korge.random.generators

import com.xenotactic.ecs.StatefulEntity
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.model.RectangleEntity
import com.xenotactic.gamelogic.model.TeleportPair
import com.xenotactic.gamelogic.pathing.PathFindingResult
import com.xenotactic.gamelogic.utils.toRectangleEntity
import com.xenotactic.korge.random.GenerationContext
import com.xenotactic.korge.random.IGenerator
import com.xenotactic.korge.utils.StagingEntityUtils
import com.xenotactic.korge.utils.intersectRectangles
import com.xenotactic.korge.utils.intersectsEntity
import pathing.PathFinder

class TeleportsGenerator(
    val numTeleports: Int
) : IGenerator {
    override fun run(context: GenerationContext) {
        val startEntity = context.gameWorld.startEntity
        val finishEntity = context.gameWorld.finishEntity
        val addedCheckpoints = context.gameWorld.checkpoints

        val addedTpIns = mutableListOf<StatefulEntity>()
        val addedTpOuts = mutableListOf<StatefulEntity>()

        val teleportInSize = context.getSizeOfEntity(MapEntityType.TELEPORT_IN)
        val teleportOutSize = context.getSizeOfEntity(MapEntityType.TELEPORT_OUT)
        for (i in 0 until numTeleports) {
            var teleportInPosition: GameUnitTuple
            do {
                context.incrementNumAttempts {
                    "Failed to create place TELEPORT IN $i."
                }
                teleportInPosition = context.getRandomPointWithinMapBounds(teleportInSize)
            } while (
                startEntity.intersectsEntity(teleportInPosition, teleportInSize) ||
                finishEntity.intersectsEntity(teleportInPosition, teleportInSize) ||
                addedCheckpoints.any { it.intersectsEntity(teleportInPosition, teleportInSize) } ||
                addedTpIns.any { it.intersectsEntity(teleportInPosition, teleportInSize) } ||
                addedTpOuts.any { it.intersectsEntity(teleportInPosition, teleportInSize) }
            )

            var teleportOutPosition: GameUnitTuple
            val attemptedPlacementPoints = mutableSetOf<GameUnitTuple>()
            var isFirstAttempt = true
            do {
                do {
                    if (!isFirstAttempt) {
                        context.incrementNumAttempts {
                            """
                                Failed to place TELEPORT OUT $i.
                                stagedTeleportIn: $teleportInPosition
                                Attempted points: $attemptedPlacementPoints
                            """.trimIndent()
                        }
                    }
                    teleportOutPosition = context.getRandomPointWithinMapBounds(teleportOutSize)
                    isFirstAttempt = false
                } while (
                    attemptedPlacementPoints.contains(teleportOutPosition)
                )
                attemptedPlacementPoints.add(teleportOutPosition)
            } while (
                intersectRectangles(
                    teleportInPosition, teleportInSize,
                    teleportOutPosition, teleportOutSize
                ) ||
                startEntity.intersectsEntity(teleportOutPosition, teleportOutSize) ||
                finishEntity.intersectsEntity(teleportOutPosition, teleportOutSize) ||
                addedTpIns.any { it.intersectsEntity(teleportOutPosition, teleportOutSize) } ||
                addedCheckpoints.any { it.intersectsEntity(teleportOutPosition, teleportOutSize) } ||
                PathFinder.getUpdatablePath(
                    context.width,
                    context.height,
                    startEntity.toRectangleEntity(),
                    finishEntity.toRectangleEntity(),
                    blockingEntities = emptyList(),
                    pathingEntities = addedCheckpoints.map { it.toRectangleEntity() },
                    teleportPairs = listOf(
                        TeleportPair(
                            RectangleEntity(
                                teleportInPosition.x, teleportInPosition.y,
                                teleportInSize.first, teleportInSize.second
                            ),
                            RectangleEntity(
                                teleportOutPosition.x, teleportOutPosition.y,
                                teleportOutSize.first, teleportOutSize.second
                            ), i
                        )
                    )
                ) is PathFindingResult.Failure
            )

            val addedTeleportIn = context.world.addEntityReturnStateful {
                addComponentsFromStagingEntity(
                    StagingEntityUtils.createTeleportIn(
                        i, teleportInPosition, teleportInSize
                    )
                )
            }
            addedTpIns.add(addedTeleportIn)

            val addedTeleportOut = context.world.addEntityReturnStateful {
                addComponentsFromStagingEntity(
                    StagingEntityUtils.createTeleportOut(
                        i,
                        teleportOutPosition,
                        teleportOutSize
                    )
                )
            }
            addedTpOuts.add(addedTeleportOut)
        }
    }
}