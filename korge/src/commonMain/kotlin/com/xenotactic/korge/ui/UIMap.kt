package com.xenotactic.korge.ui

import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.utils.*
import com.xenotactic.gamelogic.views.UIEntity
import com.xenotactic.korge.events.RemovedEntityEvent
import com.xenotactic.korge.input_processors.PointerAction
import com.xenotactic.korge.utils.getRoundedGridCoordinates
import com.xenotactic.korge.utils.makeEntityLabelText
import com.xenotactic.korge.utils.toWorldCoordinates
import korlibs.image.bitmap.effect.BitmapEffect
import korlibs.image.color.Colors
import korlibs.image.color.MaterialColors
import korlibs.image.font.BitmapFont
import korlibs.image.font.DefaultTtfFont
import korlibs.image.text.TextAlignment
import korlibs.io.async.launch
import korlibs.korge.view.*
import korlibs.korge.view.vector.gpuGraphics
import korlibs.math.geom.Point
import korlibs.math.geom.Rectangle
import korlibs.math.geom.vector.StrokeInfo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex

enum class BoardType {
    SOLID,
    CHECKERED_1X1,
    CHECKERED_2X2,
}

data class UIMapSettings(
    val gridSize: Float = GRID_SIZE,
    val borderRatio: Float = BORDER_RATIO,
    val gridLinesRatio: Float = GRID_LINES_RATIO,
    val gridNumbersRatio: Float = GRID_NUMBERS_RATIO,
    val pathLinesRatio: Number = PATH_LINES_RATIO,
    val drawGridNumbers: Boolean = true,
    val boardType: BoardType = BoardType.CHECKERED_1X1,
) {
    val borderSize = gridSize * borderRatio
    val gridLineSize = gridSize * gridLinesRatio
    val gridNumberFontSize = gridSize * gridNumbersRatio
    val pathLinesWidth = gridSize * pathLinesRatio.toFloat()
}

val ENTITY_TEXT_FONT = BitmapFont(
    font = DefaultTtfFont, 30f, effect = BitmapEffect(
        //                        dropShadowX = 1,
        //                        dropShadowY = 1,
        //                        dropShadowRadius = 1,
        borderSize = 1
    )
)

