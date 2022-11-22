package com.xenotactic.korge.systems

import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.vector.StrokeInfo
import com.soywiz.korma.geom.vector.line
import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.korge.components.*
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.korge_utils.getCenterPoint
import com.xenotactic.korge.ui.UIMapV2
import kotlin.time.Duration

class TargetingRenderSystem(val engine: Engine) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            EntityTowerComponent::class, BottomLeftPositionComponent::class, SizeComponent::class,
            RangeComponent::class, TargetingComponent::class
        ),
    )

    private val world = engine.gameWorld.world
    private val uiMap = engine.injections.getSingleton<UIMapV2>()

    override fun update(deltaTime: Duration) {
        uiMap.targetingLinesLayer.updateShape {
            getFamily().getSequence().forEach {
                val targetingComponent = world[it, TargetingComponent::class]
                val sizeComponent = world[it, SizeComponent::class]
                val bottomLeftPositionComponent = world[it, BottomLeftPositionComponent::class]
                val centerPoint = getCenterPoint(bottomLeftPositionComponent, sizeComponent)

                val monsterCenterPoint = world[targetingComponent.targetEntityId, PathSequenceTraversalComponent::class].pathSequenceTraversal.currentPosition

                val (towerWorldX, towerWorldY) = uiMap.getWorldCoordinates(centerPoint.x, centerPoint.y)
                val (monsterWorldX, monsterWorldY) = uiMap.getWorldCoordinates(monsterCenterPoint.x, monsterCenterPoint.y)

                stroke(Colors.YELLOW, StrokeInfo(thickness = 3.0)) {
                    line(
                        towerWorldX.toDouble(),
                        towerWorldY.toDouble(),
                        monsterWorldX.toDouble(),
                        monsterWorldY.toDouble(),
                    )
                }

            }
        }

    }
}