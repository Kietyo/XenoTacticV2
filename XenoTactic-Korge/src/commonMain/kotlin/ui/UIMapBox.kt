package ui

import com.soywiz.klogger.Logger
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.color.Colors
import com.soywiz.korio.async.launch
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.async.runBlockingNoSuspensions
import com.xenotactic.gamelogic.globals.PATH_LINES_RATIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import model.GameMap
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
    boxWidth: Double, boxHeight: Double,
    gameMapGridSize: Double = 25.0,
    paddingTopAndBottom: Double = 5.0,
    paddingLeftAndRight: Double = 5.0,
    calculateMapPath: Boolean = false
) : Container() {

    val mapContainer: Container
    var mapScale: Double = 1.0

    init {
        val currentMapContainerHeight = gameMap.height * gameMapGridSize
        val currentMapContainerWidth = gameMap.width * gameMapGridSize

        val maxMapHeight = boxHeight - paddingTopAndBottom * 2
        val maxMapWidth = boxWidth - paddingLeftAndRight * 2

        val scaledByHeight = maxMapHeight / currentMapContainerHeight
        val scaledByWidth = maxMapWidth / currentMapContainerWidth

        mapScale = min(scaledByHeight, scaledByWidth)

        val mapSection = this.solidRect(boxWidth, boxHeight, Colors.BLACK.withAd(0.4))
        mapContainer = this.container {
            this.uiMap(
                gameMap,
                shortestPath = if (calculateMapPath) PathFinder.getShortestPath(gameMap) else
                    null,
                UIMapSettings(
                    drawGridNumbers = false,
                    gridSize = gameMapGridSize,
                    pathLinesRatio = (0.5 / mapScale) * PATH_LINES_RATIO,
                    drawCheckeredBoard = false
                )
            )
        }


        mapContainer.scale = mapScale
        mapContainer.centerOn(mapSection)

        GlobalScope.launch {
            val asBitMap = mapContainer.renderToBitmap(scenes.VIEWS_INSTANCE)
            mapContainer.removeChildren()
            val mapImage = mapContainer.image(asBitMap)
        }

//        launch(Dispatchers.Default) {
//            val asBitMap = mapContainer.renderToBitmap(scenes.VIEWS_INSTANCE)
//            mapContainer.removeChildren()
//            val mapImage = mapContainer.image(asBitMap)
//        }

    }

    companion object {
        val logger = Logger<UIMapBox>()
    }
}