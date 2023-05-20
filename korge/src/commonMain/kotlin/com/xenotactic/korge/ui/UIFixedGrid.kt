package com.xenotactic.korge.ui

import korlibs.image.color.Colors
import korlibs.image.color.MaterialColors
import korlibs.image.color.RGBA
import korlibs.korge.view.*
import korlibs.logger.Logger
import korlibs.math.geom.PointInt

val EMPTY_BOX_FN: (x: Int, y: Int, width: Float, height: Float) -> View = { x, y, width, height ->
    SolidRect(width, height, MaterialColors.TEAL_100)
}

fun Container.uiFixedGrid(
    maxColumns: Int,
    maxRows: Int,
    gridWidth: Number,
    gridHeight: Number,
    entryPaddingHorizontal: Number,
    entryPaddingVertical: Number,
    initialEntries: List<(x: Int, y: Int, width: Float, height: Float) -> Container> = emptyList()
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
    gridWidth: Number,
    gridHeight: Number,
    val entryPaddingHorizontal: Number,
    val entryPaddingVertical: Number,
    initialEntries: List<(x: Int, y: Int, width: Float, height: Float) -> Container> = emptyList(),
    val defaultInitializerFn: (x: Int, y: Int, width: Float, height: Float) -> View = EMPTY_BOX_FN,
    backgroundColor: RGBA = Colors.DARKMAGENTA
) : Container() {
    val gridEntryViewWidth =
        (gridWidth.toFloat() - entryPaddingHorizontal.toFloat() * (maxColumns + 1)) / maxColumns
    val gridEntryViewHeight =
        (gridHeight.toFloat() - entryPaddingVertical.toFloat() * (maxRows + 1)) / maxRows

    init {
        this.solidRect(gridWidth.toFloat(), gridHeight.toFloat(), color = backgroundColor)
        println(
            """
            gridEntryViewWidth: $gridEntryViewWidth
            gridEntryViewHeight: $gridEntryViewHeight
        """.trimIndent()
        )
    }

    val gridEntryContainer = container()
    val coordToView = mutableMapOf<PointInt, View>()

    init {
        resetWithEntries(initialEntries)
    }

    fun setEntry(x: Int, y: Int, view: View) {
        val point = PointInt(x, y)
        val currView = coordToView[point]!!
        currView.removeFromParent()
        val scaledView = view.scaleWhileMaintainingAspect(
            ScalingOption.ByWidthAndHeight(
                gridEntryViewWidth.toDouble(), gridEntryViewHeight.toDouble()
            )
        )
        scaledView.addTo(gridEntryContainer)
        scaledView.x = calculateXPosition(x)
        scaledView.y = calculateYPosition(y)
        coordToView[point] = scaledView
    }

    fun clearEntry(x: Int, y: Int) {
        setEntry(x, y, defaultInitializerFn(x, y, gridEntryViewWidth, gridEntryViewHeight))
    }

    fun resetWithEntries(entries: List<(x: Int, y: Int, width: Float, height: Float) -> View>) {
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

    private fun calculateXPosition(x: Int): Float {
        return x * gridEntryViewWidth + (x + 1) * entryPaddingHorizontal.toFloat()
    }

    private fun calculateYPosition(y: Int): Float {
        return y * gridEntryViewHeight + (y + 1) * entryPaddingVertical.toFloat()
    }

    fun clear() {
        rowloop@ for (y in 0 until maxRows) {
            for (x in 0 until maxColumns) {
                clearEntry(x, y)
            }
        }
    }

    companion object {
        val logger = Logger<UIFixedGrid>()
    }
}