class UIMap(
    val gameMap: GameMap,
    val engine: Engine? = null,
    val shortestPath: PathSequence? = null,
    private val uiMapSettings: UIMapSettings = UIMapSettings(),
    val initialRenderEntities: Boolean = true
) : Container() {
    val _gridSize = uiMapSettings.gridSize
    private val _borderSize = uiMapSettings.borderSize
    private val _gridLineSize = uiMapSettings.gridLineSize
    private val _gridNumberFontSize = uiMapSettings.gridNumberFontSize
    private val _pathLinesWidth = uiMapSettings.pathLinesWidth

    private val _drawnEntities = mutableMapOf<MapEntity, MutableList<UIEntity>>()
    private val _entityToDrawnText = mutableMapOf<MapEntity, Text>()

    private val _drawnRockCounters = mutableMapOf<GameUnitTuple, Text>()

    // Note that the order in which layers are initialized mattes here.
    private val _boardLayer = this.container {
        //        this.propagateEvents = false
        //        this.mouseChildren = false
        //        this.hitTestEnabled = false
    }
    private val _gridNumberLayer = this.container()

    private val _gridLinesLayer = this.container()
    private val _gridLinesGraphics = _gridLinesLayer.gpuGraphics {
        //        useNativeRendering = false
        //        visible(false)
    }

    private val _speedAreaLayer = this.container()

    private val _entityLayer = this.container().apply {
    }

    val _selectionLayer = this.container()

    private val _rockCountersLayer = this.container()
    private val _rockCountersLayerMutex = Mutex()

    private val _entityLabelLayer = this.container()

    private val _pathingLinesLayer = this.container()
    private val _pathingLinesGraphics = _pathingLinesLayer.gpuGraphics {
        //        useNativeRendering = false
    }

    private val _highlightLayer = this.container()
    private val _highlightRectangle = this.solidRect(0, 0, Colors.YELLOW).alpha(0.5).visible(false)

    val mapHeight: GameUnit
        get() = gameMap.height
    val mapWidth: GameUnit
        get() = gameMap.width

    init {
        drawBoard()
        drawGridNumbers()
        drawGridLines()
        if (initialRenderEntities) {
            renderEntities()
        }
        renderRockCounters()
        renderPathLines(shortestPath)

        //                engine?.eventBus?.register<UIEntityClickedEvent> {
        //                    val (worldWidth, worldHeight) = toWorldDimensions(it.entity, _gridSize)
        //        //            val solidRect = SolidRect(
        //        //                worldWidth + 10, worldHeight + 10,
        //        //                MaterialColors.YELLOW_900).addTo(_selectionLayer).apply {
        //        //                    centerOn(it.view)
        //        //            }
        //
        //                    Graphics().addTo(_selectionLayer).apply {
        //                        stroke(Colors.YELLOW, StrokeInfo(3.0)) {
        //                            this.rectHole(0.0, 0.0, worldWidth, worldHeight)
        //                        }
        //                        centerOn(it.view)
        //                    }
        //                }
    }

    suspend fun viewRockCounters() {
        if (_rockCountersLayerMutex.isLocked) {
            println("Rock counters mutex is locked, skipping")
            return
        }
        _rockCountersLayerMutex.lock()
        println("Started rock counter routine")
        _rockCountersLayer.visible = true
        launch(GlobalScope.coroutineContext) {
            delay(5000)
            _rockCountersLayer.visible = false
            _rockCountersLayerMutex.unlock()
            println("End rock counter routine")
        }
    }

    private fun renderRockCounters() {
        val rockCounters = RockCounterUtil.calculate(gameMap)
        for (x in 0 until gameMap.width) {
            for (y in 0 until gameMap.height) {
                val num = rockCounters[x.toInt(), y.toInt()]
                if (num > 0) {
                    val (worldX, worldY) = toWorldCoordinates(
                        _gridSize,
                        IPoint(x.toFloat() + 0.5, y.toFloat() + 0.5), gameMap.height
                    )
                    val component = _rockCountersLayer.text(
                        num.toString(), textSize = 15f, alignment = TextAlignment
                            .MIDDLE_CENTER,
                        font = ENTITY_TEXT_FONT
                    ).xy(
                        worldX.value,
                        worldY.value
                    ).apply {
                        scaledHeight = _gridSize / 2
                        scaledWidth = scaledHeight * unscaledWidth / unscaledHeight
                    }
                    _drawnRockCounters[GameUnitTuple(x, y)] = component
                }
            }
        }
        _rockCountersLayer.visible = false
    }

    private fun drawBoard() {
        println("Drawing board")
        when (uiMapSettings.boardType) {
            BoardType.SOLID -> _boardLayer.solidRect(
                _gridSize * gameMap.width.value, _gridSize * gameMap.height.value,
                MaterialColors.GREEN_600
            )

            BoardType.CHECKERED_1X1 -> {
                var altColorWidth = true
                for (i in 0 until gameMap.width) {
                    var altColorHeight = altColorWidth
                    for (j in 0 until gameMap.height) {
                        val currColor =
                            if (altColorHeight) MaterialColors.GREEN_600 else MaterialColors
                                .GREEN_800
                        _boardLayer.solidRect(_gridSize, _gridSize, currColor)
                            .xy(i.toWorldUnit(_gridSize), j.toWorldUnit(_gridSize))
                        altColorHeight = !altColorHeight
                    }
                    altColorWidth = !altColorWidth
                }
            }

            BoardType.CHECKERED_2X2 -> {
                var altColorWidth = true
                val gridSize = _gridSize * 2
                for (i in 0 until ((gameMap.width.toInt() + 1) / 2)) {
                    var altColorHeight = altColorWidth
                    for (j in 0 until ((gameMap.height.toInt() + 1) / 2)) {
                        val gridWidth = if ((i + 1) * 2 > gameMap.width.value) _gridSize else gridSize
                        val gridHeight = if ((j + 1) * 2 > gameMap.height.value) _gridSize else gridSize
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

    fun removeEntity(entity: MapEntity) {
        if (_drawnEntities.containsKey(entity)) {
            val drawnList = _drawnEntities[entity]!!
            val drawn = drawnList.removeLast()
            drawn.removeFromParent()
            if (drawnList.isEmpty()) {
                _drawnEntities.remove(entity)
            }
        }
    }

    fun handleRemoveEntityEvent(event: RemovedEntityEvent) {
        removeEntity(event.entity)
    }

    private fun drawGridLines() {
        //        _gridLinesGraphics.clear()
        _gridLinesGraphics.updateShape {
            stroke(Colors.BLACK, info = StrokeInfo(_gridLineSize)) {
                for (i in 0..gameMap.width) {
                    this.line(Point(i * _gridSize, 0f), Point(i * _gridSize, gameMap.height.toFloat() * _gridSize))
                }
                for (j in 0..gameMap.height) {
                    this.line(Point(0f, j * _gridSize), Point(gameMap.width.toFloat() * _gridSize, j * _gridSize))
                }
            }
        }
    }

    private fun drawGridNumbers() {
        _gridNumberLayer.removeChildren()

        if (!uiMapSettings.drawGridNumbers) {
            return
        }

        for (i in 0 until gameMap.width) {
            _gridNumberLayer.text(
                i.toString(),
                textSize = _gridNumberFontSize,
                alignment = TextAlignment.BOTTOM_LEFT
            ).xy(
                i.toWorldUnit(_gridSize), 0.toWorldUnit()
            )
            _gridNumberLayer.text(i.toString(), textSize = _gridNumberFontSize).xy(
                i.toWorldUnit(_gridSize), gameMap.height.toWorldUnit(_gridSize)
            )
        }
        for (j in 0 until gameMap.height) {
            _gridNumberLayer.text(
                j.toString(),
                textSize = _gridNumberFontSize,
                alignment = TextAlignment.BASELINE_RIGHT
            ).xy(
                (-10.0).toWorldUnit(), gameMap.height.toWorldUnit(_gridSize) - j.toWorldUnit(_gridSize)
            )
            _gridNumberLayer.text(
                j.toString(),
                textSize = _gridNumberFontSize,
                alignment = TextAlignment.BASELINE_LEFT
            ).xy(
                gameMap.width.toWorldUnit(_gridSize) + 10.toWorldUnit(),
                gameMap.height.toWorldUnit(_gridSize) - j.toWorldUnit(_gridSize)
            )
        }
    }

    private fun createEntityView(entity: MapEntity): UIEntity {
        //        return UIEntity(entity, engine, _gridSize, _borderSize)
        return UIEntity(
            entity.type, entity.width, entity.height, _gridSize, _borderSize,
            if (entity is MapEntity.SpeedArea) entity.speedEffect else null
        )
    }

    private fun renderEntityInternal(entity: MapEntity): UIEntity {
        val (worldX, worldY) = toWorldCoordinates(
            _gridSize, entity, gameMap.width, gameMap
                .height
        )
        val uiEntity = createEntityView(entity).apply {
            if (entity is MapEntity.SpeedArea) {
                addTo(_speedAreaLayer)
            } else {
                addTo(_entityLayer)
            }
            xy(worldX, worldY)
        }
        val drawnEntitiesList = _drawnEntities.getOrPut(entity) { mutableListOf() }
        drawnEntitiesList.add(uiEntity)
        return uiEntity
    }

    private fun renderEntityTextInternal(entity: MapEntity) {
        if (_entityToDrawnText.containsKey(entity)) {
            // Already drew text for entity
            return
        }
        val text: String? = entity.toMapEntityData().getText()

        if (text != null) {
            val (worldX, worldY) = toWorldCoordinates(
                _gridSize,
                entity.centerPoint, gameMap.height
            )
            val component = makeEntityLabelText(text).apply {
                addTo(_entityLabelLayer)
                xy(worldX, worldY)
                scaledHeight = _gridSize / 2
                scaledWidth = scaledHeight * unscaledWidth / unscaledHeight
            }
            _entityToDrawnText[entity] = component
        }
    }

    fun addEntity(entity: MapEntity): UIEntity {
        renderEntityTextInternal(entity)
        return renderEntityInternal(entity)
    }

    private fun renderEntities() {
        // Draw map entity shapes
        for (entity in gameMap.getAllEntities()) {
            addEntity(entity)
        }
    }

    fun renderPathLines(pathSequence: PathSequence?) {
        //        _pathingLinesGraphics.clear()

        // Draw path lines
        if (pathSequence != null) {
            _pathingLinesGraphics.updateShape {
                stroke(
                    Colors.YELLOW, info = StrokeInfo(
                        thickness = _pathLinesWidth,
                    )
                ) {
                    for (path in pathSequence.paths) {
                        for (segment in path.getSegments()) {
                            val worldP1 = toWorldCoordinates(
                                _gridSize, segment.point1, gameMap.height
                            )
                            val worldP2 = toWorldCoordinates(
                                _gridSize, segment.point2, gameMap.height
                            )
                            this.line(worldP1.toPoint(), worldP2.toPoint())
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

    fun renderEntityHighlightRectangle(
        gridX: GameUnit,
        gridY: GameUnit,
        entityWidth: GameUnit,
        entityHeight: GameUnit
    ) {
        val (worldX, worldY) = toWorldCoordinates(
            _gridSize,
            GameUnitTuple(gridX, gridY),
            gameMap.height, entityHeight
        )
        val (worldWidth, worldHeight) = toWorldDimensions(
            entityWidth,
            entityHeight,
            _gridSize
        )
        _highlightRectangle
            .size(worldWidth, worldHeight)
            .xy(worldX, worldY)
            .visible(true)
    }

    //    fun renderHighlightEntity(entity: MapEntity) {
    //        val (worldX, worldY) = toWorldCoordinates(
    //            _gridSize, entity, gameMap.width, gameMap
    //                .height
    //        )
    //        createEntityView(entity).apply {
    //            addTo(_highlightLayer)
    //            xy(worldX, worldY)
    //        }
    //    }

    fun clearHighlightLayer() {
        _highlightLayer.removeChildren()
    }

    fun hideHighlightRectangle() {
        _highlightRectangle.visible(false)
    }

    fun renderHighlightingForPointerAction(pointerAction: PointerAction) {
        when (pointerAction) {
            PointerAction.Inactive -> {
                hideHighlightRectangle()
            }

            is PointerAction.HighlightForPlacement -> {
                if (pointerAction.placementLocation != null) {
                    val (gridX, gridY) = pointerAction.placementLocation!!
                    renderEntityHighlightRectangle(
                        gridX, gridY,
                        pointerAction.mapEntity.width,
                        pointerAction.mapEntity.height,
                    )
                }
            }

            is PointerAction.RemoveEntityAtPlace -> {
                val data = pointerAction.data
                if (data == null) {
                    hideHighlightRectangle()
                } else {
                    val (worldX, worldY) = toWorldCoordinates(
                        _gridSize,
                        data.entity.gameUnitPoint,
                        gameMap.height, data.entity.height
                    )
                    val (worldWidth, worldHeight) = toWorldDimensions(data.entity, _gridSize)
                    _highlightRectangle
                        .size(worldWidth, worldHeight)
                        .xy(worldX, worldY)
                        .visible(true)
                }
            }
        }
    }

    fun getGridPositionsFromGlobalMouse(
        globalMouseX: Double,
        globalMouseY: Double
    ): Pair<Double, Double> {
        val localXY = _boardLayer.globalToLocal(Point(globalMouseX, globalMouseY))
        val unprojected = Point(
            localXY.x,
            (mapHeight.value * _gridSize - localXY.y).toFloat()
        )

        val gridX = unprojected.x / _gridSize
        val gridY = unprojected.y / _gridSize

        return gridX.toDouble() to gridY.toDouble()
    }

    fun getRoundedGridCoordinates(
        gridX: Double,
        gridY: Double,
        entityWidth: GameUnit,
        entityHeight: GameUnit,
    ): GameUnitTuple =
        getRoundedGridCoordinates(gridX, gridY, entityWidth, entityHeight, mapWidth, mapHeight)

    fun getIntersectingEntities(rect: Rectangle): List<UIEntity> {
        return _drawnEntities.values.asSequence().flatten().filter {
            rect.intersects(it.getGlobalBounds())
        }.toList()

    }
}