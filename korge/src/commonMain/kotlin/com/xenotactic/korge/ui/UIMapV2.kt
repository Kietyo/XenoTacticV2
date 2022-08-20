package com.xenotactic.korge.ui

import com.soywiz.korge.view.*
import com.soywiz.korge.view.vector.gpuGraphics
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.MaterialColors
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.StrokeInfo
import com.soywiz.korma.geom.vector.line
import com.xenotactic.gamelogic.globals.BORDER_RATIO
import com.xenotactic.gamelogic.globals.GRID_LINES_RATIO
import com.xenotactic.gamelogic.globals.GRID_NUMBERS_RATIO
import com.xenotactic.gamelogic.globals.GRID_SIZE
import com.xenotactic.gamelogic.globals.PATH_LINES_RATIO
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.utils.toWorldCoordinates
import com.xenotactic.gamelogic.views.UIEntity

data class UIMapSettingsV2(
    val width: Int = 10,
    val height: Int = 10,
    val gridSize: Double = GRID_SIZE,
    val borderRatio: Double = BORDER_RATIO,
    val gridLinesRatio: Double = GRID_LINES_RATIO,
    val gridNumbersRatio: Double = GRID_NUMBERS_RATIO,
    val pathLinesRatio: Double = PATH_LINES_RATIO,
    val drawGridNumbers: Boolean = true,
    val boardType: BoardType = BoardType.CHECKERED_1X1,
) {
    val borderSize = gridSize * borderRatio
    val gridLineSize = gridSize * gridLinesRatio
    val gridNumberFontSize = gridSize * gridNumbersRatio
    val pathLinesWidth = gridSize * pathLinesRatio
}

