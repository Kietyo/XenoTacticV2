package com.xenotactic.gamelogic.ecs

class EntityIdService() {
    private var nextId: Int = 0

    fun getNewEntityId(): Int {
        return nextId++
    }
}
