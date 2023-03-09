package com.xenotactic.korge.random

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.components.*
import com.xenotactic.korge.random.generators.*
import com.kietyo.ktruth2.assertThat
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

        val startEntity = result.world.getFirstStatefulEntityMatching(FamilyConfiguration.allOf(com.xenotactic.gamelogic.components.EntityStartComponent::class))

        assertThat(startEntity).containsExactlyComponents(
            com.xenotactic.gamelogic.components.SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            com.xenotactic.gamelogic.components.BottomLeftPositionComponent(17.toGameUnit(), 17.toGameUnit()),
            com.xenotactic.gamelogic.components.EntityStartComponent,
            com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.START)
        )

        val finishEntity = result.world.getFirstStatefulEntityMatching(FamilyConfiguration.allOf(com.xenotactic.gamelogic.components.EntityFinishComponent::class))
        assertThat(finishEntity).containsExactlyComponents(
            com.xenotactic.gamelogic.components.SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            com.xenotactic.gamelogic.components.BottomLeftPositionComponent(8.toGameUnit(), 1.toGameUnit()),
            com.xenotactic.gamelogic.components.EntityFinishComponent,
            com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.FINISH)
        )

        val checkpointEntities = result.world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(com.xenotactic.gamelogic.components.EntityCheckpointComponent::class)
        )
        assertThat(checkpointEntities).hasSize(3)
        val sequenceNumToCheckpointEntity = checkpointEntities.associateBy({
            it[com.xenotactic.gamelogic.components.EntityCheckpointComponent::class].sequenceNumber
        }) {
            it
        }
        assertThat(sequenceNumToCheckpointEntity[0]!!).containsExactlyComponents(
            com.xenotactic.gamelogic.components.SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            com.xenotactic.gamelogic.components.BottomLeftPositionComponent(16.toGameUnit(), 7.toGameUnit()),
            com.xenotactic.gamelogic.components.EntityCheckpointComponent(0),
            com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.CHECKPOINT)
        )
        assertThat(sequenceNumToCheckpointEntity[1]!!).containsExactlyComponents(
            com.xenotactic.gamelogic.components.SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            com.xenotactic.gamelogic.components.BottomLeftPositionComponent(13.toGameUnit(), 14.toGameUnit()),
            com.xenotactic.gamelogic.components.EntityCheckpointComponent(1),
            com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.CHECKPOINT)
        )
        assertThat(sequenceNumToCheckpointEntity[2]!!).containsExactlyComponents(
            com.xenotactic.gamelogic.components.SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            com.xenotactic.gamelogic.components.BottomLeftPositionComponent(9.toGameUnit(), 3.toGameUnit()),
            com.xenotactic.gamelogic.components.EntityCheckpointComponent(2),
            com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.CHECKPOINT)
        )

        val teleportInEntities = result.world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(com.xenotactic.gamelogic.components.EntityTeleportInComponent::class)
        )
        assertThat(teleportInEntities).hasSize(2)
        val teleportOutEntities = result.world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(com.xenotactic.gamelogic.components.EntityTeleportOutComponent::class)
        )
        assertThat(teleportOutEntities).hasSize(2)

        val sequenceNumToTeleportIn = teleportInEntities.associateBy({
            it[com.xenotactic.gamelogic.components.EntityTeleportInComponent::class].sequenceNumber
        }) {
            it
        }
        val sequenceNumToTeleportOut = teleportOutEntities.associateBy({
            it[com.xenotactic.gamelogic.components.EntityTeleportOutComponent::class].sequenceNumber
        }) {
            it
        }

        assertThat(sequenceNumToTeleportIn[0]!!).containsExactlyComponents(
            com.xenotactic.gamelogic.components.EntityTeleportInComponent(0),
            com.xenotactic.gamelogic.components.SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            com.xenotactic.gamelogic.components.BottomLeftPositionComponent(4.toGameUnit(), 2.toGameUnit()),
            com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.TELEPORT_IN)
        )
        assertThat(sequenceNumToTeleportOut[0]!!).containsExactlyComponents(
            com.xenotactic.gamelogic.components.EntityTeleportOutComponent(0),
            com.xenotactic.gamelogic.components.SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            com.xenotactic.gamelogic.components.BottomLeftPositionComponent(1.toGameUnit(), 14.toGameUnit()),
            com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.TELEPORT_OUT)
        )
        assertThat(sequenceNumToTeleportIn[1]!!).containsExactlyComponents(
            com.xenotactic.gamelogic.components.EntityTeleportInComponent(1),
            com.xenotactic.gamelogic.components.SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            com.xenotactic.gamelogic.components.BottomLeftPositionComponent(17.toGameUnit(), 4.toGameUnit()),
            com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.TELEPORT_IN)
        )
        assertThat(sequenceNumToTeleportOut[1]!!).containsExactlyComponents(
            com.xenotactic.gamelogic.components.EntityTeleportOutComponent(1),
            com.xenotactic.gamelogic.components.SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            com.xenotactic.gamelogic.components.BottomLeftPositionComponent(17.toGameUnit(), 9.toGameUnit()),
            com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.TELEPORT_OUT)
        )


        val rockEntities = result.gameWorld.rocks
        assertThat(rockEntities).hasSize(2)
        assertThat(rockEntities[0]).containsExactlyComponents(
            com.xenotactic.gamelogic.components.BottomLeftPositionComponent(10, 17),
            com.xenotactic.gamelogic.components.SizeComponent(4, 2),
            com.xenotactic.gamelogic.components.EntityRockComponent,
            com.xenotactic.gamelogic.components.EntityBlockingComponent,
            com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.ROCK)
        )
        assertThat(rockEntities[1]).containsExactlyComponents(
            com.xenotactic.gamelogic.components.BottomLeftPositionComponent(16, 15),
            com.xenotactic.gamelogic.components.SizeComponent(4, 2),
            com.xenotactic.gamelogic.components.EntityRockComponent,
            com.xenotactic.gamelogic.components.EntityBlockingComponent,
            com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.ROCK)
        )

        val speedAreas = result.gameWorld.speedAreas
        assertThat(speedAreas).hasSize(2)
        assertThat(speedAreas[0]).containsExactlyComponents(
            com.xenotactic.gamelogic.components.BottomLeftPositionComponent(8, 5),
            com.xenotactic.gamelogic.components.SizeComponent(20, 20),
            com.xenotactic.gamelogic.components.EntitySpeedAreaComponent(0.3647766653668693),
            com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.SPEED_AREA)
        )
        assertThat(speedAreas[1]).containsExactlyComponents(
            com.xenotactic.gamelogic.components.BottomLeftPositionComponent(14, -16),
            com.xenotactic.gamelogic.components.SizeComponent(20, 20),
            com.xenotactic.gamelogic.components.EntitySpeedAreaComponent(0.6793122886342378),
            com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.SPEED_AREA)
        )
    }
}