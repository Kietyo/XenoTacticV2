package com.xenotactic.gamelogic.ecs

interface ComponentListener<T> {
    // Listener for when a component gets added to the entity.
    fun onAdd(entity: Entity, component: T)

    // Listener for when a component gets removed from an entity.
    fun onRemove(entity: Entity, component: T)
}