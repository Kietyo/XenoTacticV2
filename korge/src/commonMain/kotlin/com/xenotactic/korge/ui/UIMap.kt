package com.xenotactic.korge.ui

import com.soywiz.kmem.clamp
import com.soywiz.korge.view.*
import com.soywiz.korge.view.vector.gpuGraphics
import com.soywiz.korim.bitmap.effect.BitmapEffect
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.MaterialColors
import com.soywiz.korim.font.BitmapFont
import com.soywiz.korim.font.DefaultTtfFont
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.async.launch
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.Rectangle
import com.soywiz.korma.geom.vector.StrokeInfo
import com.soywiz.korma.geom.vector.line
import com.xenotactic.gamelogic.globals.*
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.utils.RockCounterUtil
import com.xenotactic.gamelogic.utils.toWorldCoordinates
import com.xenotactic.gamelogic.utils.toWorldDimensions
import com.xenotactic.gamelogic.views.UIEntity
import com.xenotactic.korge.engine.EComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.RemovedEntityEvent
import com.xenotactic.korge.input_processors.PointerAction
import com.xenotactic.korge.korge_utils.makeEntityLabelText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlin.math.floor
import kotlin.math.roundToInt

enum class BoardType {
    SOLID,
    CHECKERED_1X1,
    CHECKERED_2X2,
}

