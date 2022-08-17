package com.xenotactic.korge.state

import com.soywiz.korma.geom.Rectangle
import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.MapEntityComponent
import com.xenotactic.gamelogic.components.UIMapEntityComponent
import com.xenotactic.gamelogic.model.MapEntityData
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.views.UIEntity

class GameMapState(
    gameWorld: World
) {
    private val entityFamily = gameWorld.createFamily(
        FamilyConfiguration(allOfComponents = setOf(MapEntityComponent::class, UIMapEntityComponent::class))
    )
    private val mapEntity = gameWorld.getComponentContainer<MapEntityComponent>()
    private val uiMapEntity = gameWorld.getComponentContainer<UIMapEntityComponent>()
    private val numCheckpoints get() = entityFamily.getSequence().count {
        mapEntity.getComponent(it).entityData is MapEntityData.Checkpoint
    }
    private val numCompletedTeleports get() = run {
        var numTpIn = 0
        var numTpOut = 0
        entityFamily.getSequence().forEach {
            val comp = mapEntity.getComponent(it)
            if (comp.entityData is MapEntityData.TeleportIn) {
                numTpIn++
            }
            if (comp.entityData is MapEntityData.TeleportOut) {
                numTpOut++
            }
        }
        minOf(numTpIn, numTpOut)
    }

    fun getIntersectingEntities(rect: Rectangle): List<UIEntity> {
        return entityFamily.getSequence().mapNotNull {
            val comp = uiMapEntity.getComponent(it)
            if (rect.intersects(comp.entityView.getGlobalBounds())) {
                comp.entityView
            } else {
                null
            }
        }.toList()
    }

    fun getNotificationText(entityType: MapEntityType): String {
        val entityName = when (entityType) {
            MapEntityType.START -> "Start"
            MapEntityType.FINISH -> "Finish"
            MapEntityType.CHECKPOINT -> {
                "Checkpoint ${numCheckpoints + 1}"
            }

            MapEntityType.ROCK -> "Rock"
            MapEntityType.TOWER -> TODO()
            MapEntityType.TELEPORT_IN -> "Teleport In ${numCompletedTeleports + 1}"
            MapEntityType.TELEPORT_OUT -> "Teleport Out ${numCompletedTeleports + 1}"
            MapEntityType.SMALL_BLOCKER -> TODO()
            MapEntityType.SPEED_AREA -> TODO()
        }

        return "Placement Mode: $entityName"
    }
}