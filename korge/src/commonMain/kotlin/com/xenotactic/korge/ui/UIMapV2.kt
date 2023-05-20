package com.xenotactic.korge.ui

import com.xenotactic.ecs.StagingEntity
import com.xenotactic.gamelogic.events.ResizeMapEvent
import com.xenotactic.gamelogic.events.UpdatedPathLineEvent
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.GameWorld
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.state.GameMapDimensionsState
import com.xenotactic.gamelogic.state.GameMapPathState
import com.xenotactic.gamelogic.utils.*
import com.xenotactic.gamelogic.views.UIEntity
import com.xenotactic.korge.utils.toWorldCoordinates
import korlibs.image.color.Colors
import korlibs.image.color.MaterialColors
import korlibs.image.text.TextAlignment
import korlibs.korge.view.*
import korlibs.korge.view.vector.gpuGraphics
import korlibs.math.geom.Point
import korlibs.math.geom.vector.StrokeInfo

data class UIMapSettingsV2(
    val gridSize: Float = GRID_SIZE,
    val borderRatio: Float = BORDER_RATIO,
    val gridLinesRatio: Float = GRID_LINES_RATIO,
    val gridNumbersRatio: Float = GRID_NUMBERS_RATIO,
    val pathLinesRatio: Float = PATH_LINES_RATIO,
    val drawGridNumbers: Boolean = true,
    val boardType: BoardType = BoardType.CHECKERED_2X2,
    val drawGridLines: Boolean = true,
) {
    val borderSize = gridSize * borderRatio
    val gridLineSize = gridSize * gridLinesRatio
    val gridNumberFontSize = gridSize * gridNumbersRatio
    val pathLinesWidth = gridSize * pathLinesRatio
}

