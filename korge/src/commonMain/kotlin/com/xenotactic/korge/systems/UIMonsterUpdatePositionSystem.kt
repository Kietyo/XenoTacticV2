package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.gamelogic.utils.xy
import com.xenotactic.korge.ui.UIMapV2
import kotlin.time.Duration

class UIMonsterUpdatePositionSystem(val world: World) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            com.xenotactic.gamelogic.components.MonsterComponent::class,
            com.xenotactic.gamelogic.components.UIEntityViewComponent::class,
            com.xenotactic.gamelogic.components.PathSequenceTraversalComponent::class,
            com.xenotactic.gamelogic.components.VelocityComponent::class,
        )
    )

    val uiMapV2 = world.injections.getSingleton<UIMapV2>()

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val traversal =
                world[it, com.xenotactic.gamelogic.components.PathSequenceTraversalComponent::class].pathSequenceTraversal
            val currentPoint = traversal.currentPosition
            val uiEntityViewComponent = world[it, com.xenotactic.gamelogic.components.UIEntityViewComponent::class]

            val (worldX, worldY) = uiMapV2.getWorldCoordinates(
                currentPoint.x, currentPoint.y,
                0.toGameUnit()
            )

            uiEntityViewComponent.entityView.xy(worldX, worldY)
        }
    }

}