package com.xenotactic.gamelogic.ecs;

import kotlin.reflect.KClass

class Entity(
        val id: Int,
        val mutableComponentService: ComponentService
) {
  inline fun <reified T> getComponent(): T {
    return mutableComponentService.getComponentForEntity<T>(this)
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
    val arr = componentTypeToArray[T::class]
            ?: throw ECSComponentNotFoundException {
              "No component type ${T::class} found for entity: ${entity.id}"
            }
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