class UIMapV2(
    val engine: Engine,
    private val uiMapSettingsV2: UIMapSettingsV2 = UIMapSettingsV2(),
) : Container() {
    private val gameWorld: GameWorld = engine.gameWorld
    private val gameMapDimensionsState = engine.stateInjections.getSingleton<GameMapDimensionsState>()
    val gridSize get() = uiMapSettingsV2.gridSize
    private val gridNumberFontSize get() = uiMapSettingsV2.gridNumberFontSize
    val borderSize get() = uiMapSettingsV2.borderSize
    val mapWidth get() = gameMapDimensionsState.width
    val mapHeight get() = gameMapDimensionsState.height
    val _pathLinesWidth = uiMapSettingsV2.pathLinesWidth

    val gameMapPathState = engine.stateInjections.getSingleton<GameMapPathState>()

    private val _boardLayer = this.container {
        name = "_boardLayer"
        //        this.propagateEvents = false
        //        this.mouseChildren = false
        //        this.hitTestEnabled = false
    }

    private val _boardGraphicsLayer = this.graphics {
        name = "_boardGraphicsLayer"
    }

    private val _gridNumberLayer = this.container {
        name = "_gridNumberLayer"
    }

    //    val speedAreaLayer = this.clipContainer(
    //        mapWidth.toWorldUnit(gridSize).toDouble(),
    //        mapHeight.toWorldUnit(gridSize).toDouble()
    //    )

    val speedAreaLayer = this.container {
        name = "speedAreaLayer"
    }
    val speedAreaLayerGraphics = this.graphics() {
        name = "speedAreaLayerGraphics"
    }

    val entityLayer = this.container {
        name = "entityLayer"
    }

    val entityLabelLayer = this.container() {
        name = "entityLabelLayer"
    }

    val rangeIndicatorLayer = this.container() {
        name = "rangeIndicatorLayer"
    }

    private val _pathingLinesGraphics = this.gpuGraphics {
        name = "_pathingLinesGraphics"
        //        useNativeRendering = false
    }

    val monsterLayer = this.container() {
        name = "monsterLayer"
    }

    val targetingLinesLayer = this.graphics { }
    val projectileLayer = this.graphics { }

    private val _highlightLayer = this.container()
    private val _highlightRectangle = this.solidRect(0, 0, Colors.YELLOW).alpha(0.5).visible(false)

    init {
        resetUIMap()

        engine.eventBus.register<UpdatedPathLineEvent> {
            renderPathLines(it.pathSequence)
        }
    }

    private fun resetUIMap() {
        //        drawBoard()
        drawBoardV2()
        drawGridNumbers()
    }

    fun handleResizeMapEvent(event: ResizeMapEvent) {
        require(
            gameMapDimensionsState.width == event.newMapWidth &&
                    gameMapDimensionsState.height == event.newMapHeight
        )
        resetUIMap()
        val heightDiffWorldUnit = (event.newMapHeight - event.oldMapHeight).toWorldUnit(gridSize)
        gameWorld.uiEntityFamily.getSequence().forEach {
            val uiMapEntityComponent = gameWorld.uiEntityViewComponentContainer.getComponent(it)
            uiMapEntityComponent.entityView.y += heightDiffWorldUnit.toFloat()
            val uiMapEntityTextComponent =
                gameWorld.uiMapEntityTextComponentContainer.getComponentOrNull(it)
            if (uiMapEntityTextComponent != null) {
                uiMapEntityTextComponent.textView.y += heightDiffWorldUnit.toFloat()
            }
        }
        renderPathLines(gameMapPathState.shortestPath)
    }

    fun getWorldCoordinates(x: GameUnit, y: GameUnit, entityHeight: GameUnit = GameUnit(0)): WorldPoint =
        Pair(WorldUnit(x.value * gridSize), WorldUnit((mapHeight - y - entityHeight).value * gridSize))

    fun toWorldDimensions(width: GameUnit, height: GameUnit) = Pair(
        WorldUnit(width * gridSize), WorldUnit(height * gridSize)
    )

    private fun drawBoardV2() {
        println("Drawing board")
        _boardGraphicsLayer.updateShape {
            when (uiMapSettingsV2.boardType) {
                BoardType.SOLID -> _boardLayer.solidRect(
                    mapWidth.toWorldUnit(gridSize).value,
                    mapHeight.toWorldUnit(gridSize).value,
                    MaterialColors.GREEN_600
                )

                BoardType.CHECKERED_1X1 -> {
                    var altColorWidth = true
                    for (i in 0 until mapWidth) {
                        var altColorHeight = altColorWidth
                        val xGrid = i.toWorldUnit(gridSize)
                        for (j in 0 until mapHeight) {
                            val currColor =
                                if (altColorHeight) MaterialColors.GREEN_600 else MaterialColors
                                    .GREEN_800
                            this.fillStyle(currColor) {
                                this.fillRect(xGrid.toFloat(), j.toWorldUnit(gridSize).toFloat(), gridSize, gridSize)
                            }
                            altColorHeight = !altColorHeight
                        }
                        altColorWidth = !altColorWidth
                    }
                }

                BoardType.CHECKERED_2X2 -> {
                    var altColorWidth = true
                    val checkeredGridSize = gridSize * 2
                    for (i in 0 until ((mapWidth.toInt() + 1) / 2)) {
                        var altColorHeight = altColorWidth
                        val xGridPosition = i * checkeredGridSize
                        for (j in 0 until ((mapHeight.toInt() + 1) / 2)) {
                            val gridWidth =
                                if ((i + 1) * 2 > mapWidth.value) this@UIMapV2.gridSize else checkeredGridSize
                            val gridHeight =
                                if ((j + 1) * 2 > mapHeight.value) this@UIMapV2.gridSize else checkeredGridSize
                            val currColor =
                                if (altColorHeight) MaterialColors.GREEN_600 else MaterialColors
                                    .GREEN_800
                            fillStyle(currColor) {
                                fillRect(xGridPosition, j * checkeredGridSize, gridWidth, gridHeight)
                            }
                            altColorHeight = !altColorHeight
                        }
                        altColorWidth = !altColorWidth
                    }
                }
            }

            if (uiMapSettingsV2.drawGridLines) {
                val alpha = 0.5
                val strokeInfo = StrokeInfo(
                    thickness = 1f,
                )
                for (x in 0 until mapWidth) {
                    stroke(Colors.BLACK.withAd(alpha), info = strokeInfo) {
                        line(
                            Point(x.toWorldUnit(gridSize), WorldUnit.ZERO),
                            Point(x.toWorldUnit(gridSize), mapHeight.toWorldUnit(gridSize))
                        )
                    }
                }

                for (y in 0 until mapHeight) {
                    stroke(Colors.BLACK.withAd(alpha), info = strokeInfo) {
                        line(
                            Point(WorldUnit.ZERO, y.toWorldUnit(gridSize)),
                            Point(mapWidth.toWorldUnit(gridSize), y.toWorldUnit(gridSize))
                        )
                    }
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
            val iNumString = i.toInt().toString()
            _gridNumberLayer.text(
                iNumString,
                textSize = gridNumberFontSize,
                alignment = TextAlignment.BOTTOM_LEFT
            ).xy(
                i.toWorldUnit(gridSize), 0.toWorldUnit()
            )
            _gridNumberLayer.text(iNumString, textSize = gridNumberFontSize).xy(
                i.toWorldUnit(gridSize), mapHeight.toWorldUnit(gridSize)
            )
        }
        for (j in 0 until mapHeight) {
            val jNumString = j.toInt().toString()
            _gridNumberLayer.text(
                jNumString,
                textSize = gridNumberFontSize,
                alignment = TextAlignment.BASELINE_RIGHT
            ).xy(
                (-10.0).toWorldUnit(), mapHeight.toWorldUnit(gridSize) - j.toWorldUnit(gridSize)
            )
            _gridNumberLayer.text(
                jNumString,
                textSize = gridNumberFontSize,
                alignment = TextAlignment.BASELINE_LEFT
            ).xy(
                mapWidth.toWorldUnit(gridSize) + 10.toWorldUnit(),
                mapHeight.toWorldUnit(gridSize) - j.toWorldUnit(gridSize)
            )
        }
    }

    fun getGridPositionsFromGlobalMouse(
        globalMouseX: Float,
        globalMouseY: Float
    ): Pair<Float, Float> {
        val localXY = globalToLocal(Point(globalMouseX, globalMouseY))
        val unprojected = Point(
            localXY.x,
            (mapHeight.value * gridSize - localXY.y).toFloat()
        )

        val gridX = unprojected.x / gridSize
        val gridY = unprojected.y / gridSize

        return gridX to gridY
    }

    fun getRoundedGridCoordinates(
        gridX: Number,
        gridY: Number,
        entityWidth: GameUnit,
        entityHeight: GameUnit,
    ): GameUnitTuple =
        com.xenotactic.korge.utils.getRoundedGridCoordinates(
            gridX,
            gridY,
            entityWidth,
            entityHeight,
            mapWidth,
            mapHeight
        )

    fun renderEntityHighlightRectangle(
        gridX: GameUnit,
        gridY: GameUnit,
        entityWidth: GameUnit,
        entityHeight: GameUnit
    ) {
        val (worldX, worldY) = toWorldCoordinates(
            gridSize,
            GameUnitTuple(gridX, gridY),
            mapHeight, entityHeight
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

    fun renderHighlightEntity(entity: StagingEntity) {
        val (worldX, worldY) = toWorldCoordinates(
            gridSize, entity, mapWidth, mapHeight
        )
        createEntityView(entity).apply {
            addTo(_highlightLayer)
            xy(worldX, worldY)
        }
    }

    private fun createEntityView(entity: StagingEntity): UIEntity {
        val entityType = entity[com.xenotactic.gamelogic.components.EntityTypeComponent::class]
        val size = entity[com.xenotactic.gamelogic.components.SizeComponent::class]
        return UIEntity(
            entityType.type, size.width, size.height, gridSize, borderSize,
            if (entityType.type == MapEntityType.SPEED_AREA) {
                entity[com.xenotactic.gamelogic.components.EntitySpeedAreaComponent::class].speedEffect
            } else null
        )
    }

    fun clearHighlightLayer() {
        _highlightLayer.removeChildren()
    }

    fun hideHighlightRectangle() {
        _highlightRectangle.visible(false)
    }

    fun renderPathLines(pathSequence: PathSequence?) {
        //        _pathingLinesGraphics.updateShape { }

        // Draw path lines
        if (pathSequence != null) {
            _pathingLinesGraphics.updateShape {
                stroke(
                    Colors.YELLOW.withAd(0.75), info = StrokeInfo(
                        thickness = _pathLinesWidth,
                    )
                ) {
                    for (path in pathSequence.paths) {
                        for (segment in path.getSegments()) {
                            val worldP1 = toWorldCoordinates(
                                gridSize, segment.point1, mapHeight
                            )
                            val worldP2 = toWorldCoordinates(
                                gridSize, segment.point2, mapHeight
                            )
                            this.line(
                                worldP1.toPoint(),
                                worldP2.toPoint()
                            )
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



