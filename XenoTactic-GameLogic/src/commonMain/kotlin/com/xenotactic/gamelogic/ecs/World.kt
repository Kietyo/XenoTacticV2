package com.xenotactic.gamelogic.ecs;

import kotlin.reflect.KClass

class Entity(
    val id: Int,
    val componentService: ComponentService
) {
    inline fun <T> addOrReplaceComponent(component: T) {
        componentService.addOrReplaceComponentForEntity(id, component)
    }

    inline fun <reified T> getComponent(): T {
        return componentService.getComponentForEntity<T>(this)
    }

    inline fun <reified T> getComponentOrNull(): T? {
        return componentService.getComponentForEntityOrNull<T>(this)
    }

    inline fun containsComponent(klass: KClass<*>): Boolean {
        return componentService.containsComponentForEntity(klass, this)
    }

    fun matchesFamilyConfiguration(familyConfiguration: FamilyConfiguration): Boolean {
        return familyConfiguration.allOfComponents.all {
            containsComponent(it)
        } && (familyConfiguration.anyOfComponents.isEmpty() || familyConfiguration.anyOfComponents.any {
            containsComponent(it)
        }) && familyConfiguration.noneOfComponents.none {
            containsComponent(it)
        }
    }
}

class EntityIdService() {
    private var nextId: Int = 0

    fun getNewEntityId(): Int {
        return nextId++
    }
}

class ComponentEntityContainer<T>() {
    val entityIdToComponentMap: MutableMap<Int, T> = mutableMapOf()

    fun setComponent(entityId: Int, component: T) {
        entityIdToComponentMap.put(entityId, component)
    }

    fun getComponentOrNull(entityId: Int): T? {
        return entityIdToComponentMap.get(entityId)
    }

    fun removeComponent(entityId: Int): T? {
        return entityIdToComponentMap.remove(entityId)
    }

    fun containsComponent(entityId: Int): Boolean {
        return entityIdToComponentMap.containsKey(entityId)
    }
}

class EntityBuilder(
    val entityId: Int,
    private val componentService: ComponentService
) {
    fun <T> addOrReplaceComponent(component: T) {
        componentService.addOrReplaceComponentForEntity(entityId, component)
    }
}

class World {
    private val entityIdService = EntityIdService()
    private val componentService = ComponentService(this)
    private val familyService = FamilyService(this)

    internal val entities = arrayListOf<Entity>()

    fun addEntity(builder: EntityBuilder.() -> Unit): Entity {
        val id = entityIdService.getNewEntityId()
        builder(EntityBuilder(id, componentService))
        val newEntity = Entity(id, componentService)
        entities.add(newEntity)

        familyService.updateFamiliesWithNewEntity(newEntity)
        return newEntity
    }

    fun addFamily(familyConfiguration: FamilyConfiguration): Family {
        return familyService.createFamily(familyConfiguration)
    }
}
