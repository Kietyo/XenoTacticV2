package com.xenotactic.korge.korge_utils

import com.xenotactic.ecs.StagingEntity
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.IRectangleEntity
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.korge.components.*

object StagingEntityUtils {
    fun createStart(
        position: GameUnitTuple,
        size: GameUnitTuple
    ) = StagingEntity {
            addComponentOrThrow(position.toBottomLeftPositionComponent())
            addComponentOrThrow(size.toSizeComponent())
            addComponentOrThrow(EntityStartComponent)
            addComponentOrThrow(EntityTypeComponent(MapEntityType.START))
        }
    fun createFinish(
        position: GameUnitTuple,
        size: GameUnitTuple
    ) = StagingEntity {
            addComponentOrThrow(position.toBottomLeftPositionComponent())
            addComponentOrThrow(size.toSizeComponent())
            addComponentOrThrow(EntityFinishComponent)
            addComponentOrThrow(EntityTypeComponent(MapEntityType.FINISH))
        }
    fun createCheckpoint(
        position: GameUnitTuple,
        size: GameUnitTuple,
        sequenceNum: Int
    ) = StagingEntity {
        addComponentOrThrow(position.toBottomLeftPositionComponent())
        addComponentOrThrow(size.toSizeComponent())
        addComponentOrThrow(EntityCheckpointComponent(sequenceNum))
        addComponentOrThrow(EntityTypeComponent(MapEntityType.CHECKPOINT))
    }
    fun createTeleportIn(
        position: GameUnitTuple,
        size: GameUnitTuple,
        sequenceNum: Int
    ) = StagingEntity {
        addComponentOrThrow(position.toBottomLeftPositionComponent())
        addComponentOrThrow(size.toSizeComponent())
        addComponentOrThrow(EntityTeleportInComponent(sequenceNum))
        addComponentOrThrow(EntityTypeComponent(MapEntityType.TELEPORT_IN))
    }
    fun createTeleportOut(
        position: GameUnitTuple,
        size: GameUnitTuple,
        sequenceNum: Int
    ) = StagingEntity {
        addComponentOrThrow(position.toBottomLeftPositionComponent())
        addComponentOrThrow(size.toSizeComponent())
        addComponentOrThrow(EntityTeleportOutComponent(sequenceNum))
        addComponentOrThrow(EntityTypeComponent(MapEntityType.TELEPORT_OUT))
    }
    fun createRock(
        rect: IRectangleEntity,
    ) = StagingEntity {
        addComponentOrThrow(rect.getBottomLeftPositionComponent())
        addComponentOrThrow(rect.getSizeComponent())
        addComponentOrThrow(EntityRockComponent)
        addComponentOrThrow(EntityBlockingComponent)
        addComponentOrThrow(EntityTypeComponent(MapEntityType.ROCK))
    }
    fun createSpeedArea(
        position: GameUnitTuple,
        diameter: GameUnit,
        speedEffect: Double,
    ) = StagingEntity {
        addComponentOrThrow(position.toBottomLeftPositionComponent())
        addComponentOrThrow(SizeComponent(diameter, diameter))
        addComponentOrThrow(EntitySpeedAreaComponent(speedEffect))
        addComponentOrThrow(EntityTypeComponent(MapEntityType.SPEED_AREA))
    }
}
