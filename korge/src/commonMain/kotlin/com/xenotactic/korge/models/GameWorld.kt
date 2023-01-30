package com.xenotactic.korge.models

import com.xenotactic.ecs.EntityId
import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.EntityTowerComponent
import com.xenotactic.gamelogic.model.IRectangleEntity
import com.xenotactic.gamelogic.model.TeleportPair
import com.xenotactic.gamelogic.pathing.PathFindingResult
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.korge.korge_utils.toRectangleEntity
import pathing.PathFinder

class GameWorld(
    val world: World = World()
) {
    val entityFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(
                com.xenotactic.gamelogic.components.EntityTypeComponent::class,
                com.xenotactic.gamelogic.components.SizeComponent::class,
                com.xenotactic.gamelogic.components.BottomLeftPositionComponent::class,
            )
        )
    )
    val selectableEntitiesFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(
                com.xenotactic.gamelogic.components.EntityTypeComponent::class,
                com.xenotactic.gamelogic.components.SizeComponent::class,
                com.xenotactic.gamelogic.components.BottomLeftPositionComponent::class,
                com.xenotactic.gamelogic.components.SelectableComponent::class
            )
        )
    )
    val uiEntityFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(com.xenotactic.gamelogic.components.UIEntityViewComponent::class)
        )
    )
    val preSelectionFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(com.xenotactic.gamelogic.components.PreSelectionComponent::class)
        )
    )
    val selectionFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(com.xenotactic.gamelogic.components.SelectedComponent::class)
        )
    )
    val monsterFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(com.xenotactic.gamelogic.components.MonsterComponent::class, com.xenotactic.gamelogic.components.PathSequenceTraversalComponent::class)
        )
    )
    val speedAreaFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(
                com.xenotactic.gamelogic.components.EntitySpeedAreaComponent::class,
                com.xenotactic.gamelogic.components.BottomLeftPositionComponent::class,
                com.xenotactic.gamelogic.components.SizeComponent::class
            )
        )
    )
    val blockingEntitiesFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(
                com.xenotactic.gamelogic.components.EntityBlockingComponent::class,
            )
        )
    )

    val bottomLeftPositionComponent =
        world.getComponentContainer<com.xenotactic.gamelogic.components.BottomLeftPositionComponent>()
    val sizeComponent = world.getComponentContainer<com.xenotactic.gamelogic.components.SizeComponent>()
    val entityTypeComponents = world.getComponentContainer<com.xenotactic.gamelogic.components.EntityTypeComponent>()
    val uiEntityViewComponentContainer =
        world.getComponentContainer<com.xenotactic.gamelogic.components.UIEntityViewComponent>()
    val uiMapEntityComponentContainer =
        world.getComponentContainer<com.xenotactic.gamelogic.components.UIMapEntityComponent>()
    val uiMapEntityTextComponentContainer =
        world.getComponentContainer<com.xenotactic.gamelogic.components.UIMapEntityTextComponent>()
    val selectionComponentContainer = world.getComponentContainer<com.xenotactic.gamelogic.components.SelectedComponent>()
    val preSelectionComponentContainer = world.getComponentContainer<com.xenotactic.gamelogic.components.PreSelectionComponent>()

    val startEntity
        get() = world.getFirstStatefulEntityMatching(
            FamilyConfiguration.allOf(
                com.xenotactic.gamelogic.components.EntityStartComponent::class
            )
        )
    val finishEntity
        get() = world.getFirstStatefulEntityMatching(
            FamilyConfiguration.allOf(
                com.xenotactic.gamelogic.components.EntityFinishComponent::class
            )
        )

    val checkpoints
        get() = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(
                com.xenotactic.gamelogic.components.EntityCheckpointComponent::class
            )
        )

    val teleportIns
        get() = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(
                com.xenotactic.gamelogic.components.EntityTeleportInComponent::class
            )
        )

    val teleportOuts
        get() = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(
                com.xenotactic.gamelogic.components.EntityTeleportOutComponent::class
            )
        )

    val rocks
        get() = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(
                com.xenotactic.gamelogic.components.EntityRockComponent::class
            )
        )

    val speedAreas
        get() = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(
                com.xenotactic.gamelogic.components.EntitySpeedAreaComponent::class
            )
        )

    val blockingEntities
        get() = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(com.xenotactic.gamelogic.components.EntityBlockingComponent::class)
        )

    fun getPathFindingResult(
        width: GameUnit, height: GameUnit,
        blockingEntities: List<IRectangleEntity> = this.blockingEntities.map { it.toRectangleEntity() },
        additionalBlockingEntities: List<IRectangleEntity> = emptyList()
    ): PathFindingResult {
        val startEntity = this.startEntity
        val finishEntity = this.finishEntity
        val addedCheckpoints = this.checkpoints

        val sequenceNumToTeleportInEntities = this.teleportIns.associateBy({
            it[com.xenotactic.gamelogic.components.EntityTeleportInComponent::class].sequenceNumber
        }) {
            it
        }
        val sequenceNumToTeleportOutEntities = this.teleportOuts.associateBy({
            it[com.xenotactic.gamelogic.components.EntityTeleportOutComponent::class].sequenceNumber
        }) {
            it
        }

        require(
            sequenceNumToTeleportInEntities.keys == sequenceNumToTeleportOutEntities.keys
        )

        val teleportPairs = sequenceNumToTeleportInEntities.map {
            TeleportPair(
                it.value.toRectangleEntity(),
                sequenceNumToTeleportOutEntities[it.key]!!.toRectangleEntity(),
                it.key
            )
        }

        return PathFinder.getUpdatablePath(
            width,
            height,
            startEntity.toRectangleEntity(),
            finishEntity.toRectangleEntity(),
            blockingEntities + additionalBlockingEntities,
            addedCheckpoints.sortedBy { it[com.xenotactic.gamelogic.components.EntityCheckpointComponent::class].sequenceNumber }
                .map { it.toRectangleEntity() },
            teleportPairs
        )
    }

    fun isTowerEntity(id: EntityId): Boolean {
        return world.getComponentContainer<EntityTowerComponent>().containsComponent(id)
    }
}