package com.xenotactic.korge.systems

import com.soywiz.klogger.Logger
import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.korge_utils.getCenterPoint
import kotlin.time.Duration

class TowerAttackSystem(
    val world: World
) : System() {
    companion object {
        val logger = Logger<TowerAttackSystem>()
    }
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            com.xenotactic.gamelogic.components.EntityTowerComponent::class, com.xenotactic.gamelogic.components.BottomLeftPositionComponent::class, com.xenotactic.gamelogic.components.SizeComponent::class,
            com.xenotactic.gamelogic.components.RangeComponent::class, com.xenotactic.gamelogic.components.TargetingComponent::class,
            com.xenotactic.gamelogic.components.ReloadTimeComponent::class,
            com.xenotactic.gamelogic.components.ReadyToAttackComponent::class
        ),
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach { towerId ->
//            logger.info { "update: tower id: $towerId" }
            val sizeComponent = world[towerId, com.xenotactic.gamelogic.components.SizeComponent::class]
            val bottomLeftPositionComponent = world[towerId, com.xenotactic.gamelogic.components.BottomLeftPositionComponent::class]
            val towerCenterPoint = getCenterPoint(bottomLeftPositionComponent, sizeComponent)

            val targetingComponent = world[towerId, com.xenotactic.gamelogic.components.TargetingComponent::class]

            world.addEntity {
                addComponentOrThrow(com.xenotactic.gamelogic.components.ProjectileComponent)
                addComponentOrThrow(targetingComponent)
                addComponentOrThrow(com.xenotactic.gamelogic.components.VelocityComponent(0.2.toGameUnit()))
                addComponentOrThrow(
                    com.xenotactic.gamelogic.components.MutableCenterPositionComponent(
                        towerCenterPoint.x,
                        towerCenterPoint.y
                    )
                )
                addComponentOrThrow(com.xenotactic.gamelogic.components.DamageComponent(10.0))
            }

            world.modifyEntity(towerId) {
                addComponentOrThrow(com.xenotactic.gamelogic.components.ReloadDowntimeComponent(0.0))
                removeComponent<com.xenotactic.gamelogic.components.ReadyToAttackComponent>()
            }
        }
    }
}