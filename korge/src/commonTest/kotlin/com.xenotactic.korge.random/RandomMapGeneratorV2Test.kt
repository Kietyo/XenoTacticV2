package com.xenotactic.korge.random

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.components.EntityStartComponent
import com.xenotactic.testing.assertThat
import kotlin.test.Test
import kotlin.test.assertTrue

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

        val entities = result.gameWorld.world.getEntities(FamilyConfiguration.allOf(EntityStartComponent::class))

        assertThat(entities).hasSize(1)


    }
}