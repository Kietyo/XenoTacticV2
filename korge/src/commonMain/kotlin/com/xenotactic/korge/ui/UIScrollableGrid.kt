package com.xenotactic.korge.ui

import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.utils.size
import korlibs.image.color.MaterialColors
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.ui.uiScrollable
import korlibs.korge.view.Container
import korlibs.korge.view.addTo
import korlibs.logger.Logger

fun Container.uiScrollableGrid(
    eventBus: EventBus,
    maxColumns: Int,
    maxRows: Int,
    gridWidth: Double,
    gridHeight: Double,
    entryPaddingHorizontal: Double,
    entryPaddingVertical: Double,
    entries: List<(width: Double, height: Double) -> Container>
): UIScrollableGrid =
    UIScrollableGrid(
        eventBus,
        maxColumns,
        maxRows,
        gridWidth,
        gridHeight,
        entryPaddingHorizontal,
        entryPaddingVertical,
        entries
    ).addTo(this)

/**
 * Represents a scrollable grid with `gridWidth` and `gridHeight` dimensions with
 * `maxColumns` columns and `maxRows` rows.
 *
 * The `entryPaddingHorizontal` and `entryPaddingVertical` defines the padding between each grid
 * entry in the grid.
 *
 * This grid will display at most `maxColumns` * `maxRows` items. It will exhaust the provided
 * list of entries until it reaches the maximum number of items. If not enough entries are provided
 * to populate the full grid, then it will be partially populated.
 *
 * For example, if a 4x4 grid is created and only 6 entries are provided, then the first row will
 * be populated and the second row will have 2 entries.
 *
 * The `entries` is a list of lambdas that are used to create the containers for each grid entry.
 * The lambdas must accept `width` and `height` parameters which represent the width and height
 * of each grid entry itself. This was implemented like this because the grid entry width and height
 * are calculated provided the given `gridWidth` and `gridHeight` with respect to the
 * `entryPaddingHorizontal` and `entryPaddingVertical`. This also allows us to only lazily create
 * as many containers that are needed to fill the grid.
 */
@OptIn(KorgeExperimental::class)
class UIScrollableGrid(
    val eventBus: EventBus,
    val maxColumns: Int,
    val maxRows: Int,
    gridWidth: Double,
    gridHeight: Double,
    val entryPaddingHorizontal: Double,
    val entryPaddingVertical: Double,
    val entries: List<(width: Double, height: Double) -> Container>
) : Container() {
    init {
        val gridEntryViewWidth = (gridWidth - entryPaddingHorizontal * (maxColumns - 1)) /
                maxColumns
        val gridEntryViewHeight = (gridHeight - entryPaddingVertical * (maxRows - 1)) / maxRows

        this.uiScrollable(gridWidth size gridHeight,
            config = {
                this.backgroundColor = MaterialColors.GRAY_600
            }) {
            val entriesIterator = entries.iterator()
            rowloop@ for (j in 0 until maxRows) {
                for (i in 0 until maxColumns) {
                    if (!entriesIterator.hasNext()) break@rowloop
                    val gridEntry = entriesIterator.next()(gridEntryViewWidth, gridEntryViewHeight)
                    gridEntry.x += (i * gridEntryViewWidth + i * entryPaddingHorizontal).toFloat()
                    gridEntry.y += (j * gridEntryViewHeight + j * entryPaddingVertical).toFloat()
                    this.addChild(gridEntry)
                }
            }
        }

    }

    companion object {
        val logger = Logger<UIScrollableGrid>()
    }
}