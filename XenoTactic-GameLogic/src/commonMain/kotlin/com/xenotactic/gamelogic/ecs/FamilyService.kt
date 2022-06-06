package com.xenotactic.gamelogic.ecs

import kotlin.reflect.KClass

data class FamilyConfiguration(
    val allOfComponents: List<KClass<*>> = emptyList(),
    val anyOfComponents: List<KClass<Any>> = emptyList(),
    val noneOfComponents: List<KClass<Any>> = emptyList()
)

data class Family(
    private var entities: ArrayList<Entity>
) {
    fun getEntities() : List<Entity> = entities

    internal fun addEntity(entity: Entity) {
        entities.add(entity)
    }
}

data class FamilyNode(
    var numInstances: Int,
    val family: Family
)

class FamilyService(
    val world: World
) {

    val families = mutableMapOf<FamilyConfiguration, FamilyNode>()

    fun updateFamiliesWithNewEntity(newEntity: Entity) {
        for ((config, node) in families) {
            if (newEntity.matchesFamilyConfiguration(config)) {
                node.family.addEntity(newEntity)
            }
        }
    }

    fun createFamily(familyConfiguration: FamilyConfiguration): Family {
        val node = families.getOrPut(familyConfiguration) {
            FamilyNode(0,
                Family(
                    kotlin.run {
                        val entities = world.entities.filter {
                            it.matchesFamilyConfiguration(familyConfiguration)
                        }
                        val arr = ArrayList<Entity>(entities.size)
                        entities.forEach { arr.add(it) }
                        arr
                    }
                )
            )
        }
        node.numInstances++
        return node.family
    }
}