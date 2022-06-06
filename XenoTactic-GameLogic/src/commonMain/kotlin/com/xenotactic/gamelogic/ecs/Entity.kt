package com.xenotactic.gamelogic.ecs

import kotlin.reflect.KClass

class EntityBuilder(
    private val entity: Entity,
    private val componentService: ComponentService
) {
    fun <T> addOrReplaceComponent(component: T) {
        componentService.addOrReplaceComponentForEntity(entity, component)
    }
}

class Entity(
    val id: Int,
    val componentService: ComponentService
) {
    inline fun <T> addOrReplaceComponent(component: T) {
        componentService.addOrReplaceComponentForEntity(this, component)
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