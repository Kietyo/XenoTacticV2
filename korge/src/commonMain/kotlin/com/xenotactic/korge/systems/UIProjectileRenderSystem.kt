package com.xenotactic.korge.systems

import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.vector.circle
import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.gamelogic.engine.Engine
import com.xenotactic.korge.ui.UIMapV2
import kotlin.time.Duration

class UIProjectileRenderSystem(
    val engine: Engine
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            com.xenotactic.gamelogic.components.ProjectileComponent::class,
            com.xenotactic.gamelogic.components.MutableCenterPositionComponent::class
        )
    )

    private val world = engine.gameWorld.world
    private val uiMap = engine.injections.getSingleton<UIMapV2>()

    override fun update(deltaTime: Duration) {
        uiMap.projectileLayer.updateShape {
            getFamily().getSequence().forEach {
                val mutableCenterPositionComponent = world[it, com.xenotactic.gamelogic.components.MutableCenterPositionComponent::class]
                val (worldX, worldY) = uiMap.getWorldCoordinates(
                    mutableCenterPositionComponent.x,
                    mutableCenterPositionComponent.y
                )
                fill(Colors.RED) {
                    circle(worldX.toDouble(), worldY.toDouble(), 10.0)
                }
            }
        }

    }
}