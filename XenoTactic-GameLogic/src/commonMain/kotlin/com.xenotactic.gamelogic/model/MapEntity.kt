package com.xenotactic.gamelogic.model

import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit
import kotlinx.serialization.Serializable

enum class MapEntityType {
    START,
    FINISH,
    CHECKPOINT,
    ROCK,
    TOWER,
    TELEPORT_IN,
    TELEPORT_OUT,
    SMALL_BLOCKER,
    SPEED_AREA;

    sealed class EntitySize {
        // No fixed size
        object Varied : EntitySize()
        data class Fixed(val width: Int, val height: Int) : EntitySize()
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
                }
            }.toSet()

        fun createEntity(entityType: MapEntityType, x: GameUnit, y: GameUnit): MapEntity {
            return when (entityType) {
                START -> MapEntity.Start(x, y)
                FINISH -> MapEntity.Finish(x, y)
                CHECKPOINT -> TODO()
                ROCK -> TODO()
                TOWER -> TODO()
                TELEPORT_IN -> TODO()
                TELEPORT_OUT -> TODO()
                SMALL_BLOCKER -> TODO()
                SPEED_AREA -> TODO()
            }
        }

        fun getEntitySize(entityType: MapEntityType): EntitySize {
            return when (entityType) {
                START,
                FINISH,
                CHECKPOINT,
                TOWER,
                TELEPORT_IN,
                TELEPORT_OUT -> EntitySize.Fixed(2, 2)

                ROCK, SPEED_AREA -> EntitySize.Varied
                SMALL_BLOCKER -> EntitySize.Fixed(1, 1)
            }
        }
    }
}

sealed class MapEntity : IRectangleEntity {
    abstract val type: MapEntityType
    abstract val isBlockingEntity: Boolean

    abstract val friendlyName: String

    val gameUnitPoint: GameUnitPoint
        get() = GameUnitPoint(x, y)

    val topY: GameUnit
        get() = gameUnitPoint.y + height

    val rightX: GameUnit
        get() = gameUnitPoint.x + width

    fun at(x: Int, y: Int): MapEntity {
        val p = GameUnitPoint(x, y)
        return at(p)
    }

    fun at(p: GameUnitPoint): MapEntity {
        return when (this) {
            is Checkpoint -> Checkpoint(this.sequenceNumber, p.x, p.y)
            is Finish -> Finish(p.x, p.y)
            is Rock -> Rock(p.x, p.y, width, height)
            is Start -> Start(p.x, p.y)
            is Tower -> Tower(p.x, p.y)
            is TeleportIn -> TeleportIn(this.sequenceNumber, p.x, p.y)
            is TeleportOut -> TeleportOut(this.sequenceNumber, p.x, p.y)
            is SmallBlocker -> SmallBlocker(p.x, p.y)
            is SpeedArea -> SpeedArea(p.x, p.y, radius, speedEffect)
        }
    }

    fun getGRectInt(): GRectInt {
        return GRectInt(x, y, width, height)
    }

    // Returns whether or not this entity intersects with a 1x1 block
    // at the given x, y position
    fun intersectsUnitBlock(unitX: GameUnit, unitY: GameUnit): Boolean {
        return unitX >= this.x && unitX <= this.rightX &&
                unitY >= this.y && unitY <= this.topY &&
                run {
                    val unitRightX = unitX + 1
                    unitRightX >= x && unitRightX <= rightX
                } &&
                run {
                    val unitTopY = unitY + 1
                    unitTopY >= this.y && unitTopY <= this.topY
                }
    }

    // Returns whether or not this entity intersects with the provided entity.
    fun intersectsEntity2(entity: MapEntity): Boolean {
        return this.blockGameUnitPoints.intersect(entity.blockGameUnitPoints).isNotEmpty()
    }

