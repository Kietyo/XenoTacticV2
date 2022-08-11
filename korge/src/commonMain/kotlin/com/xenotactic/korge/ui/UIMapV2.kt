package com.xenotactic.korge.ui

import com.soywiz.korge.view.Container
import com.soywiz.korge.view.container
import com.soywiz.korge.view.solidRect
import com.soywiz.korge.view.xy
import com.xenotactic.gamelogic.globals.BORDER_RATIO
import com.xenotactic.gamelogic.globals.GRID_LINES_RATIO
import com.xenotactic.gamelogic.globals.GRID_NUMBERS_RATIO
import com.xenotactic.gamelogic.globals.GRID_SIZE
import com.xenotactic.gamelogic.globals.PATH_LINES_RATIO
import com.xenotactic.korge.korge_utils.MaterialColors

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
    private val _gridSize get() = uiMapSettingsV2.gridSize
    private val _width get() = uiMapSettingsV2.width
    private val _height get() = uiMapSettingsV2.height

    val _boardLayer = this.container {
        //        this.propagateEvents = false
        //        this.mouseChildren = false
        //        this.hitTestEnabled = false
    }

    init {
        drawBoard()
    }

    private fun drawBoard() {
        println("Drawing board")
        when (uiMapSettingsV2.boardType) {
            BoardType.SOLID -> _boardLayer.solidRect(
                _gridSize * _width,
                _gridSize * _height,
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
                for (i in 0 until ((_width + 1) / 2)) {
                    var altColorHeight = altColorWidth
                    for (j in 0 until ((_height + 1) / 2)) {
                        val gridWidth = if ((i + 1) * 2 > _width) _gridSize else gridSize
                        val gridHeight = if ((j + 1) * 2 > _height) _gridSize else gridSize
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
}