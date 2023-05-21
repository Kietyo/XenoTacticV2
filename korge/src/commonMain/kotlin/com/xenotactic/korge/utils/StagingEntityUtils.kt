package com.xenotactic.korge.utils

import com.xenotactic.ecs.StagingEntity
import com.xenotactic.gamelogic.components.*
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.IRectangleEntity
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.GameUnit

object StagingEntityUtils {
    fun createStart(
        position: GameUnitTuple,
        size: GameUnitTuple = GameUnitTuple(2, 2)
    ) = StagingEntity {
        addComponentOrThrow(position.toBottomLeftPositionComponent())
        addComponentOrThrow(size.toSizeComponent())
        addComponentOrThrow(com.xenotactic.gamelogic.components.EntityStartComponent)
        addComponentOrThrow(com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.START))
    }

    fun createFinish(
        position: GameUnitTuple,
        size: GameUnitTuple = GameUnitTuple(2, 2)
    ) = StagingEntity {
        addComponentOrThrow(position.toBottomLeftPositionComponent())
        addComponentOrThrow(size.toSizeComponent())
        addComponentOrThrow(com.xenotactic.gamelogic.components.EntityFinishComponent)
        addComponentOrThrow(com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.FINISH))
    }

    fun createCheckpoint(
        sequenceNum: Int,
        position: GameUnitTuple,
        size: GameUnitTuple = GameUnitTuple(2, 2)
    ) = StagingEntity {
        addComponentOrThrow(position.toBottomLeftPositionComponent())
        addComponentOrThrow(size.toSizeComponent())
        addComponentOrThrow(com.xenotactic.gamelogic.components.EntityCheckpointComponent(sequenceNum))
        addComponentOrThrow(com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.CHECKPOINT))
    }

    fun createTeleportIn(
        sequenceNum: Int,
        position: GameUnitTuple,
        size: GameUnitTuple = GameUnitTuple(2, 2)
    ) = StagingEntity {
        addComponentOrThrow(position.toBottomLeftPositionComponent())
        addComponentOrThrow(size.toSizeComponent())
        addComponentOrThrow(com.xenotactic.gamelogic.components.EntityTeleportInComponent(sequenceNum))
        addComponentOrThrow(com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.TELEPORT_IN))
    }

    fun createTeleportOut(
        sequenceNum: Int,
        position: GameUnitTuple,
        size: GameUnitTuple = GameUnitTuple(2, 2)
    ) = StagingEntity {
        addComponentOrThrow(position.toBottomLeftPositionComponent())
        addComponentOrThrow(size.toSizeComponent())
        addComponentOrThrow(com.xenotactic.gamelogic.components.EntityTeleportOutComponent(sequenceNum))
        addComponentOrThrow(com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.TELEPORT_OUT))
    }

    fun createRock(
        rect: IRectangleEntity,
    ) = StagingEntity {
        addComponentOrThrow(rect.getBottomLeftPositionComponent())
        addComponentOrThrow(rect.getSizeComponent())
        addComponentOrThrow(com.xenotactic.gamelogic.components.EntityRockComponent)
        addComponentOrThrow(com.xenotactic.gamelogic.components.EntityBlockingComponent)
        addComponentOrThrow(com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.ROCK))
    }

    fun createSpeedArea(
        position: GameUnitTuple,
        diameter: GameUnit,
        speedEffect: Double,
    ) = StagingEntity {
        addComponentOrThrow(position.toBottomLeftPositionComponent())
        addComponentOrThrow(SizeComponent(diameter, diameter))
        addComponentOrThrow(EntitySpeedAreaComponent(speedEffect))
        addComponentOrThrow(com.xenotactic.gamelogic.components.EntityTypeComponent(MapEntityType.SPEED_AREA))
    }

    fun createTower(
        position: GameUnitTuple,
        size: GameUnitTuple = GameUnitTuple(2, 2)
    ) = StagingEntity {
        addComponentOrThrow(position.toBottomLeftPositionComponent())
        addComponentOrThrow(size.toSizeComponent())
        addComponentOrThrow(EntityBlockingComponent)
        addComponentOrThrow(EntityTowerComponent)
        addComponentOrThrow(EntityTypeComponent(MapEntityType.TOWER))
        addComponentOrThrow(SupplyCostComponent(1))
    }

    fun createSupplyDepot(position: GameUnitTuple,
        size: GameUnitTuple = GameUnitTuple(2, 2)) = StagingEntity {
        addComponentOrThrow(position.toBottomLeftPositionComponent())
        addComponentOrThrow(size.toSizeComponent())
        addComponentOrThrow(EntityBlockingComponent)
        addComponentOrThrow(EntitySupplyDepotComponent)
        addComponentOrThrow(EntityTypeComponent(MapEntityType.SUPPLY_DEPOT))
    }
}
