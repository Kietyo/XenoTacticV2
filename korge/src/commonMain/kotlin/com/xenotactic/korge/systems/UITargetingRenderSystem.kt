package com.xenotactic.korge.systems

import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.StrokeInfo
import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.gamelogic.components.*
import com.xenotactic.gamelogic.engine.Engine
import com.xenotactic.gamelogic.utils.getCenterPoint
import com.xenotactic.korge.ui.UIMapV2
import kotlin.time.Duration

class UITargetingRenderSystem(val engine: Engine) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            EntityTowerComponent::class,
            BottomLeftPositionComponent::class,
            SizeComponent::class,
            RangeComponent::class,
            TargetingComponent::class
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

                val monsterCenterPoint =
                    world[targetingComponent.targetEntityId, PathSequenceTraversalComponent::class].pathSequenceTraversal.currentPosition

                val (towerWorldX, towerWorldY) = uiMap.getWorldCoordinates(centerPoint.x, centerPoint.y)
                val (monsterWorldX, monsterWorldY) = uiMap.getWorldCoordinates(
                    monsterCenterPoint.x,
                    monsterCenterPoint.y
                )

                stroke(Colors.RED.withAd(0.5), StrokeInfo(thickness = 2.0)) {
                    line(
                        Point(
                            towerWorldX.toDouble(),
                            towerWorldY.toDouble()
                        ),
                        Point(
                            monsterWorldX.toDouble(),
                            monsterWorldY.toDouble()
                        )
                    )
                }

            }
        }

    }
}