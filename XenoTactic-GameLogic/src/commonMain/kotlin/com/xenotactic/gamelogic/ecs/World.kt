package com.xenotactic.gamelogic.ecs;

class World {
    private val entityIdService = EntityIdService()
    internal val componentService = ComponentService(this)
    private val familyService = FamilyService(this)

    internal val entities = arrayListOf<Entity>()

    fun addEntity(builder: EntityBuilder.() -> Unit = {}): Entity {
        val id = entityIdService.getNewEntityId()
        val newEntity = Entity(id, componentService)
        builder(EntityBuilder(newEntity, componentService))
        entities.add(newEntity)

        familyService.updateFamiliesWithNewEntity(newEntity)
        return newEntity
    }

    fun addFamily(familyConfiguration: FamilyConfiguration): Family {
        return familyService.createFamily(familyConfiguration)
    }

    internal inline fun <reified T> addComponentListener(listener: ComponentListener<T>) {
        componentService.addComponentListener(listener)
    }
}
