package com.xenotactic.korge.models

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.model.IRectangleEntity
import com.xenotactic.gamelogic.model.TeleportPair
import com.xenotactic.gamelogic.pathing.PathFindingResult
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.korge.components.*
import com.xenotactic.korge.korge_utils.toRectangleEntity
import pathing.PathFinder

class GameWorld(
    val world: World = World()
) {
    val entityFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(
                EntityTypeComponent::class,
                SizeComponent::class,
                BottomLeftPositionComponent::class,
            )
        )
    )
    val selectableEntitiesFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(
                EntityTypeComponent::class,
                SizeComponent::class,
                BottomLeftPositionComponent::class,
                SelectableComponent::class
            )
        )
    )
    val uiEntityFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(UIEntityViewComponent::class)
        )
    )
    val preSelectionFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(PreSelectionComponent::class)
        )
    )
    val selectionFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(SelectedComponent::class)
        )
    )
    val monsterFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(MonsterComponent::class, PathSequenceTraversalComponent::class)
        )
    )
    val speedAreaFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(
                EntitySpeedAreaComponent::class,
                BottomLeftPositionComponent::class,
                SizeComponent::class
            )
        )
    )
    val bottomLeftPositionComponent =
        world.getComponentContainer<BottomLeftPositionComponent>()
    val sizeComponent = world.getComponentContainer<SizeComponent>()
    val entityTypeComponents = world.getComponentContainer<EntityTypeComponent>()
    val uiEntityViewComponentContainer =
        world.getComponentContainer<UIEntityViewComponent>()
    val uiMapEntityComponentContainer =
        world.getComponentContainer<UIMapEntityComponent>()
    val uiMapEntityTextComponentContainer =
        world.getComponentContainer<UIMapEntityTextComponent>()
    val selectionComponentContainer = world.getComponentContainer<SelectedComponent>()
    val preSelectionComponentContainer = world.getComponentContainer<PreSelectionComponent>()

    val startEntity
        get() = world.getFirstStatefulEntityMatching(
            FamilyConfiguration.allOf(
                EntityStartComponent::class
            )
        )
    val finishEntity
        get() = world.getFirstStatefulEntityMatching(
            FamilyConfiguration.allOf(
                EntityFinishComponent::class
            )
        )

    val checkpoints
        get() = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(
                EntityCheckpointComponent::class
            )
        )

    val teleportIns
        get() = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(
                EntityTeleportInComponent::class
            )
        )

    val teleportOuts
        get() = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(
                EntityTeleportOutComponent::class
            )
        )

    val rocks
        get() = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(
                EntityRockComponent::class
            )
        )

    val speedAreas
        get() = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(
                EntitySpeedAreaComponent::class
            )
        )

    val blockingEntities
        get() = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(EntityBlockingComponent::class)
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
            it[EntityTeleportInComponent::class].sequenceNumber
        }) {
            it
        }
        val sequenceNumToTeleportOutEntities = this.teleportOuts.associateBy({
            it[EntityTeleportOutComponent::class].sequenceNumber
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
            addedCheckpoints.sortedBy { it[EntityCheckpointComponent::class].sequenceNumber }
                .map { it.toRectangleEntity() },
            teleportPairs
        )
    }
}