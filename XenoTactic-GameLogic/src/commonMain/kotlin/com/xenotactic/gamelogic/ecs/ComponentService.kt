package com.xenotactic.gamelogic.ecs

import kotlin.reflect.KClass

class ComponentService(
    val world: World
) {
    val componentTypeToArray = mutableMapOf<KClass<out Any>, ComponentEntityContainer<Any>>()

    inline fun <reified T> getComponentForEntity(entity: Entity): T {
        return getComponentForEntityOrNull(entity) ?: throw ECSComponentNotFoundException {
            "No component type ${T::class} found for entity: ${entity.id}"
        }
    }

    inline fun <reified T> getComponentForEntityOrNull(entity: Entity): T? {
        val arr = componentTypeToArray[T::class] ?: return null
        return arr.getComponentOrNull(entity) as T
    }

    fun containsComponentForEntity(kClass: KClass<*>, entity: Entity): Boolean {
        val arr = componentTypeToArray[kClass] ?: return false
        return arr.containsComponent(entity)
    }

    fun <T> addOrReplaceComponentForEntity(entity: Entity, component: T) {
        val klass = component!!::class
        val container = componentTypeToArray.getOrPut(component!!::class) {
            ComponentEntityContainer()
        }
        container.addOrReplaceComponent(entity, component)
    }

    fun getOrPutContainer(klass: KClass<*>): ComponentEntityContainer<Any> {
        return componentTypeToArray.getOrPut(klass) {
            ComponentEntityContainer()
        }
    }

    // Returns true if the entity had the component, and the component was removed.
    inline fun <reified T> removeComponentForEntity(entity: Entity): T? {
        val container = componentTypeToArray[T::class] ?: return null
        return container.removeComponent(entity) as T
    }

    inline fun <reified T> addComponentListener(listener: ComponentListener<T>) {
        val container = getOrPutContainer(T::class) as ComponentEntityContainer<T>
        container.addListener(listener)
    }
}