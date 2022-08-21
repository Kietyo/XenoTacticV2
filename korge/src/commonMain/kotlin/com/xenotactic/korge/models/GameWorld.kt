package com.xenotactic.korge.models

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.*

class GameWorld(
    val world: World = World()
) {
    val entityFamily = world.createFamily(
        FamilyConfiguration(
            allOfComponents = setOf(
                MapEntityComponent::class,
                UIMapEntityComponent::class,
                SizeComponent::class,
                BottomLeftPositionComponent::class,
            )
        )
    )
    val uiEntityFamily = world.createFamily(
        FamilyConfiguration(
            allOfComponents = setOf(UIMapEntityComponent::class)
        )
    )
    val bottomLeftPositionComponent =
        world.getComponentContainer<BottomLeftPositionComponent>()
    val sizeComponent = world.getComponentContainer<SizeComponent>()
    val mapEntityComponent = world.getComponentContainer<MapEntityComponent>()
    val uiMapEntityComponentContainer =
        world.getComponentContainer<UIMapEntityComponent>()
    val uiMapEntityTextComponentContainer =
        world.getComponentContainer<UIMapEntityTextComponent>()
}