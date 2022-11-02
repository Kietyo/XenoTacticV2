package com.xenotactic.korge.random

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.components.BottomLeftPositionComponent
import com.xenotactic.korge.components.EntityStartComponent
import com.xenotactic.korge.components.SizeComponent
import com.xenotactic.testing.assertThat
import kotlin.test.Test

internal class RandomMapGeneratorV2Test {
    @Test
    fun generate() {
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

        println(entity)

        assertThat(entity).containsExactlyComponents(
            SizeComponent(2.toGameUnit(), 2.toGameUnit()),
            BottomLeftPositionComponent(17.toGameUnit(), 17.toGameUnit()),
            EntityStartComponent
        )
    }
}