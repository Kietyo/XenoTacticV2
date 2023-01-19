package com.xenotactic.korge.ui

import com.soywiz.klogger.Logger
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.xenotactic.gamelogic.utils.measureTime
import com.xenotactic.korge.events.EventBus

val EMPTY_BOX_FN: (x: Int, y:Int, width: Double, height: Double) -> View = {x,y,width, height ->
    SolidRect(width, height, Colors.DARKKHAKI)
}

fun Container.uiFixedGrid(
    maxColumns: Int,
    maxRows: Int,
    gridWidth: Double,
    gridHeight: Double,
    entryPaddingHorizontal: Double,
    entryPaddingVertical: Double,
    initialEntries: List<(x: Int, y: Int, width: Double, height: Double) -> Container> = emptyList()
): UIFixedGrid =
    UIFixedGrid(
        maxColumns,
        maxRows,
        gridWidth,
        gridHeight,
        entryPaddingHorizontal,
        entryPaddingVertical,
        initialEntries,
        EMPTY_BOX_FN
    ).addTo(this)

/**
 * Fixed grid UI entry where top left element is (0,0).
 * - x coordinate corresponds to elements from left to right
 * - y coordinate corresponds to elements from top to bottom
 */
class UIFixedGrid(
    val maxColumns: Int,
    val maxRows: Int,
    gridWidth: Double,
    gridHeight: Double,
    val entryPaddingHorizontal: Double,
    val entryPaddingVertical: Double,
    initialEntries: List<(x: Int, y: Int, width: Double, height: Double) -> Container> = emptyList(),
    val defaultInitializerFn: (x: Int, y: Int, width: Double, height: Double) -> View = EMPTY_BOX_FN
) : Container() {
    val gridEntryViewWidth =
        (gridWidth - entryPaddingHorizontal * (maxColumns - 1)) / maxColumns
    val gridEntryViewHeight =
        (gridHeight - entryPaddingVertical * (maxRows - 1)) / maxRows

    init {
        this.solidRect(gridWidth, gridHeight, color = Colors.LIGHTGRAY)
    }

    val gridEntryContainer = container()

    init {
        resetWithEntries(initialEntries)
    }


    fun resetWithEntries(entries: List<(x: Int, y: Int, width: Double, height: Double) -> Container>) {
        gridEntryContainer.removeChildren()
        val entriesIterator = entries.iterator()
        rowloop@ for (y in 0 until maxRows) {
            for (x in 0 until maxColumns) {
                val fn = if (entriesIterator.hasNext()) entriesIterator.next() else defaultInitializerFn
                val gridEntry = fn(x, y, gridEntryViewWidth, gridEntryViewHeight)
                gridEntry.x += x * gridEntryViewWidth + x * entryPaddingHorizontal
                gridEntry.y += y * gridEntryViewHeight + y * entryPaddingVertical
                gridEntryContainer.addChild(gridEntry)
            }
        }
    }

    companion object {
        val logger = Logger<UIFixedGrid>()
    }
}