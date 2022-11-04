package com.xenotactic.korge.models

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.korge.components.*
import com.xenotactic.korge.korge_utils.toRectangleEntity
import pathing.PathFinder

class GameWorld(
    val width: GameUnit,
    val height: GameUnit,
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
    val uiEntityViewComponentContainer =
        world.getComponentContainer<UIEntityViewComponent>()
    val uiMapEntityComponentContainer =
        world.getComponentContainer<UIMapEntityComponent>()
    val uiMapEntityTextComponentContainer =
        world.getComponentContainer<UIMapEntityTextComponent>()
    val selectionComponentContainer = world.getComponentContainer<SelectedComponent>()
    val preSelectionComponentContainer = world.getComponentContainer<PreSelectionComponent>()

    fun getShortestPath() {
        val startEntity = world.getFirstStatefulEntityMatching(
            FamilyConfiguration.allOf(
                EntityStartComponent::class
            )
        )
        val finishEntity = world.getFirstStatefulEntityMatching(
            FamilyConfiguration.allOf(
                EntityFinishComponent::class
            )
        )
        val addedCheckpoints = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(EntityCheckpointComponent::class)
        )

        val blockingEntities = world.getStatefulEntitySnapshots(
            FamilyConfiguration.allOf(EntityBlockingComponent::class)
        )

        val path = PathFinder.getUpdatablePath(
            width,
            height,
            startEntity.toRectangleEntity(),
            finishEntity.toRectangleEntity(),
            blockingEntities.map { it.toRectangleEntity() },
            addedCheckpoints.map { it.toRectangleEntity() }
        )
    }
}