    fun intersectsEntity(entity: MapEntity): Boolean {
        // I hate this code
        if (this.x == entity.x && this.y == entity.y) {
            return true
        }
        val xProjIntersect =
            (entity.x > this.x && entity.x < this.rightX) ||
                    (this.x > entity.x && this.x < entity.rightX)

        val xRightProjIntersect =
            (entity.rightX > this.x && entity.rightX < this.rightX) ||
                    (this.rightX > entity.x && this.rightX < entity.rightX)

        val yProjIntersect =
            (entity.y > this.y && entity.y < this.topY) ||
                    (this.y > entity.y && this.y < entity.topY)

        val yTopProjIntersect =
            (entity.topY > this.y && entity.topY < this.topY) ||
                    (this.topY > entity.y && this.topY < entity.topY)

        val xEquals = this.x == entity.x
        val yEquals = this.y == entity.y

        //        println(
        //            """
        //            xProjIntersect: $xProjIntersect
        //            xRightProjIntersect: $xRightProjIntersect
        //            yProjIntersect: $yProjIntersect
        //            yTopProjIntersect: $yTopProjIntersect
        //            xEquals: $xEquals
        //            yEquals: $yEquals
        //            \n
        //        """.trimIndent()
        //        )

        return ((xProjIntersect || xRightProjIntersect) && (yProjIntersect || yTopProjIntersect))
                || (yEquals && xProjIntersect)
                || (xEquals && yProjIntersect)
    }

    @Serializable
    data class Start(override val x: GameUnit, override val y: GameUnit) : MapEntity() {
        override val width: GameUnit = 2.toGameUnit()
        override val height: GameUnit = 2.toGameUnit()
        override val type: MapEntityType = MapEntityType.START
        override val friendlyName: String = "Start"
        override val isBlockingEntity: Boolean = false

        constructor(gameUnitPoint: GameUnitPoint) : this(gameUnitPoint.x, gameUnitPoint.y)
    }

    @Serializable
    data class Finish(override val x: GameUnit, override val y: GameUnit) : MapEntity() {
        override val width: GameUnit = 2.toGameUnit()
        override val height: GameUnit = 2.toGameUnit()
        override val type: MapEntityType = MapEntityType.FINISH
        override val friendlyName: String = "Finish"
        override val isBlockingEntity: Boolean = false

        constructor(gameUnitPoint: GameUnitPoint) : this(gameUnitPoint.x, gameUnitPoint.y)
    }

    @Serializable
    data class TeleportIn(
        val sequenceNumber: Int,
        override val x: GameUnit,
        override val y: GameUnit
    ) : MapEntity(
    ) {
        override val width: GameUnit = 2.toGameUnit()
        override val height: GameUnit = 2.toGameUnit()
        override val type: MapEntityType = MapEntityType.TELEPORT_IN
        override val friendlyName: String = "Teleport In $sequenceNumber"
        override val isBlockingEntity: Boolean = false

        constructor(sequenceNumber: Int, gameUnitPoint: GameUnitPoint) : this(
            sequenceNumber, gameUnitPoint.x,
            gameUnitPoint.y
        )

        // The ordinal sequence number (starts at 1).
        val ordinalSequenceNumber: Int
            get() = sequenceNumber + 1

        val radius: Double = 1.0

        infix fun to(tpOut: TeleportOut): TeleportPair {
            return TeleportPair(this, tpOut, this.sequenceNumber)
        }
    }

    @Serializable
    data class TeleportOut(
        val sequenceNumber: Int,
        override val x: GameUnit,
        override val y: GameUnit
    ) : MapEntity(
    ) {
        override val width: GameUnit = 2.toGameUnit()
        override val height: GameUnit = 2.toGameUnit()
        override val type: MapEntityType = MapEntityType.TELEPORT_OUT
        override val friendlyName: String = "Teleport Out $sequenceNumber"
        override val isBlockingEntity: Boolean = false

        constructor(sequenceNumber: Int, gameUnitPoint: GameUnitPoint) : this(
            sequenceNumber, gameUnitPoint.x,
            gameUnitPoint.y
        )

        // The ordinal sequence number (starts at 1).
        val ordinalSequenceNumber: Int
            get() = sequenceNumber + 1
    }

    @Serializable
    data class Rock(
        override val x: GameUnit, override val y: GameUnit,
        override val width: GameUnit,
        override val height: GameUnit,
    ) : MapEntity(
    ) {
        override val type: MapEntityType = MapEntityType.ROCK

        override val friendlyName: String = "Rock (${width}x${height})"
        override val isBlockingEntity: Boolean = true

        constructor(gameUnitPoint: GameUnitPoint, width: GameUnit, height: GameUnit) : this(
            gameUnitPoint.x, gameUnitPoint.y,
            width, height
        )

        init {
            require(width >= 0)
            require(height >= 0)
        }
    }

