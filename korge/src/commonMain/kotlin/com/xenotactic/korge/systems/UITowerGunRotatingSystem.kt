package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.gamelogic.components.*
import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.getCenterPoint
import com.xenotactic.korge.ui.UIMapV2
import korlibs.korge.view.rotation
import korlibs.logger.Logger
import kotlin.time.Duration

class UITowerGunRotatingSystem(
    val engine: Engine
) : System() {
    companion object {
        val logger = Logger<UITowerGunRotatingSystem>()
    }

    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            EntityTowerComponent::class,
            BottomLeftPositionComponent::class,
            SizeComponent::class,
            UIGunBarrelComponent::class,
            TargetingComponent::class
        ),
    )

    private val world = engine.gameWorld.world
    private val uiMap = engine.injections.getSingleton<UIMapV2>()

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach { towerId ->
            //            logger.info { "update: tower id: $towerId" }
            val sizeComponent = world[towerId, SizeComponent::class]
            val bottomLeftPositionComponent = world[towerId, BottomLeftPositionComponent::class]
            val uiGunBarrelComponent = world[towerId, UIGunBarrelComponent::class]
            val targetingComponent = world[towerId, TargetingComponent::class]
            val towerCenterPoint = getCenterPoint(bottomLeftPositionComponent, sizeComponent)

            val monsterCenterPoint =
                world[targetingComponent.targetEntityId, PathSequenceTraversalComponent::class].pathSequenceTraversal.currentPosition

            val (towerWorldX, towerWorldY) = uiMap.getWorldCoordinates(towerCenterPoint.x, towerCenterPoint.y)
            val (monsterWorldX, monsterWorldY) = uiMap.getWorldCoordinates(monsterCenterPoint.x, monsterCenterPoint.y)

            val angle = IPoint(towerWorldX.toDouble(), towerWorldY.toDouble()).angleTo(
                IPoint(monsterWorldX.toDouble(), monsterWorldY.toDouble())
            )
            uiGunBarrelComponent.view.rotation(angle)
        }
    }
}