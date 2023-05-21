package com.xenotactic.korge.random

import com.kietyo.ktruth.assertThat
import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.gamelogic.components.*
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.pathing.PathFindingResult
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.random.generators.*
import com.xenotactic.testing.assertThat
import kotlin.test.Test
import kotlin.test.assertIs

internal class RandomMapGeneratorV2Test {

    @Test
    fun generateStartFinishHitsFailureLimit() {
        val result = RandomMapGeneratorV2.generate(
            MapGeneratorConfigurationV2(
                1337, listOf(
                    StartGenerator,
                    FinishGenerator
                ), 2.toGameUnit(), 2.toGameUnit()
            )
        )
        assertIs<MapGeneratorResultV2.Failure>(result)

        assertThat(result.errors).containsExactlyUnordered(
            "Failed to create FINISH entity in a spot that didn't intersect with START.")
    }

    @Test
    fun generateStartFinishCheckpointTeleportsRocksSpeedArea() {
        val result = RandomMapGeneratorV2.generate(
            MapGeneratorConfigurationV2(
                1337, listOf(
                    StartGenerator,
                    FinishGenerator,
                    CheckpointsGenerator(3),
                    TeleportsGenerator(2),
                    RocksGenerator(2),
                    SpeedAreaGenerator(2)
                ), 20.toGameUnit(), 20.toGameUnit()
            )
        )
        assertThat(result).isInstanceOf<MapGeneratorResultV2.Success>()

        println(result.world)

        assertThat(result.world.numEntities).isEqualTo(13)

        val startEntity = result.world.getFirstStatefulEntityMatching(
            FamilyConfiguration.allOf(EntityStartComponent::class)
        )

        assertThat(startEntity).containsExactlyComponents(
            SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            BottomLeftPositionComponent(17.toGameUnit(), 17.toGameUnit()),
            EntityStartComponent,
            EntityTypeComponent(MapEntityType.START)
        )

        val finishEntity = result.world.getFirstStatefulEntityMatching(
            FamilyConfiguration.allOf(EntityFinishComponent::class)
        )
        assertThat(finishEntity).containsExactlyComponents(
            SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            BottomLeftPositionComponent(8.toGameUnit(), 1.toGameUnit()),
            EntityFinishComponent,
            EntityTypeComponent(MapEntityType.FINISH)
        )

        val checkpointEntities = result.world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(EntityCheckpointComponent::class)
        )
        assertThat(checkpointEntities).hasSize(3)
        val sequenceNumToCheckpointEntity = checkpointEntities.associateBy({
            it[EntityCheckpointComponent::class].sequenceNumber
        }) {
            it
        }
        assertThat(sequenceNumToCheckpointEntity[0]!!).containsExactlyComponents(
            SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            BottomLeftPositionComponent(16.toGameUnit(), 7.toGameUnit()),
            EntityCheckpointComponent(0),
            EntityTypeComponent(MapEntityType.CHECKPOINT)
        )
        assertThat(sequenceNumToCheckpointEntity[1]!!).containsExactlyComponents(
            SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            BottomLeftPositionComponent(13.toGameUnit(), 14.toGameUnit()),
            EntityCheckpointComponent(1),
            EntityTypeComponent(MapEntityType.CHECKPOINT)
        )
        assertThat(sequenceNumToCheckpointEntity[2]!!).containsExactlyComponents(
            SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            BottomLeftPositionComponent(9.toGameUnit(), 3.toGameUnit()),
            EntityCheckpointComponent(2),
            EntityTypeComponent(MapEntityType.CHECKPOINT)
        )

        val teleportInEntities = result.world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(EntityTeleportInComponent::class)
        )
        assertThat(teleportInEntities).hasSize(2)
        val teleportOutEntities = result.world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(EntityTeleportOutComponent::class)
        )
        assertThat(teleportOutEntities).hasSize(2)

        val sequenceNumToTeleportIn = teleportInEntities.associateBy({
            it[EntityTeleportInComponent::class].sequenceNumber
        }) {
            it
        }
        val sequenceNumToTeleportOut = teleportOutEntities.associateBy({
            it[EntityTeleportOutComponent::class].sequenceNumber
        }) {
            it
        }

        assertThat(sequenceNumToTeleportIn[0]!!).containsExactlyComponents(
            EntityTeleportInComponent(0),
            SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            BottomLeftPositionComponent(4.toGameUnit(), 2.toGameUnit()),
            EntityTypeComponent(MapEntityType.TELEPORT_IN)
        )
        assertThat(sequenceNumToTeleportOut[0]!!).containsExactlyComponents(
            EntityTeleportOutComponent(0),
            SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            BottomLeftPositionComponent(1.toGameUnit(), 14.toGameUnit()),
            EntityTypeComponent(MapEntityType.TELEPORT_OUT)
        )
        assertThat(sequenceNumToTeleportIn[1]!!).containsExactlyComponents(
            EntityTeleportInComponent(1),
            SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            BottomLeftPositionComponent(17.toGameUnit(), 4.toGameUnit()),
            EntityTypeComponent(MapEntityType.TELEPORT_IN)
        )
        assertThat(sequenceNumToTeleportOut[1]!!).containsExactlyComponents(
            EntityTeleportOutComponent(1),
            SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            BottomLeftPositionComponent(17.toGameUnit(), 9.toGameUnit()),
            EntityTypeComponent(MapEntityType.TELEPORT_OUT)
        )


        val rockEntities = result.gameWorld.rocks
        assertThat(rockEntities).hasSize(2)
        assertThat(rockEntities[0]).containsExactlyComponents(
            BottomLeftPositionComponent(10, 17),
            SizeComponent(4, 2),
            EntityRockComponent,
            EntityBlockingComponent,
            EntityTypeComponent(MapEntityType.ROCK)
        )
        assertThat(rockEntities[1]).containsExactlyComponents(
            BottomLeftPositionComponent(16, 15),
            SizeComponent(4, 2),
            EntityRockComponent,
            EntityBlockingComponent,
            EntityTypeComponent(MapEntityType.ROCK)
        )

        val speedAreas = result.gameWorld.speedAreas
        assertThat(speedAreas).hasSize(2)
        assertThat(speedAreas[0]).containsExactlyComponents(
            BottomLeftPositionComponent(8, 5),
            SizeComponent(20, 20),
            EntitySpeedAreaComponent(0.3647766653668693),
            EntityTypeComponent(MapEntityType.SPEED_AREA)
        )
        assertThat(speedAreas[1]).containsExactlyComponents(
            BottomLeftPositionComponent(14, -16),
            SizeComponent(20, 20),
            EntitySpeedAreaComponent(0.6793122886342378),
            EntityTypeComponent(MapEntityType.SPEED_AREA)
        )
    }

    @Test
    fun regressionTest1() {
        val seed = 1352L

        val width = 30.toGameUnit()
        val height = 20.toGameUnit()

        val randomMap = RandomMapGeneratorV2.generate(
            MapGeneratorConfigurationV2(
                seed,
                listOf(
                    StartGenerator,
                    FinishGenerator,
                    CheckpointsGenerator(2),
                    RocksGenerator(10),
                    TeleportsGenerator(2),
                    SpeedAreaGenerator(5)
                ),
                width, height
            )
        )

        assertThat(randomMap).isInstanceOf<MapGeneratorResultV2.Success>()

        val pathFindingResult = randomMap.gameWorld.getPathFindingResult(
            width, height
        )

        assertThat(pathFindingResult).isInstanceOf<PathFindingResult.Success>()
    }
}