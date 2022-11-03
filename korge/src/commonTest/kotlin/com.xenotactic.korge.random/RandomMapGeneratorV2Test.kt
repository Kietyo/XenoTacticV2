package com.xenotactic.korge.random

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.components.BottomLeftPositionComponent
import com.xenotactic.korge.components.EntityFinishComponent
import com.xenotactic.korge.components.EntityStartComponent
import com.xenotactic.korge.components.SizeComponent
import com.xenotactic.testing.assertThat
import kotlin.test.Test
import kotlin.test.assertIs

internal class RandomMapGeneratorV2Test {
    @Test
    fun generateStart() {
        val result = RandomMapGeneratorV2.generate(
            MapGeneratorConfigurationV2(
                1337, listOf(
                    StartGenerator
                ), 20.toGameUnit(), 20.toGameUnit()
            )
        )
        assertThat(result).isInstanceOf<MapGeneratorResultV2.Success>()

        val entities = result.world.getEntities(
            FamilyConfiguration.allOf(EntityStartComponent::class)
        )

        assertThat(entities).hasSize(1)

        val entity = result.world.getStatefulEntitySnapshot(entities.first())

        assertThat(entity).containsExactlyComponents(
            SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            BottomLeftPositionComponent(17.toGameUnit(), 17.toGameUnit()),
            EntityStartComponent
        )
    }

    @Test
    fun generateStartFinish() {
        val result = RandomMapGeneratorV2.generate(
            MapGeneratorConfigurationV2(
                1337, listOf(
                    StartGenerator,
                    FinishGenerator
                ), 20.toGameUnit(), 20.toGameUnit()
            )
        )
        assertThat(result).isInstanceOf<MapGeneratorResultV2.Success>()

        println(result.world)

        assertThat(result.world.numEntities).isEqualTo(2)

        val startEntity = result.world.getFirstStatefulEntityMatching(FamilyConfiguration.allOf(EntityStartComponent::class))

        assertThat(startEntity).containsExactlyComponents(
            SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            BottomLeftPositionComponent(17.toGameUnit(), 17.toGameUnit()),
            EntityStartComponent
        )

        val finishEntity = result.world.getFirstStatefulEntityMatching(FamilyConfiguration.allOf(EntityFinishComponent::class))
        assertThat(finishEntity).containsExactlyComponents(
            SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            BottomLeftPositionComponent(8.toGameUnit(), 1.toGameUnit()),
            EntityFinishComponent
        )
    }

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
}