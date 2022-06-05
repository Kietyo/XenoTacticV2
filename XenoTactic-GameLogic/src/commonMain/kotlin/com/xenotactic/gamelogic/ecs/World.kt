package com.xenotactic.gamelogic.ecs;

import kotlin.reflect.KClass

class Entity(
        val id: Int,
        val componentService: ComponentService
) {
  inline fun <reified T> getComponent(): T {
    return componentService.getComponentForEntity<T>(this)
  }
  inline fun <reified T> getComponentOrNull(): T? {
    return componentService.getComponentForEntityOrNull<T>(this)
  }
}

class EntityService() {
  private var nextId: Int = 0

  fun getNewEntityId(): Int {
    return nextId++
  }
}

abstract class ComponentService {
  open val componentTypeToArray: Map<KClass<out Any>, ArrayList<Any>> = emptyMap()

  inline fun <reified T> getComponentForEntity(entity: Entity): T {
    return getComponentForEntityOrNull(entity) ?: throw ECSComponentNotFoundException {
      "No component type ${T::class} found for entity: ${entity.id}"
    }
  }

  inline fun <reified T> getComponentForEntityOrNull(entity: Entity): T? {
    val arr = componentTypeToArray[T::class] ?: return null
    return arr[entity.id] as T
  }
}

class MutableComponentService(): ComponentService() {
  override val componentTypeToArray = mutableMapOf<KClass<out Any>, ArrayList<Any>>()

  fun <T> addComponentForEntity(entityId: Int, component: T) {
    val list = componentTypeToArray.getOrPut(component!!::class) {
      ArrayList()
    }
    list.add(entityId, component)
  }

  // Returns true if the entity had the component, and the component was removed.
//  inline fun <reified T> removeComponentForEntity(entity: Entity): Boolean {
//    val list = componentTypeToArray.get(T::class)
//    list.
//  }
}

class EntityBuilder(
        val entityId: Int,
        private val mutableComponentService: MutableComponentService
) {
  fun <T> addComponent(component: T) {
    mutableComponentService.addComponentForEntity(entityId, component)
  }
}

class World {
  val entityService = EntityService()
  val mutableComponentService = MutableComponentService()

  fun addEntity(builder: EntityBuilder.() -> Unit): Entity {
    val id = entityService.getNewEntityId()
    builder(EntityBuilder(id, mutableComponentService))
    return Entity(id, mutableComponentService)
  }
}

object PreSelectComponent
object SelectedCoponent

