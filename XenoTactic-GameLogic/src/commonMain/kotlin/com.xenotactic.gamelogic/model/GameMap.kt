package com.xenotactic.gamelogic.model

import com.xenotactic.gamelogic.containers.BlockingPointContainer
import com.xenotactic.gamelogic.utils.sequenceOfNullable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class GameMap(
    val width: Int, val height: Int,
    private var start: MapEntity.Start? = null,
    private var finish: MapEntity.Finish? = null,
    private val checkpoints: MutableList<MapEntity.CheckPoint> = mutableListOf(),
    private val teleportIns: MutableMap<Int, MapEntity.TeleportIn> = mutableMapOf(),
    private val teleportOuts: MutableMap<Int, MapEntity.TeleportOut> = mutableMapOf(),
    private val towers: MutableList<MapEntity.Tower> = mutableListOf(),
    private val rocks: MutableList<MapEntity.Rock> = mutableListOf(),
    private val smallBlockers: MutableList<MapEntity.SmallBlocker> = mutableListOf(),
    private val speedAreas: MutableList<MapEntity.SpeedArea> = mutableListOf()
) {
    @Transient
    private val blockingPoints: BlockingPointContainer.Mutable = BlockingPointContainer.Mutable()

    fun getStart() = start
    fun getFinish() = finish

    val teleportPairs: List<TeleportPair>
        get() = teleportIns.map {
            TeleportPair(
                it.value, teleportOuts[it.key]!!
            )
        }

    val numPathingEntities: Int
        get() = checkpoints.size + 2

    init {
        towers.forEach { blockingPoints.add(it) }
        rocks.forEach { blockingPoints.add(it) }

        require(teleportIns.size == teleportOuts.size)

        for (sequenceNumber in teleportIns.keys) {
            require(teleportOuts.containsKey(sequenceNumber))
        }
    }

    fun getEntitiesForType(entityType: MapEntityType): Sequence<MapEntity> {
        return when (entityType) {
            MapEntityType.START -> sequenceOfNullable(start)
            MapEntityType.FINISH -> sequenceOfNullable(finish)
            MapEntityType.CHECKPOINT -> checkpoints.asSequence()
            MapEntityType.ROCK -> rocks.asSequence()
            MapEntityType.TOWER -> towers.asSequence()
            MapEntityType.TELEPORT_IN -> teleportIns.values.asSequence()
            MapEntityType.TELEPORT_OUT -> teleportOuts.values.asSequence()
            MapEntityType.SMALL_BLOCKER -> smallBlockers.asSequence()
            MapEntityType.SPEED_AREA -> speedAreas.asSequence()
        }
    }

    fun getEntitiesForTypes(vararg entityType: MapEntityType): Sequence<MapEntity> {
        return entityType.asSequence().flatMap { getEntitiesForType(it) }
    }

    fun blockingPointsView(): BlockingPointContainer.View {
        return blockingPoints.toView()
    }

    override fun toString(): String {
        return """
            GameMap(
                start=$start,
                finish=$finish,
                checkpoints=$checkpoints,
                teleportIns=$teleportIns,
                teleportOuts=$teleportOuts,
                towers=$towers,
                rocks=$rocks,
                smallBlockers=$smallBlockers
            )
        """.trimIndent()
    }

    fun placeEntities(vararg entities: MapEntity) {
        for (entity in entities) {
            placeEntity(entity)
        }
    }

    fun placeEntity(entity: MapEntity) {
        when (entity) {
            is MapEntity.Rock -> rocks.add(entity)
            is MapEntity.Start -> start = entity
            is MapEntity.Finish -> finish = entity
            is MapEntity.CheckPoint -> checkpoints.add(entity)
            is MapEntity.TeleportIn -> teleportIns[entity.sequenceNumber] = entity
            is MapEntity.TeleportOut -> teleportOuts[entity.sequenceNumber] = entity
            is MapEntity.Tower -> towers.add(entity)
            is MapEntity.SmallBlocker -> smallBlockers.add(entity)
            is MapEntity.SpeedArea -> speedAreas.add(entity)
        }

        if (entity.isBlockingEntity) {
            blockingPoints.add(entity)
        }
    }

    fun removeEntity(entity: MapEntity) {
        when (entity) {
            is MapEntity.Rock -> rocks.remove(entity)
            is MapEntity.Start -> start = null
            is MapEntity.Finish -> start = null
            is MapEntity.CheckPoint -> checkpoints.remove(entity)
            is MapEntity.TeleportIn -> teleportIns.remove(entity.sequenceNumber)
            is MapEntity.TeleportOut -> teleportOuts.remove(entity.sequenceNumber)
            is MapEntity.Tower -> towers.remove(entity)
            is MapEntity.SpeedArea -> speedAreas.remove(entity)
        }

        if (entity.isBlockingEntity) {
            blockingPoints.removeAll(entity.blockIntPoints)
        }
    }

    fun isPathingEntity(entity: MapEntity): Boolean {
        return when (entity) {
            is MapEntity.TeleportOut,
            is MapEntity.Start,
            is MapEntity.Finish,
            is MapEntity.CheckPoint -> true
            is MapEntity.TeleportIn,
            is MapEntity.Rock,
            is MapEntity.Tower,
            is MapEntity.SpeedArea,
            is MapEntity.SmallBlocker -> false
        }
    }

    /**
     * Returns whether an entity intersects any blocking entities.
     */
    fun intersectsBlockingEntities(entity: MapEntity): Boolean {
        return intersectsBlockingEntities(entity.x, entity.y, entity.width, entity.height)
    }

    fun intersectsBlockingEntities(x: Int, y: Int, width: Int, height: Int): Boolean {
        for (i in 0 until width) {
            for (j in 0 until height) {
                if (blockingPoints.contains(x + i, y + j)) return true
            }
        }
        return false
    }

    fun getAllEntities(): List<MapEntity> {
        return MapEntityType.values().flatMap {
            getEntitiesForType(it)
        }
    }

    fun getAllEntitiesExceptRocks(): List<MapEntity> {
        return listOfNotNull(start, finish) + checkpoints +
                teleportIns.values + teleportOuts.values + towers
    }

    /**
     * Returns whether the provided entity fully covers a pathing entity or not.
     */
    fun fullyCoversAPathingEntity(entity: MapEntity): Boolean {
        return getPathingEntities().any {
            it.isFullyCoveredBy(entity)
        }
    }

    fun getBlockingEntities(): List<MapEntity> {
        return MapEntityType.blockingEntityTypes.flatMap {
            getEntitiesForType(it)
        }
    }

    fun getAllRocksAtPoint(x: Int, y: Int): List<MapEntity.Rock> {
        return rocks.filter { it.intersectsUnitBlock(x, y) }
    }

    fun getFirstRockAt(x: Int, y: Int): MapEntity.Rock? {
        return rocks.firstOrNull { it.intersectsUnitBlock(x, y) }
    }

    fun getFirstTowerAt(x: Int, y: Int): MapEntity.Tower? {
        return towers.firstOrNull { it.intersectsUnitBlock(x, y) }
    }

    fun getPathingEntities(): List<MapEntity> {
        return checkpoints + listOfNotNull(start, finish) + teleportOuts.values
    }

    fun getFirstPathingEntityAt(x: Int, y: Int): MapEntity? {
        return getPathingEntities().firstOrNull { it.intersectsUnitBlock(x, y) }
    }

    /**
     * Returns the next pathing entity after a given entity if it exists.
     *
     * For example if there exists a path from Start -> CP1 -> CP2 -> Finish
     *
     * Then:
     * - getPathingEntityAfter(Start) -> CP1
     * - getPathingEntityAfter(CP1) -> CP2
     * - getPathingEntityAfter(CP2) -> Finish
     * - getPathingEntityAfter(Finish) -> null
     */
    fun getPathingEntityAfter(entity: MapEntity): MapEntity? {
        when (entity) {
            is MapEntity.CheckPoint -> {
                if (entity.sequenceNumber == checkpoints.size - 1) return finish
                return checkpoints[entity.sequenceNumber + 1]
            }
            is MapEntity.Start -> return checkpoints.firstOrNull()
            is MapEntity.Finish -> return null
            is MapEntity.Rock,
            is MapEntity.TeleportIn,
            is MapEntity.TeleportOut,
            is MapEntity.Tower,
            is MapEntity.SmallBlocker,
            is MapEntity.SpeedArea -> return null
        }
    }

    /**
     * Returns the pathing entity at the specified index.
     * For example if we have a path from Start -> CP1 -> CP2 -> Finish.
     * Then
     * - 0 -> Start
     * - 1 -> CP1
     * - 2 -> CP2
     * - 3 -> Finish
     */
    fun getPathingEntity(idx: Int): MapEntity? {
        if (idx == 0) return start
        if (idx == numPathingEntities - 1) return finish
        return checkpoints[idx - 1]
    }

    fun getSequentialPathingEntities(): List<MapEntity> {
        return mutableListOf<MapEntity>().apply {
            add(start!!)
            addAll(checkpoints)
            add(finish!!)
        }
    }

    companion object {
        fun create(width: Int, height: Int, vararg entities: MapEntity): GameMap {
            val gameMap = GameMap(width, height)
            entities.forEach {
                gameMap.placeEntity(it)
            }
            return gameMap
        }
    }
}