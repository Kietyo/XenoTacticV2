package com.xenotactic.korge.ui

import com.soywiz.klogger.Logger
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.centerOn
import com.soywiz.korge.view.container
import com.soywiz.korge.view.image
import com.soywiz.korge.view.renderToBitmap
import com.soywiz.korge.view.solidRect
import com.soywiz.korim.color.Colors
import com.soywiz.korio.async.launch
import com.xenotactic.gamelogic.globals.PATH_LINES_RATIO
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.korge.scenes.VIEWS_INSTANCE
import kotlinx.coroutines.GlobalScope
import pathing.PathFinder
import kotlin.math.min

inline fun Container.uiMapBox(
    gameMap: GameMap, boxWidth: Double, boxHeight: Double,
    gameMapGridSize: Double = 25.0
): UIMapBox =
    UIMapBox(gameMap, boxWidth, boxHeight, gameMapGridSize).addTo(this)

/**
 * A UI element where a game map is drawn inside of a box.
 */
class UIMapBox(
    gameMap: GameMap,
    val boxWidth: Double, val boxHeight: Double,
    val gameMapGridSize: Double = 25.0,
    val paddingTopAndBottom: Double = 5.0,
    val paddingLeftAndRight: Double = 5.0,
    calculateMapPath: Boolean = false
) : Container() {

    val mapSection = this.solidRect(boxWidth, boxHeight, Colors.BLACK.withAd(0.4))
    val mapContainer: Container = this.container()

    init {


//        mapContainer.scaledWidth = maxMapWidth
//        mapContainer.scaledHeight = maxMapWidth
//        mapContainer.scale = mapScale
//        mapContainer.centerOn(mapSection)

        updateMap(gameMap, calculateMapPath)



        //        launch(Dispatchers.Default) {
        //            val asBitMap = mapContainer.renderToBitmap(scenes.VIEWS_INSTANCE)
        //            mapContainer.removeChildren()
        //            val mapImage = mapContainer.image(asBitMap)
        //        }

    }

    fun updateMap(gameMap: GameMap, calculateMapPath: Boolean) {
        val currentMapContainerHeight = gameMap.height.value * gameMapGridSize
        val currentMapContainerWidth = gameMap.width.value * gameMapGridSize

        val maxMapHeight = boxHeight - paddingTopAndBottom * 2
        val maxMapWidth = boxWidth - paddingLeftAndRight * 2

        val scaledByHeight = maxMapHeight / currentMapContainerHeight
        val scaledByWidth = maxMapWidth / currentMapContainerWidth

        val mapScale = min(scaledByHeight, scaledByWidth)

        mapContainer.scale = mapScale
        mapContainer.removeChildren()
        mapContainer.apply {
            UIMap(
                gameMap,
                shortestPath = if (calculateMapPath) PathFinder.getShortestPath(gameMap) else
                    null,
                uiMapSettings = UIMapSettings(
                    drawGridNumbers = false,
                    gridSize = gameMapGridSize,
                    pathLinesRatio = (0.5 / mapScale) * PATH_LINES_RATIO,
                    boardType = BoardType.SOLID
                )
            )
        }
        mapContainer.centerOn(mapSection)

        GlobalScope.launch {
            val asBitMap = mapContainer.renderToBitmap(VIEWS_INSTANCE)
            mapContainer.removeChildren()
            val mapImage = mapContainer.image(asBitMap)
        }
    }

    companion object {
        val logger = Logger<UIMapBox>()
    }
}