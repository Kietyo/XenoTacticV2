package com.xenotactic.gamelogic.containers

import com.xenotactic.gamelogic.model.MapEntity

class ArrayEntityContainer : EntityContainer<MapEntity> {

    private val entities = mutableListOf<MapEntity>()

    override fun placeEntity(entity: MapEntity) {
        entities.add(entity)
    }


    override fun getAllEntities(): List<MapEntity> {
        return entities.toList()
    }

    override fun removeEntity(entity: MapEntity) {
        entities.remove(entity)
    }
}