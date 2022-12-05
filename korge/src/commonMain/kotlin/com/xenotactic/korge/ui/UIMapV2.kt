package com.xenotactic.korge.ui

import com.soywiz.korge.view.*
import com.soywiz.korge.view.vector.gpuGraphics
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.MaterialColors
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.StrokeInfo
import com.soywiz.korma.geom.vector.line
import com.xenotactic.ecs.StagingEntity
import com.xenotactic.gamelogic.globals.BORDER_RATIO
import com.xenotactic.gamelogic.globals.GRID_LINES_RATIO
import com.xenotactic.gamelogic.globals.GRID_NUMBERS_RATIO
import com.xenotactic.gamelogic.globals.GRID_SIZE
import com.xenotactic.gamelogic.globals.PATH_LINES_RATIO
import com.xenotactic.gamelogic.korge_utils.size
import com.xenotactic.gamelogic.korge_utils.xy
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.utils.*
import com.xenotactic.gamelogic.views.UIEntity
import com.xenotactic.korge.components.EntitySpeedAreaComponent
import com.xenotactic.korge.components.EntityTypeComponent
import com.xenotactic.korge.components.SizeComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.ResizeMapEvent
import com.xenotactic.korge.events.UpdatedPathLineEvent
import com.xenotactic.korge.korge_utils.toWorldCoordinates
import com.xenotactic.korge.models.GameWorld
import com.xenotactic.korge.state.GameMapDimensionsState
import com.xenotactic.korge.state.GameMapPathState
import kotlin.ranges.until

data class UIMapSettingsV2(
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
    val engine: Engine,
    private val uiMapSettingsV2: UIMapSettingsV2 = UIMapSettingsV2(),
) : Container() {
    private val gameWorld: GameWorld = engine.gameWorld
    private val gameMapDimensionsState = engine.injections.getSingleton<GameMapDimensionsState>()
    val gridSize get() = uiMapSettingsV2.gridSize
    val gridNumberFontSize get() = uiMapSettingsV2.gridNumberFontSize
    val borderSize get() = uiMapSettingsV2.borderSize
    val mapWidth get() = gameMapDimensionsState.width
    val mapHeight get() = gameMapDimensionsState.height
    val _pathLinesWidth = uiMapSettingsV2.pathLinesWidth

    val gameMapPathState = engine.injections.getSingleton<GameMapPathState>()

    private val _boardLayer = this.container {
        //        this.propagateEvents = false
        //        this.mouseChildren = false
        //        this.hitTestEnabled = false
    }

    private val _boardGraphicsLayer = this.graphics { }

    private val _gridNumberLayer = this.container()

//    val speedAreaLayer = this.clipContainer(
//        mapWidth.toWorldUnit(gridSize).toDouble(),
//        mapHeight.toWorldUnit(gridSize).toDouble()
//    )

    val speedAreaLayer = this.container()

    val entityLayer = this.container().apply {
    }

    val entityLabelLayer = this.container()

    private val _pathingLinesGraphics = this.gpuGraphics {
        //        useNativeRendering = false
    }

    val monsterLayer = this.container().apply {
    }

    val targetingLinesLayer = this.graphics { }
    val projectileLayer = this.graphics { }

    val _highlightLayer = this.container()
    val _highlightRectangle = this.solidRect(0, 0, Colors.YELLOW).alpha(0.5).visible(false)

    init {
        resetUIMap()

        engine.eventBus.register<UpdatedPathLineEvent> {
            renderPathLines(it.pathSequence)
        }
    }

    fun resetUIMap() {
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
            uiMapEntityComponent.entityView.y += heightDiffWorldUnit.value
            val uiMapEntityTextComponent =
                gameWorld.uiMapEntityTextComponentContainer.getComponentOrNull(it)
            if (uiMapEntityTextComponent != null) {
                uiMapEntityTextComponent.textView.y += heightDiffWorldUnit.value
            }
        }
        renderPathLines(gameMapPathState.shortestPath)
    }

    fun getWorldCoordinates(x: GameUnit, y: GameUnit, entityHeight: GameUnit = GameUnit(0)) =
        Pair(WorldUnit(x.value * gridSize), WorldUnit((mapHeight - y - entityHeight).value * gridSize))

    fun toWorldDimensions(width: GameUnit, height: GameUnit) = Pair(
        WorldUnit(width * gridSize), WorldUnit(height * gridSize)
    )

    private fun drawBoard() {
        println("Drawing board")
        _boardLayer.removeChildren()
        when (uiMapSettingsV2.boardType) {
            BoardType.SOLID -> _boardLayer.solidRect(
                gridSize * mapWidth.value,
                gridSize * mapHeight.value,
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
                for (i in 0 until ((mapWidth.toInt() + 1) / 2)) {
                    var altColorHeight = altColorWidth
                    for (j in 0 until ((mapHeight.toInt() + 1) / 2)) {
                        val gridWidth = if ((i + 1) * 2 > mapWidth.value) this.gridSize else gridSize
                        val gridHeight = if ((j + 1) * 2 > mapHeight.value) this.gridSize else gridSize
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

    private fun drawBoardV2() {
        println("Drawing board")
        _boardGraphicsLayer.updateShape {

            when (uiMapSettingsV2.boardType) {
                BoardType.SOLID -> _boardLayer.solidRect(
                    gridSize * mapWidth.value,
                    gridSize * mapHeight.value,
                    MaterialColors.GREEN_600
                )

                BoardType.CHECKERED_1X1 -> {
                    var altColorWidth = true
                    for (i in 0 until mapWidth) {
                        var altColorHeight = altColorWidth
                        val xGrid = i * gridSize
                        for (j in 0 until mapHeight) {
                            val currColor =
                                if (altColorHeight) MaterialColors.GREEN_600 else MaterialColors
                                    .GREEN_800
                            this.fillStyle(currColor) {
                                this.fillRect(xGrid, j * gridSize, gridSize, gridSize)
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
                i * gridSize, mapHeight.value * gridSize
            )
        }
        for (j in 0 until mapHeight) {
            _gridNumberLayer.text(
                j.toString(),
                textSize = gridNumberFontSize,
                alignment = TextAlignment.BASELINE_RIGHT
            ).xy(
                -10.0, mapHeight.value * gridSize - j * gridSize
            )
            _gridNumberLayer.text(
                j.toString(),
                textSize = gridNumberFontSize,
                alignment = TextAlignment.BASELINE_LEFT
            ).xy(
                mapWidth.value * gridSize + 10.0, mapHeight.value * gridSize - j * gridSize
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
            mapHeight.value * gridSize - localXY.y
        )

        val gridX = unprojected.x / gridSize
        val gridY = unprojected.y / gridSize

        return gridX to gridY
    }

    fun getRoundedGridCoordinates(
        gridX: Double,
        gridY: Double,
        entityWidth: GameUnit,
        entityHeight: GameUnit,
    ): GameUnitTuple =
        com.xenotactic.korge.korge_utils.getRoundedGridCoordinates(
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
        val entityType = entity[EntityTypeComponent::class]
        val size = entity[SizeComponent::class]
        return UIEntity(
            entityType.type, size.width, size.height, gridSize, borderSize,
            if (entityType.type == MapEntityType.SPEED_AREA) {
                entity[EntitySpeedAreaComponent::class].speedEffect
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
                            this.line(
                                p1WorldX.value,
                                p1WorldY.value,
                                p2WorldX.value,
                                p2WorldY.value
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