    /**
     * Sequence number starts at 0.
     */
    @Serializable
    data class Checkpoint(
        val sequenceNumber: Int,
        override val x: GameUnit,
        override val y: GameUnit
    ) :
        MapEntity() {
        override val width: GameUnit = 2.toGameUnit()
        override val height: GameUnit = 2.toGameUnit()
        override val type: MapEntityType = MapEntityType.CHECKPOINT
        override val friendlyName: String = "Checkpoint $sequenceNumber"
        override val isBlockingEntity: Boolean = false

        constructor(sequenceNumber: Int, gameUnitPoint: GameUnitPoint) : this(
            sequenceNumber, gameUnitPoint.x,
            gameUnitPoint.y
        )

        // The ordinal sequence number (starts at 1).
        val ordinalSequenceNumber: Int
            get() = sequenceNumber + 1
    }

    @Serializable
    data class Tower(override val x: GameUnit, override val y: GameUnit) : MapEntity() {
        override val width: GameUnit = 2.toGameUnit()
        override val height: GameUnit = 2.toGameUnit()
        override val type: MapEntityType = MapEntityType.TOWER
        override val friendlyName: String = "Tower"
        override val isBlockingEntity: Boolean = true

        constructor(gameUnitPoint: GameUnitPoint) : this(
            gameUnitPoint.x,
            gameUnitPoint.y
        )
    }

    @Serializable
    data class SmallBlocker(
        override val x: GameUnit,
        override val y: GameUnit,
    ) : MapEntity() {
        override val width: GameUnit = 1.toGameUnit()
        override val height: GameUnit = 1.toGameUnit()
        override val type: MapEntityType = MapEntityType.SMALL_BLOCKER
        override val friendlyName: String = "Small Blocker"
        override val isBlockingEntity: Boolean = true
    }

    @Serializable
    data class SpeedArea(
        override val x: GameUnit,
        override val y: GameUnit,
        val radius: GameUnit,
        val speedEffect: Double
    ) : MapEntity() {
        override val width: GameUnit = radius * 2
        override val height: GameUnit = radius * 2
        override val type: MapEntityType = MapEntityType.SPEED_AREA
        override val isBlockingEntity: Boolean = false
        override val friendlyName: String = "Speed Area"

        fun getSpeedText() = "${(speedEffect * 100).toInt()}%"
    }

    fun toMapEntityData(): MapEntityData {
        return when (this) {
            is Checkpoint -> MapEntityData.Checkpoint(sequenceNumber)
            is Finish -> MapEntityData.Finish
            is Rock -> MapEntityData.Rock
            is SmallBlocker -> MapEntityData.SmallBlocker
            is SpeedArea -> MapEntityData.SpeedArea(radius, speedEffect)
            is Start -> MapEntityData.Start
            is TeleportIn -> MapEntityData.TeleportIn(sequenceNumber)
            is TeleportOut -> MapEntityData.TeleportOut(sequenceNumber)
            is Tower -> MapEntityData.Tower
        }
    }

    companion object {
        val CHECKPOINT = Checkpoint(0, 0.toGameUnit(), 0.toGameUnit())
        val TELEPORT_IN = TeleportIn(0, 0.toGameUnit(), 0.toGameUnit())
        val TELEPORT_OUT = TeleportOut(0, 0.toGameUnit(), 0.toGameUnit())
        val ROCK_1X1 = Rock(0.toGameUnit(), 0.toGameUnit(), 1.toGameUnit(), 1.toGameUnit())
        val ROCK_2X4 = Rock(0.toGameUnit(), 0.toGameUnit(), 2.toGameUnit(), 4.toGameUnit())
        val ROCK_4X2 = Rock(0.toGameUnit(), 0.toGameUnit(), 4.toGameUnit(), 2.toGameUnit())

        // Returns true if the `rhs` fully covers the `lhs`
        fun fullyCovers(lhs: MapEntity, rhs: MapEntity): Boolean {
            val lhsPoints = lhs.blockGameUnitPoints
            val rhsPoints = rhs.blockGameUnitPoints
            return lhsPoints.intersect(rhsPoints).size == lhsPoints.size
        }
    }
}


data class TeleportPair(
    val teleportIn: IRectangleEntity,
    val teleportOut: IRectangleEntity,
    val sequenceNumber: Int
) {
    constructor(teleportIn: MapEntity.TeleportIn, teleportOut: MapEntity.TeleportOut) : this(
        teleportIn, teleportOut, teleportIn.sequenceNumber
    )
}