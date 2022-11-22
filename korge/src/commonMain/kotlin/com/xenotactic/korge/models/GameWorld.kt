package com.xenotactic.korge.models

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.model.IRectangleEntity
import com.xenotactic.gamelogic.model.TeleportPair
import com.xenotactic.gamelogic.pathing.PathFindingResult
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.components.*
import com.xenotactic.korge.korge_utils.toRectangleEntity
import pathing.PathFinder

class GameWorld(
    val world: World = World()
) {
    val entityFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(
                MapEntityComponent::class,
                SizeComponent::class,
                BottomLeftPositionComponent::class,
            )
        )
    )
    val selectableEntitiesFamily = world.getOrCreateFamily(
        FamilyConfiguration(
            allOfComponents = setOf(
                MapEntityComponent::class,
                SizeComponent::class,
                BottomLeftPositionComponent::class,
                IsSelectableComponent::class
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
                SpeedAreaEffectComponent::class,
                BottomLeftPositionComponent::class,
                SizeComponent::class
            )
        )
    )
    val bottomLeftPositionComponent =
        world.getComponentContainer<BottomLeftPositionComponent>()
    val sizeComponent = world.getComponentContainer<SizeComponent>()
    val mapEntityComponent = world.getComponentContainer<MapEntityComponent>()
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

    val addedCheckpoints
        get() = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(
                EntityCheckpointComponent::class
            )
        )

    val addedTeleportIns
        get() = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(
                EntityTeleportInComponent::class
            )
        )

    val addedTeleportOuts
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
        blockingEntities: List<IRectangleEntity> = this.blockingEntities.map { it.toRectangleEntity() }
    ): PathFindingResult {
        val startEntity = this.startEntity
        val finishEntity = this.finishEntity
        val addedCheckpoints = this.addedCheckpoints

        val sequenceNumToTeleportInEntities = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(EntityTeleportInComponent::class)
        ).associateBy({
            it[EntityTeleportInComponent::class].sequenceNum
        }) {
            it
        }
        val sequenceNumToTeleportOutEntities = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(EntityTeleportOutComponent::class)
        ).associateBy({
            it[EntityTeleportOutComponent::class].sequenceNum
        }) {
            it
        }
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
            blockingEntities,
            addedCheckpoints.sortedBy { it.get(EntityCheckpointComponent::class).sequenceNum }
                .map { it.toRectangleEntity() },
            teleportPairs
        )
    }
}