package com.xenotactic.korge.ui

import com.soywiz.kds.Array2
import com.soywiz.klogger.Logger
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.MaterialColors
import com.soywiz.korim.color.RGBA
import com.soywiz.korma.annotations.KormaValueApi
import com.soywiz.korma.geom.PointInt
import com.xenotactic.gamelogic.utils.measureTime
import com.xenotactic.korge.events.EventBus

val EMPTY_BOX_FN: (x: Int, y:Int, width: Double, height: Double) -> View = {x,y,width, height ->
    SolidRect(width, height, MaterialColors.TEAL_100)
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
    val defaultInitializerFn: (x: Int, y: Int, width: Double, height: Double) -> View = EMPTY_BOX_FN,
    backgroundColor: RGBA = Colors.DARKMAGENTA
) : Container() {
    val gridEntryViewWidth =
        (gridWidth - entryPaddingHorizontal * (maxColumns + 1)) / maxColumns
    val gridEntryViewHeight =
        (gridHeight - entryPaddingVertical * (maxRows + 1)) / maxRows

    init {
        this.solidRect(gridWidth, gridHeight, color = backgroundColor)
        println("""
            gridEntryViewWidth: $gridEntryViewWidth
            gridEntryViewHeight: $gridEntryViewHeight
        """.trimIndent())
    }

    val gridEntryContainer = container()
    val coordToView = mutableMapOf<PointInt, View>()

    init {
        resetWithEntries(initialEntries)
    }

    @OptIn(KormaValueApi::class)
    fun setEntry(x: Int, y: Int, view: View) {
        val point = PointInt(x, y)
        val currView = coordToView[point]!!
        currView.removeFromParent()
        val scaledView = view.scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(
            gridEntryViewWidth, gridEntryViewHeight
        ))
        scaledView.addTo(gridEntryContainer)
        scaledView.x = calculateXPosition(x)
        scaledView.y = calculateYPosition(y)
        coordToView[point] = scaledView
    }

    fun clearEntry(x: Int, y: Int) {
        setEntry(x, y, defaultInitializerFn(x, y, gridEntryViewWidth, gridEntryViewHeight))
    }

    fun resetWithEntries(entries: List<(x: Int, y: Int, width: Double, height: Double) -> View>) {
        gridEntryContainer.removeChildren()
        val entriesIterator = entries.iterator()
        rowloop@ for (y in 0 until maxRows) {
            for (x in 0 until maxColumns) {
                val fn = if (entriesIterator.hasNext()) entriesIterator.next() else defaultInitializerFn
                val gridEntry = fn(x, y, gridEntryViewWidth, gridEntryViewHeight)
                gridEntry.x = calculateXPosition(x)
                gridEntry.y = calculateYPosition(y)
                gridEntryContainer.addChild(gridEntry)
                coordToView[PointInt(x, y)] = gridEntry
            }
        }
    }

    private fun calculateXPosition(x: Int): Double {
        return x * gridEntryViewWidth + (x + 1) * entryPaddingHorizontal
    }

    private fun calculateYPosition(y: Int): Double {
        return y * gridEntryViewHeight + (y + 1) * entryPaddingVertical
    }

    companion object {
        val logger = Logger<UIFixedGrid>()
    }
}