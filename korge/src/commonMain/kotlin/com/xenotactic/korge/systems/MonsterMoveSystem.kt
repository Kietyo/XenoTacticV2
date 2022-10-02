package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.MonsterComponent
import com.xenotactic.gamelogic.components.PathSequenceTraversalComponent
import com.xenotactic.gamelogic.components.UIMapEntityComponent
import com.xenotactic.gamelogic.korge_utils.xy
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.ui.UIMapV2
import kotlin.time.Duration

class MonsterMoveSystem(val world: World) : System() {
    override val familyConfiguration: FamilyConfiguration
        = FamilyConfiguration(
        allOfComponents = setOf(MonsterComponent::class, UIMapEntityComponent::class,
        PathSequenceTraversalComponent::class)
        )

    val uiMapV2 = world.injections.getSingleton<UIMapV2>()

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val currentPoint = world[it, PathSequenceTraversalComponent::class].pathSequenceTraversal.currentPosition
            val uiMapEntityComponent = world[it, UIMapEntityComponent::class]

            val (worldX, worldY) = uiMapV2.toWorldDimensions(currentPoint.x.toGameUnit(), currentPoint.y.toGameUnit())

            uiMapEntityComponent.entityView.xy(worldX, worldY)

        }
    }
}