class UIMapV2(
    val uiMapSettingsV2: UIMapSettingsV2 = UIMapSettingsV2()
) : Container() {
    val gridSize get() = uiMapSettingsV2.gridSize
    val gridNumberFontSize get() = uiMapSettingsV2.gridNumberFontSize
    val borderSize get() = uiMapSettingsV2.borderSize
    val mapWidth get() = uiMapSettingsV2.width
    val mapHeight get() = uiMapSettingsV2.height
    val _pathLinesWidth = uiMapSettingsV2.pathLinesWidth

    val _boardLayer = this.container {
        //        this.propagateEvents = false
        //        this.mouseChildren = false
        //        this.hitTestEnabled = false
    }

    val _gridNumberLayer = this.container()

    val speedAreaLayer = this.container()

    val entityLayer = this.container().apply {
    }

    val _entityLabelLayer = this.container()

    private val _pathingLinesGraphics = this.gpuGraphics {
        //        useNativeRendering = false
    }

    val _highlightLayer = this.container()
    val _highlightRectangle = this.solidRect(0, 0, Colors.YELLOW).alpha(0.5).visible(false)

    init {
        drawBoard()
        drawGridNumbers()
    }

    fun getWorldCoordinates(x: Int, y: Int, entityHeight: Int = 0) =
        Pair(x * gridSize, (mapHeight - y - entityHeight) * gridSize)

    fun toWorldDimensions(width: Int, height: Int) = Pair(width * gridSize, height * gridSize)

    private fun drawBoard() {
        println("Drawing board")
        when (uiMapSettingsV2.boardType) {
            BoardType.SOLID -> _boardLayer.solidRect(
                gridSize * mapWidth,
                gridSize * mapHeight,
                MaterialColors.GREEN_600
            )

            BoardType.CHECKERED_1X1 -> {
                var altColorWidth = true
                for (i in 0 until mapWidth) {
                    var altColorHeight = altColorWidth
                    for (j in 0 until mapHeight) {
                        val currColor =
                            if (altColorHeight) MaterialColors.GREEN_600 else MaterialColors
                                .GREEN_800
                        _boardLayer.solidRect(gridSize, gridSize, currColor)
                            .xy(i * gridSize, j * gridSize)
                        altColorHeight = !altColorHeight
                    }
                    altColorWidth = !altColorWidth
                }
            }

            BoardType.CHECKERED_2X2 -> {
                var altColorWidth = true
                val gridSize = gridSize * 2
                for (i in 0 until ((mapWidth + 1) / 2)) {
                    var altColorHeight = altColorWidth
                    for (j in 0 until ((mapHeight + 1) / 2)) {
                        val gridWidth = if ((i + 1) * 2 > mapWidth) this.gridSize else gridSize
                        val gridHeight = if ((j + 1) * 2 > mapHeight) this.gridSize else gridSize
                        val currColor =
                            if (altColorHeight) MaterialColors.GREEN_600 else MaterialColors
                                .GREEN_800
                        _boardLayer.solidRect(gridWidth, gridHeight, currColor)
                            .xy(i * gridSize, j * gridSize)
                        altColorHeight = !altColorHeight
                    }
                    altColorWidth = !altColorWidth
                }
            }
        }
        println("Finished drawing board!")
    }

    private fun drawGridNumbers() {
        _gridNumberLayer.removeChildren()

        if (!uiMapSettingsV2.drawGridNumbers) {
            return
        }

        for (i in 0 until mapWidth) {
            _gridNumberLayer.text(
                i.toString(),
                textSize = gridNumberFontSize,
                alignment = TextAlignment.BOTTOM_LEFT
            ).xy(
                i * gridSize, 0.0
            )
            _gridNumberLayer.text(i.toString(), textSize = gridNumberFontSize).xy(
                i * gridSize, mapHeight * gridSize
            )
        }
        for (j in 0 until mapHeight) {
            _gridNumberLayer.text(
                j.toString(),
                textSize = gridNumberFontSize,
                alignment = TextAlignment.BASELINE_RIGHT
            ).xy(
                -10.0, mapHeight * gridSize - j * gridSize
            )
            _gridNumberLayer.text(
                j.toString(),
                textSize = gridNumberFontSize,
                alignment = TextAlignment.BASELINE_LEFT
            ).xy(
                mapWidth * gridSize + 10.0, mapHeight * gridSize - j * gridSize
            )
        }
    }

    fun getGridPositionsFromGlobalMouse(
        globalMouseX: Double,
        globalMouseY: Double
    ): Pair<Double, Double> {
        val localXY = globalToLocalXY(globalMouseX, globalMouseY)
        val unprojected = Point(
            localXY.x,
            mapHeight * gridSize - localXY.y
        )

        val gridX = unprojected.x / gridSize
        val gridY = unprojected.y / gridSize

        return gridX to gridY
    }

    fun getRoundedGridCoordinates(
        gridX: Double,
        gridY: Double,
        entityWidth: Int,
        entityHeight: Int,
    ): Pair<Int, Int> =
        com.xenotactic.korge.korge_utils.getRoundedGridCoordinates(
            gridX,
            gridY,
            entityWidth,
            entityHeight,
            mapWidth,
            mapHeight
        )

    fun renderEntityHighlightRectangle(
        gridX: Int,
        gridY: Int,
        entityWidth: Int,
        entityHeight: Int
    ) {
        val (worldX, worldY) = toWorldCoordinates(
            gridSize,
            IntPoint(gridX, gridY),
            mapWidth, mapHeight, entityHeight
        )
        val (worldWidth, worldHeight) = com.xenotactic.gamelogic.utils.toWorldDimensions(
            entityWidth,
            entityHeight,
            gridSize
        )
        _highlightRectangle
            .size(worldWidth, worldHeight)
            .xy(worldX, worldY)
            .visible(true)
    }

    fun renderHighlightEntity(entity: MapEntity) {
        val (worldX, worldY) = toWorldCoordinates(
            gridSize, entity, mapWidth, mapHeight
        )
        createEntityView(entity).apply {
            addTo(_highlightLayer)
            xy(worldX, worldY)
        }
    }

    private fun createEntityView(entity: MapEntity): UIEntity {
        //        return UIEntity(entity, engine, _gridSize, _borderSize)
        return UIEntity(
            entity.type, entity.width, entity.height, gridSize, borderSize,
            if (entity is MapEntity.SpeedArea) entity.speedEffect else null
        )
    }

    fun clearHighlightLayer() {
        _highlightLayer.removeChildren()
    }

    fun hideHighlightRectangle() {
        _highlightRectangle.visible(false)
    }

    fun renderPathLines(pathSequence: PathSequence?) {
        _pathingLinesGraphics.updateShape {  }

        // Draw path lines
        if (pathSequence != null) {
            println("Got path sequence: $pathSequence")
            _pathingLinesGraphics.updateShape {
                stroke(
                    Colors.YELLOW, info = StrokeInfo(
                        thickness = _pathLinesWidth,
                    )
                ) {
                    for (path in pathSequence.paths) {
                        for (segment in path.getSegments()) {
                            val (p1WorldX, p1WorldY) = toWorldCoordinates(
                                gridSize, segment.point1, mapHeight
                            )
                            val (p2WorldX, p2WorldY) = toWorldCoordinates(
                                gridSize, segment.point2, mapHeight
                            )
                            this.line(p1WorldX, p1WorldY, p2WorldX, p2WorldY)
                        }
                    }
                }
            }

            //            for (path in pathSequence.paths) {
            //                for (segment in path.getSegments()) {
            //                    val (p1WorldX, p1WorldY) = toWorldCoordinates(
            //                        segment.point1, gameMap.width,
            //                        gameMap.height
            //                    )
            //                    val (p2WorldX, p2WorldY) = toWorldCoordinates(
            //                        segment.point2, gameMap.width,
            //                        gameMap.height
            //                    )
            //                    _pathingLinesGraphics.line(p1WorldX, p1WorldY, p2WorldX, p2WorldY)
            //                }
            //            }
        }
    }
}