data class UIMapSettings(
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

val ENTITY_TEXT_FONT = BitmapFont(
    font = DefaultTtfFont, 30.0, effect = BitmapEffect(
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
) : Container(), EComponent {
    val _gridSize = uiMapSettings.gridSize
    val _borderSize = uiMapSettings.borderSize
    val _gridLineSize = uiMapSettings.gridLineSize
    val _gridNumberFontSize = uiMapSettings.gridNumberFontSize
    val _pathLinesWidth = uiMapSettings.pathLinesWidth

    val _drawnEntities = mutableMapOf<MapEntity, MutableList<UIEntity>>()
    val _entityToDrawnText = mutableMapOf<MapEntity, Text>()

    val _drawnRockCounters = mutableMapOf<IntPoint, Text>()

    // Note that the order in which layers are initialized mattes here.
    val _boardLayer = this.container {
        //        this.propagateEvents = false
        //        this.mouseChildren = false
        //        this.hitTestEnabled = false
    }
    val _gridNumberLayer = this.container()

    val _gridLinesLayer = this.container()
    val _gridLinesGraphics = _gridLinesLayer.gpuGraphics {
        //        useNativeRendering = false
        //        visible(false)
    }

    val _speedAreaLayer = this.container()

    val _entityLayer = this.container().apply {
    }

    val _selectionLayer = this.container()

    val _rockCountersLayer = this.container()
    val _rockCountersLayerMutex = Mutex()

    val _entityLabelLayer = this.container()

    val _pathingLinesLayer = this.container()
    val _pathingLinesGraphics = _pathingLinesLayer.gpuGraphics {
        //        useNativeRendering = false
    }

    val _highlightLayer = this.container()
    val _highlightRectangle = this.solidRect(0, 0, Colors.YELLOW).alpha(0.5).visible(false)

    val mapHeight: Int
        get() = gameMap.height
    val mapWidth: Int
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
                val num = rockCounters[x, y]
                if (num > 0) {
                    val (worldX, worldY) = toWorldCoordinates(
                        _gridSize,
                        Point(x + 0.5, y + 0.5), gameMap.width,
                        gameMap.height
                    )
                    val component = _rockCountersLayer.text(
                        num.toString(), textSize = 15.0, alignment = TextAlignment
                            .MIDDLE_CENTER,
                        font = ENTITY_TEXT_FONT
                    ).xy(
                        worldX,
                        worldY
                    ).apply {
                        scaledHeight = _gridSize / 2
                        scaledWidth = scaledHeight * unscaledWidth / unscaledHeight
                    }
                    _drawnRockCounters[IntPoint(x, y)] = component
                }
            }
        }
        _rockCountersLayer.visible = false
    }

    private fun drawBoard() {
        println("Drawing board")
        when (uiMapSettings.boardType) {
            BoardType.SOLID -> _boardLayer.solidRect(
                _gridSize * gameMap.width, _gridSize * gameMap.height,
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
                            .xy(i * _gridSize, j * _gridSize)
                        altColorHeight = !altColorHeight
                    }
                    altColorWidth = !altColorWidth
                }
            }
            BoardType.CHECKERED_2X2 -> {
                var altColorWidth = true
                val gridSize = _gridSize * 2
                for (i in 0 until ((gameMap.width + 1) / 2)) {
                    var altColorHeight = altColorWidth
                    for (j in 0 until ((gameMap.height + 1) / 2)) {
                        val gridWidth = if ((i + 1) * 2 > gameMap.width) _gridSize else gridSize
                        val gridHeight = if ((j + 1) * 2 > gameMap.height) _gridSize else gridSize
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
                    this.line(i * _gridSize, 0.0, i * _gridSize, gameMap.height * _gridSize)
                }
                for (j in 0..gameMap.height) {
                    this.line(0.0, j * _gridSize, gameMap.width * _gridSize, j * _gridSize)
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
                i * _gridSize, 0.0
            )
            _gridNumberLayer.text(i.toString(), textSize = _gridNumberFontSize).xy(
                i * _gridSize, gameMap.height * _gridSize
            )
        }
        for (j in 0 until gameMap.height) {
            _gridNumberLayer.text(
                j.toString(),
                textSize = _gridNumberFontSize,
                alignment = TextAlignment.BASELINE_RIGHT
            ).xy(
                -10.0, gameMap.height * _gridSize - j * _gridSize
            )
            _gridNumberLayer.text(
                j.toString(),
                textSize = _gridNumberFontSize,
                alignment = TextAlignment.BASELINE_LEFT
            ).xy(
                gameMap.width * _gridSize + 10.0, gameMap.height * _gridSize - j * _gridSize
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
                entity.centerPoint, gameMap.width,
                gameMap.height
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
                            var (p1WorldX, p1WorldY) = toWorldCoordinates(
                                _gridSize,
                                segment.point1, gameMap.width,
                                gameMap.height
                            )
                            var (p2WorldX, p2WorldY) = toWorldCoordinates(
                                _gridSize,
                                segment.point2, gameMap.width,
                                gameMap.height
                            )
                            // TODO: This is a workaround since Korge cannot render vertical/horizontal
                            // lines correctly. Follow bug here:
                            // https://github.com/korlibs/korge-next/issues/392
                            if (p1WorldX == p2WorldX) {
                                p2WorldX += 1
                            }
                            if (p1WorldY == p2WorldY) {
                                p2WorldY += 1
                            }
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

    fun renderHighlightRectangle(gridX: Int, gridY: Int, entityWidth: Int, entityHeight: Int) {
        val (worldX, worldY) = toWorldCoordinates(
            _gridSize,
            IntPoint(gridX, gridY),
            gameMap.width, gameMap.height, entityHeight
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

    fun renderHighlightEntity(entity: MapEntity) {
        val (worldX, worldY) = toWorldCoordinates(
            _gridSize, entity, gameMap.width, gameMap
                .height
        )
        val view = createEntityView(entity).apply {
            addTo(_highlightLayer)
            xy(worldX, worldY)
        }

    }

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
                    renderHighlightRectangle(
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
                        data.entity.intPoint,
                        gameMap.width, gameMap.height, data.entity.height
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
        val localXY = _boardLayer.globalToLocalXY(globalMouseX, globalMouseY)
        val unprojected = Point(
            localXY.x,
            mapHeight * _gridSize - localXY.y
        )

        val gridX = unprojected.x / _gridSize
        val gridY = unprojected.y / _gridSize

        return gridX to gridY
    }

    fun getRoundedGridCoordinates(
        gridX: Double,
        gridY: Double,
        entityWidth: Int,
        entityHeight: Int,
    ): Pair<Int, Int> {
        val roundedGridX = when {
            entityWidth == 1 -> floor(
                gridX - entityWidth / 2
            ).toInt()
            else -> (gridX - entityWidth / 2).roundToInt()
        }

        val roundedGridY = when {
            entityHeight == 1 -> floor(
                gridY - entityHeight / 2
            ).toInt()
            else -> (gridY - entityHeight / 2).roundToInt()
        }

        val gridXToInt = roundedGridX.clamp(
            0,
            mapWidth - entityWidth
        )
        val gridYToInt = roundedGridY.clamp(
            0,
            mapHeight - entityHeight
        )
        return gridXToInt to gridYToInt
    }

    fun getIntersectingEntities(rect: Rectangle): List<UIEntity> {
        return _drawnEntities.values.asSequence().flatten().filter {
            rect.intersects(it.getGlobalBounds())
        }.toList()

    }
}