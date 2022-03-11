package com.xenotactic.gamelogic.containers

import com.xenotactic.gamelogic.model.MapEntity

sealed interface EntityContainer<T : MapEntity> {
    fun placeEntity(entity: T)
    fun removeEntity(entity: T)
    fun getAllEntities(): List<T>
}