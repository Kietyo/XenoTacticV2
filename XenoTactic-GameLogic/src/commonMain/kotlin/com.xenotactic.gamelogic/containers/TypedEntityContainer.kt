package com.xenotactic.gamelogic.containers

import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.until

class TypedEntityContainer<T : MapEntity> : EntityContainer<T> {
    // Map of <x, <y, Entity>>
    private val entities = mutableMapOf<Int, MutableMap<Int, T>>()

    override fun placeEntity(entity: T) {
        for (i in 0 until entity.width) {
            for (j in 0 until entity.height) {
                println("i: $i, j: $j")
                placeEntity(entity.x + i, entity.y + j, entity)
            }
        }
    }

    //    fun getOrCreate(x: Int, y: Int): T {
    //        val blah = entities.getOrPut(x) { mutableMapOf() }
    //        return blah.getOrPut(y) { T(x, y) }
    //    }

    fun get(x: Int, y: Int): T {
        return entities[x]!![y]!!
    }

    fun placeEntity(x: GameUnit, y: GameUnit, entity: T) {
        placeEntity(x.toInt(), y.toInt(), entity)
    }

    fun placeEntity(x: Int, y: Int, entity: T) {
        val yMap = entities.getOrPut(x) { mutableMapOf() }
        yMap[y] = entity
    }

    fun containsEntity(entity: MapEntity): Boolean {
        return containsEntity(entity.x, entity.y, entity.width, entity.height)
    }

    /**
     * Returns true if there contains an entity at the given (x, y) position.
     */
    fun containsEntity(x: Int, y: Int): Boolean {
        if (!entities.containsKey(x)) return false
        return entities[x]!!.containsKey(y)
    }

    fun containsEntity(x: GameUnit, y: GameUnit, width: GameUnit, height: GameUnit): Boolean {
        return containsEntity(x.toInt(), y.toInt(), width.toInt(), height.toInt())
    }

    /**
     * Returns true if there contains an entity within the rectangular region.
     */
    fun containsEntity(x: Int, y: Int, width: Int, height: Int): Boolean {
        require(width > 0)
        require(height > 0)
        for (i in 0 until width) {
            for (j in 0 until height) {
                if (!containsEntity(x + i, y + j)) return false
            }
        }
        return true
    }

    override fun getAllEntities(): List<T> {
        return entities.flatMap {
            it.value.values
        }
    }

    override fun removeEntity(entity: T) {
        TODO("Not yet implemented")
    }
}