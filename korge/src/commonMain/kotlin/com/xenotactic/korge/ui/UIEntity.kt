package com.xenotactic.korge.ui

import com.soywiz.korge.view.Circle
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Graphics
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.centerOn
import com.soywiz.korge.view.container
import com.soywiz.korge.view.solidRect
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.vector.rectHole
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.utils.toWorldDimensions
import com.xenotactic.korge.korge_utils.MaterialColors
import com.xenotactic.korge.korge_utils.SpeedAreaColorUtil

class UIEntity(
    val entity: MapEntity,
//    val engine: Engine?,
    val gridSize: Double,
    val borderSize: Double
) : Container() {
    init {
        val (worldWidth, worldHeight) = toWorldDimensions(entity, gridSize)
        when (entity) {
            is MapEntity.CheckPoint -> {
                Circle(worldWidth / 2, Colors.MAROON).addTo(this)
            }
            is MapEntity.Finish -> {
                Circle(worldWidth / 2, Colors.MAGENTA).addTo(this)
            }
            is MapEntity.Start -> {
                Circle(worldWidth / 2, Colors.RED).addTo(this)
            }
            is MapEntity.Tower -> {
                this.solidRect(
                    worldWidth, worldHeight,
                    MaterialColors.YELLOW_500
                )
                this.solidRect(
                    worldWidth - borderSize, worldHeight - borderSize,
                    MaterialColors.YELLOW_900
                ).centerOn(this)
            }
            is MapEntity.Rock -> {
                this.solidRect(
                    worldWidth, worldHeight,
                    MaterialColors.BROWN_500
                )
                this.solidRect(
                    worldWidth - borderSize, worldHeight - borderSize,
                    MaterialColors.BROWN_900
                ).centerOn(this)
            }
            is MapEntity.TeleportIn -> {
                Circle(worldWidth / 2, Colors.GREEN.withAd(0.6)).addTo(this)
            }
            is MapEntity.TeleportOut -> {
                Circle(worldWidth / 2, Colors.RED.withAd(0.6)).addTo(this)
            }
            is MapEntity.SmallBlocker -> {
                this.solidRect(
                    worldWidth, worldHeight,
                    MaterialColors.YELLOW_500
                )
                this.solidRect(
                    worldWidth - borderSize, worldHeight - borderSize,
                    MaterialColors.YELLOW_900
                ).centerOn(this)
            }
            is MapEntity.SpeedArea -> {
                val speedAreaColor = SpeedAreaColorUtil(
                    entity,
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
            val (worldWidth, worldHeight) = toWorldDimensions(entity, gridSize)
            selectionBox = Graphics().addTo(this).apply {
                stroke(IN_PROCESS_SELECTION_COLOR, com.soywiz.korim.vector.StrokeInfo(3.0)) {
                    this.rectHole(0.0, 0.0, worldWidth, worldHeight)
                }
                centerOn(this)
            }
        }
    }

    fun doEndSelection() {
        if (selectionBox != null) cancelSelection()
        if (selectionBox == null) {
            val (worldWidth, worldHeight) = toWorldDimensions(entity, gridSize)
            selectionBox = Graphics().addTo(this).apply {
                stroke(SELECTION_COLOR, com.soywiz.korim.vector.StrokeInfo(3.0)) {
                    this.rectHole(0.0, 0.0, worldWidth, worldHeight)
                }
                centerOn(this)
            }
        }
    }
}