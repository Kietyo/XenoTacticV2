package com.xenotactic.gamelogic.views

import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.MaterialColors
import com.soywiz.korma.geom.Anchor
import com.soywiz.korma.geom.vector.StrokeInfo
import com.soywiz.korma.geom.vector.rectHole
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.toWorldDimensions
import com.xenotactic.gamelogic.korge_utils.SpeedAreaColorUtil
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.GlobalResources

class UIEntity(
    val entityType: MapEntityType,
    val entityWidth: GameUnit,
    val entityHeight: GameUnit,
//    val engine: Engine?,
    val gridSize: Double,
    val borderSize: Double,
    val speedEffect: Double? = null,
) : Container() {
    init {
        val (worldWidth, worldHeight) = toWorldDimensions(entityWidth, entityHeight, gridSize)
        println(
            """
            rendering: entityType: $entityType, worldWidth: $worldWidth, worldHeight: $worldHeight
        """.trimIndent()
        )
        when (entityType) {
            MapEntityType.CHECKPOINT -> {
                Circle((worldWidth / 2).value, Colors.MAROON).apply {
                    addTo(this@UIEntity)
                }
            }

            MapEntityType.FINISH -> {
                Circle((worldWidth / 2).value, Colors.MAGENTA).addTo(this)
            }

            MapEntityType.START -> {
                Circle((worldWidth / 2).value, Colors.RED).apply {
                    addTo(this@UIEntity)
                }
            }

            MapEntityType.TOWER -> {
                this.solidRect(
                    worldWidth.value, worldHeight.value,
                    MaterialColors.YELLOW_500
                )
                this.solidRect(
                    (worldWidth - borderSize).value, (worldHeight - borderSize).value,
                    MaterialColors.YELLOW_900
                ).centerOn(this)
            }

            MapEntityType.ROCK -> {
                this.solidRect(
                    worldWidth.value, worldHeight.value,
                    MaterialColors.BROWN_500
                )
                this.solidRect(
                    (worldWidth - borderSize).value, (worldHeight - borderSize).value,
                    MaterialColors.BROWN_900
                ).centerOn(this)
            }

            MapEntityType.TELEPORT_IN -> {
                Circle(worldWidth.value / 2, Colors.GREEN.withAd(0.6)).addTo(this)
            }

            MapEntityType.TELEPORT_OUT -> {
                Circle(worldWidth.value / 2, Colors.RED.withAd(0.6)).addTo(this)
            }

            MapEntityType.SMALL_BLOCKER -> {
                this.solidRect(
                    worldWidth.value, worldHeight.value,
                    MaterialColors.YELLOW_500
                )
                this.solidRect(
                    (worldWidth - borderSize).value, (worldHeight - borderSize).value,
                    MaterialColors.YELLOW_900
                ).centerOn(this)
            }

            MapEntityType.SPEED_AREA -> {
                val speedAreaColor = SpeedAreaColorUtil(
                    speedEffect!!,
                    slowLow = 0.3, slowHigh = 0.9, fastLow = 1.2, fastHigh = 2.0
                ).withAd(0.4)
                Circle(worldWidth.value / 2, speedAreaColor).addTo(this)
            }

            MapEntityType.MONSTER -> {
//                val diameter = worldWidth
                UIEightDirectionalSprite(GlobalResources.MONSTER_SPRITE).addTo(this@UIEntity) {
                    anchor(Anchor.CENTER)
                    scaledWidth = worldWidth.toDouble()
                    scaledHeight = worldHeight.toDouble()
                }
//                Circle((diameter / 2).value, Colors.RED).apply {
//                    addTo(this@UIEntity)
//                    anchor(Anchor.CENTER)
//                }

            }
        }

    }

}