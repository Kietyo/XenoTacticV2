package com.xenotactic.gamelogic.ecs

class ComponentEntityContainer<T>() {
    private val entityIdToComponentMap: MutableMap<Int, T> = mutableMapOf()
    private val listeners = mutableListOf<ComponentListener<T>>()

    fun addOrReplaceComponent(entity: Entity, component: T) {
        entityIdToComponentMap[entity.id] = component
        listeners.forEach {
            it.onAdd(entity, component)
        }
    }

    fun getComponentOrNull(entity: Entity): T? {
        return entityIdToComponentMap[entity.id]
    }

    fun removeComponent(entity: Entity): T? {
        val removedComponent = entityIdToComponentMap.remove(entity.id) ?: return null
        listeners.forEach { it.onRemove(entity, removedComponent) }
        return removedComponent
    }

    fun containsComponent(entity: Entity): Boolean {
        return entityIdToComponentMap.containsKey(entity.id)
    }

    fun addListener(listener: ComponentListener<T>) {
        listeners.add(listener)
    }
}