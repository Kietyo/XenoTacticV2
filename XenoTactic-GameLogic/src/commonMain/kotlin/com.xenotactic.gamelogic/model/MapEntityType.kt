package com.xenotactic.gamelogic.model

import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit

enum class MapEntityType {
    START,
    FINISH,
    CHECKPOINT,
    ROCK,
    TOWER,
    TELEPORT_IN,
    TELEPORT_OUT,
    SMALL_BLOCKER,
    SPEED_AREA,
    MONSTER,
    SUPPLY_DEPOT;

    sealed class EntitySize {
        // No fixed size
        object Varied : EntitySize()
        data class Fixed(val width: GameUnit, val height: GameUnit) : EntitySize()
    }

    companion object {
        val blockingEntityTypes =
            MapEntityType.values().filter {
                when (it) {
                    START -> false
                    FINISH -> false
                    CHECKPOINT -> false
                    ROCK -> true
                    TOWER -> true
                    TELEPORT_IN -> false
                    TELEPORT_OUT -> false
                    SMALL_BLOCKER -> true
                    SPEED_AREA -> false
                    MONSTER -> false
                    SUPPLY_DEPOT -> true
                }
            }.toSet()

//        fun createEntity(entityType: MapEntityType, x: Int, y: Int): MapEntity {
//            return createEntity(entityType, x.toGameUnit(), y.toGameUnit())
//        }
//
//        fun createEntity(entityType: MapEntityType, x: GameUnit, y: GameUnit): MapEntity {
//            return when (entityType) {
//                START -> MapEntity.Start(x, y)
//                FINISH -> MapEntity.Finish(x, y)
//                CHECKPOINT -> TODO()
//                ROCK -> TODO()
//                TOWER -> TODO()
//                TELEPORT_IN -> TODO()
//                TELEPORT_OUT -> TODO()
//                SMALL_BLOCKER -> TODO()
//                SPEED_AREA -> TODO()
//                MONSTER -> TODO()
//            }
//        }

        fun getEntitySize(entityType: MapEntityType): EntitySize {
            return when (entityType) {
                START,
                FINISH,
                CHECKPOINT,
                TOWER,
                TELEPORT_IN,
                TELEPORT_OUT,
                SUPPLY_DEPOT-> EntitySize.Fixed(2.toGameUnit(), 2.toGameUnit())

                ROCK, SPEED_AREA -> EntitySize.Varied
                SMALL_BLOCKER -> EntitySize.Fixed(1.toGameUnit(), 1.toGameUnit())
                MONSTER -> EntitySize.Varied
            }
        }
    }
}