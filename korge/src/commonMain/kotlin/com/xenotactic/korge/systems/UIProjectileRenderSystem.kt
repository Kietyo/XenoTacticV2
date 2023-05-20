package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.toPoint
import com.xenotactic.korge.ui.UIMapV2
import korlibs.image.color.Colors
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
                val mutableCenterPositionComponent =
                    world[it, com.xenotactic.gamelogic.components.MutableCenterPositionComponent::class]
                val worldPoint = uiMap.getWorldCoordinates(
                    mutableCenterPositionComponent.x,
                    mutableCenterPositionComponent.y
                )
                fill(Colors.RED) {
                    circle(worldPoint.toPoint(), 10.0f)
                }
            }
        }

    }
}