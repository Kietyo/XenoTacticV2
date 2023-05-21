package com.xenotactic.gamelogic.views

import korlibs.korge.view.*
import korlibs.image.color.Colors
import korlibs.image.color.MaterialColors
import korlibs.math.geom.Anchor
import com.xenotactic.gamelogic.utils.SpeedAreaColorUtil
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.GlobalResources
import com.xenotactic.gamelogic.utils.toWorldDimensions
import korlibs.korge.view.align.centerOn

class UIEntity(
    val entityType: MapEntityType,
    val entityWidth: GameUnit,
    val entityHeight: GameUnit,
    //    val engine: Engine?,
    gridSize: Number,
    borderSize: Number,
    val speedEffect: Double? = null,
) : Container() {
    val gridSize = gridSize.toFloat()
    val borderSize = borderSize.toFloat()

    init {
        val (worldWidth, worldHeight) = toWorldDimensions(entityWidth, entityHeight, gridSize)
        println(
            """
            rendering: entityType: $entityType, worldWidth: $worldWidth, worldHeight: $worldHeight
        """.trimIndent()
        )
        when (entityType) {
            MapEntityType.CHECKPOINT -> {
                Circle((worldWidth / 2).toFloat(), Colors.MAROON).apply {
                    addTo(this@UIEntity)
                }
            }

            MapEntityType.FINISH -> {
                Circle((worldWidth / 2).toFloat(), Colors.MAGENTA).addTo(this)
            }

            MapEntityType.START -> {
                Circle((worldWidth / 2).toFloat(), Colors.RED).apply {
                    addTo(this@UIEntity)
                }
            }

            MapEntityType.TOWER -> {
                this.solidRect(
                    worldWidth.toFloat(), worldHeight.toFloat(),
                    MaterialColors.YELLOW_500
                )
                this.solidRect(
                    (worldWidth - borderSize).value, (worldHeight - borderSize).value,
                    MaterialColors.YELLOW_900
                ).centerOn(this)
            }

            MapEntityType.ROCK -> {
                this.solidRect(
                    worldWidth.toFloat(), worldHeight.toFloat(),
                    MaterialColors.BROWN_500
                )
                this.solidRect(
                    (worldWidth - borderSize).value, (worldHeight - borderSize).value,
                    MaterialColors.BROWN_900
                ).centerOn(this)
            }

            MapEntityType.TELEPORT_IN -> {
                Circle(worldWidth.toFloat() / 2, Colors.GREEN.withAd(0.6)).addTo(this)
            }

            MapEntityType.TELEPORT_OUT -> {
                Circle(worldWidth.toFloat() / 2, Colors.RED.withAd(0.6)).addTo(this)
            }

            MapEntityType.SMALL_BLOCKER -> {
                this.solidRect(
                    worldWidth.toFloat(), worldHeight.toFloat(),
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
                Circle(worldWidth.toFloat() / 2, speedAreaColor).addTo(this)
            }

            MapEntityType.MONSTER -> {
                //                val diameter = worldWidth
                UIEightDirectionalSprite(GlobalResources.MONSTER_SPRITE).addTo(this@UIEntity) {
                    anchor(Anchor.CENTER)
                    scaledWidth = worldWidth.toFloat()
                    scaledHeight = worldHeight.toFloat()
                }
                //                Circle((diameter / 2).value, Colors.RED).apply {
                //                    addTo(this@UIEntity)
                //                    anchor(Anchor.CENTER)
                //                }

            }

            MapEntityType.SUPPLY_DEPOT -> {
                solidRect(
                    worldWidth.toFloat(), worldHeight.toFloat(),
                    MaterialColors.BROWN_500
                )
            }
        }

    }

}