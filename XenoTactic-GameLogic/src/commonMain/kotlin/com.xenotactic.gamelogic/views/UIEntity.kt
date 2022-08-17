package com.xenotactic.gamelogic.views

import com.soywiz.korge.view.Circle
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Graphics
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.centerOn
import com.soywiz.korge.view.container
import com.soywiz.korge.view.solidRect
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.MaterialColors
import com.soywiz.korma.geom.vector.StrokeInfo
import com.soywiz.korma.geom.vector.rectHole
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.toWorldDimensions
import com.xenotactic.gamelogic.korge_utils.SpeedAreaColorUtil

class UIEntity(
    val entityType: MapEntityType,
    val entityWidth: Int,
    val entityHeight: Int,
//    val engine: Engine?,
    val gridSize: Double,
    val borderSize: Double,
    val speedEffect: Double? = null,
) : Container() {
    init {
        val (worldWidth, worldHeight) = toWorldDimensions(entityWidth, entityHeight, gridSize)
        when (entityType) {
            MapEntityType.CHECKPOINT -> {
                Circle(worldWidth / 2, Colors.MAROON).addTo(this)
            }
            MapEntityType.FINISH -> {
                Circle(worldWidth / 2, Colors.MAGENTA).addTo(this)
            }
            MapEntityType.START -> {
                Circle(worldWidth / 2, Colors.RED).addTo(this)
            }
            MapEntityType.TOWER -> {
                this.solidRect(
                    worldWidth, worldHeight,
                    MaterialColors.YELLOW_500
                )
                this.solidRect(
                    worldWidth - borderSize, worldHeight - borderSize,
                    MaterialColors.YELLOW_900
                ).centerOn(this)
            }
            MapEntityType.ROCK -> {
                this.solidRect(
                    worldWidth, worldHeight,
                    MaterialColors.BROWN_500
                )
                this.solidRect(
                    worldWidth - borderSize, worldHeight - borderSize,
                    MaterialColors.BROWN_900
                ).centerOn(this)
            }
            MapEntityType.TELEPORT_IN -> {
                Circle(worldWidth / 2, Colors.GREEN.withAd(0.6)).addTo(this)
            }
            MapEntityType.TELEPORT_OUT -> {
                Circle(worldWidth / 2, Colors.RED.withAd(0.6)).addTo(this)
            }
            MapEntityType.SMALL_BLOCKER -> {
                this.solidRect(
                    worldWidth, worldHeight,
                    MaterialColors.YELLOW_500
                )
                this.solidRect(
                    worldWidth - borderSize, worldHeight - borderSize,
                    MaterialColors.YELLOW_900
                ).centerOn(this)
            }
            MapEntityType.SPEED_AREA -> {
                val speedAreaColor = SpeedAreaColorUtil(
                    speedEffect!!,
                    slowLow = 0.3, slowHigh = 0.9, fastLow = 1.2, fastHigh = 2.0
                ).withAd(0.7)
                Circle(worldWidth / 2, speedAreaColor).addTo(this)
            }
        }

//        onClick {
//            engine?.eventBus?.send(
//                UIEntityClickedEvent(this, entity)
//            )
//            println(
//                """
//                    entity clicked: $entity
//                """.trimIndent()
//            )
//        }
    }

    var selectionLayer = this.container()

    var selectionBox: Graphics? = null

    fun cancelSelection() {
        selectionBox?.removeFromParent()
        selectionBox = null
    }

    val IN_PROCESS_SELECTION_COLOR = Colors.YELLOW.withAd(0.5)
    val SELECTION_COLOR = Colors.YELLOW

    fun doInProcessSelection() {
        if (selectionBox == null) {
            val (worldWidth, worldHeight) = toWorldDimensions(entityWidth, entityHeight, gridSize)
            selectionBox = Graphics().addTo(this).apply {
                updateShape {
                    stroke(IN_PROCESS_SELECTION_COLOR, StrokeInfo(6.0)) {
                        this.rectHole(0.0, 0.0, worldWidth, worldHeight)
                    }
                }
                centerOn(this)
            }
        }
    }

    fun doEndSelection() {
        if (selectionBox != null) cancelSelection()
        if (selectionBox == null) {
            val (worldWidth, worldHeight) = toWorldDimensions(entityWidth, entityHeight, gridSize)
            selectionBox = Graphics().addTo(this).apply {
                updateShape {
                    stroke(SELECTION_COLOR, StrokeInfo(6.0)) {
                        this.rectHole(0.0, 0.0, worldWidth, worldHeight)
                    }
                }
                centerOn(this)
            }
        }
    }
}