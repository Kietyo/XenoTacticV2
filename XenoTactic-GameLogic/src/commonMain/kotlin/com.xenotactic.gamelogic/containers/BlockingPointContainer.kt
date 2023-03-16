package com.xenotactic.gamelogic.containers

import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.IRectangleEntity
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.utils.GameUnit
import kotlinx.serialization.Serializable

/**
 * Container for storing number of blocking entities for a specific point.
 * If multiple blocking entities cover the same point, then the counter represents that number.
 */
@Serializable
sealed class BlockingPointContainer(
    private val pointToCounter: Map<Int, Map<Int, Int>>
) {
    var size = 0
        protected set

    fun contains(x: GameUnit, y: GameUnit): Boolean = contains(x.toInt(), y.toInt())
    fun contains(x: Int, y: Int): Boolean {
        return pointToCounter.containsKey(x) && pointToCounter[x]!!.getOrElse(y) {0} > 0
    }

    @Serializable
    class Mutable(
        // Map of <x, y> to counter val
        private val mutablePointToCounter: MutableMap<Int, MutableMap<Int, Int>> = mutableMapOf()
    ) : BlockingPointContainer(mutablePointToCounter) {
        fun add(entity: IRectangleEntity) {
            addAll(entity.blockGameUnitPoints)
        }

        fun add(x: Int, y: Int) {
            val yMap = mutablePointToCounter.getOrPut(x) {
                mutableMapOf()
            }
            yMap[y] = yMap.getOrElse(y) { 0 } + 1
            size++

        }

        fun addAll(gameUnitPoints: Collection<GameUnitTuple>) {
            for (point in gameUnitPoints) {
                add(point.x.toInt(), point.y.toInt())
            }
        }

        fun remove(x: Int, y: Int) {
            val yMap = mutablePointToCounter.getOrPut(x) {
                mutableMapOf()
            }

            val newValue = yMap.getOrPut(y) { 0 } - 1
            if (newValue >= 0) {
                yMap[y] = newValue
                size--
            }
        }

        fun removeAll(gameUnitPoints: Collection<GameUnitTuple>) {
            for (point in gameUnitPoints) {
                remove(point.x.toInt(), point.y.toInt())
            }
        }

        fun toView(): View {
            return View(mutablePointToCounter)
        }
    }

    class View(
        mutablePointToCounter: MutableMap<Int, MutableMap<Int, Int>> = mutableMapOf()
    ): BlockingPointContainer(mutablePointToCounter) {
        companion object {
            val EMPTY = View()

            fun create(entities: Collection<IRectangleEntity>): View {
                val blockingPoints = Mutable()
                for (entity in entities) {
                    blockingPoints.add(entity)
                }
                return blockingPoints.toView()
            }

            fun create(vararg entities: MapEntity): View {
                return create(entities.toList())
            }
        }
    }
}