package com.xenotactic.korge.ui

import com.soywiz.korge.view.Container
import com.soywiz.korge.view.container
import com.soywiz.korge.view.filter.IdentityFilter
import com.soywiz.korge.view.filter.filter
import com.soywiz.korge.view.solidRect
import com.soywiz.korge.view.text
import com.soywiz.korge.view.xy
import com.soywiz.korim.color.MaterialColors
import com.soywiz.korim.text.TextAlignment
import com.xenotactic.gamelogic.globals.BORDER_RATIO
import com.xenotactic.gamelogic.globals.GRID_LINES_RATIO
import com.xenotactic.gamelogic.globals.GRID_NUMBERS_RATIO
import com.xenotactic.gamelogic.globals.GRID_SIZE
import com.xenotactic.gamelogic.globals.PATH_LINES_RATIO

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
    private val _width get() = uiMapSettingsV2.width
    private val _height get() = uiMapSettingsV2.height

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

    init {
        drawBoard()
        drawGridNumbers()
    }

    fun getWorldCoordinates(x: Int, y: Int, entityHeight: Int = 0) =
        Pair(x * gridSize, (_height - y - entityHeight) * gridSize)

    fun toWorldDimensions(width: Int, height: Int) = Pair(width * gridSize, height * gridSize)

    private fun drawBoard() {
        println("Drawing board")
        when (uiMapSettingsV2.boardType) {
            BoardType.SOLID -> _boardLayer.solidRect(
                gridSize * _width,
                gridSize * _height,
                MaterialColors.GREEN_600
            )
            BoardType.CHECKERED_1X1 -> {
                var altColorWidth = true
                for (i in 0 until _width) {
                    var altColorHeight = altColorWidth
                    for (j in 0 until _height) {
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
                for (i in 0 until ((_width + 1) / 2)) {
                    var altColorHeight = altColorWidth
                    for (j in 0 until ((_height + 1) / 2)) {
                        val gridWidth = if ((i + 1) * 2 > _width) this.gridSize else gridSize
                        val gridHeight = if ((j + 1) * 2 > _height) this.gridSize else gridSize
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

        for (i in 0 until _width) {
            _gridNumberLayer.text(
                i.toString(),
                textSize = gridNumberFontSize,
                alignment = TextAlignment.BOTTOM_LEFT
            ).xy(
                i * gridSize, 0.0
            )
            _gridNumberLayer.text(i.toString(), textSize = gridNumberFontSize).xy(
                i * gridSize, _height * gridSize
            )
        }
        for (j in 0 until _height) {
            _gridNumberLayer.text(
                j.toString(),
                textSize = gridNumberFontSize,
                alignment = TextAlignment.BASELINE_RIGHT
            ).xy(
                -10.0, _height * gridSize - j * gridSize
            )
            _gridNumberLayer.text(
                j.toString(),
                textSize = gridNumberFontSize,
                alignment = TextAlignment.BASELINE_LEFT
            ).xy(
                _width * gridSize + 10.0, _height * gridSize - j * gridSize
            )
        }
    }
}