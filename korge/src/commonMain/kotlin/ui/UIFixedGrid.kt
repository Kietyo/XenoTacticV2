package ui

import com.soywiz.klogger.Logger
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.solidRect
import com.soywiz.korim.color.Colors
import com.xenotactic.gamelogic.utils.measureTime
import events.EventBus

fun Container.uiFixedGrid(
    eventBus: EventBus,
    maxColumns: Int,
    maxRows: Int,
    gridWidth: Double,
    gridHeight: Double,
    entryPaddingHorizontal: Double,
    entryPaddingVertical: Double,
    initialEntries: List<(width: Double, height: Double) -> Container> = emptyList()
): UIFixedGrid =
    UIFixedGrid(
        eventBus,
        maxColumns,
        maxRows,
        gridWidth,
        gridHeight,
        entryPaddingHorizontal,
        entryPaddingVertical,
        initialEntries
    ).addTo(this)

class UIFixedGrid(
    val eventBus: EventBus,
    val maxColumns: Int,
    val maxRows: Int,
    gridWidth: Double,
    gridHeight: Double,
    val entryPaddingHorizontal: Double,
    val entryPaddingVertical: Double,
    initialEntries: List<(width: Double, height: Double) -> Container> = emptyList()
) : Container() {
    val gridEntryViewWidth =
        (gridWidth - entryPaddingHorizontal * (maxColumns - 1)) / maxColumns
    val gridEntryViewHeight =
        (gridHeight - entryPaddingVertical * (maxRows - 1)) / maxRows

    init {
        this.solidRect(gridWidth, gridHeight, color = Colors.DARKGRAY)
        resetWithEntries(initialEntries)
    }

    fun resetWithEntries(entries: List<(width: Double, height: Double) -> Container>) {
        removeChildren()
        val entriesIterator = entries.iterator()
        rowloop@ for (j in 0 until maxRows) {
            for (i in 0 until maxColumns) {
                if (!entriesIterator.hasNext()) break@rowloop
                measureTime(message = "Time it takes to setup grid entry: $i, $j") {
                    val gridEntry = entriesIterator.next()(gridEntryViewWidth, gridEntryViewHeight)
                    gridEntry.x += i * gridEntryViewWidth + i * entryPaddingHorizontal
                    gridEntry.y += j * gridEntryViewHeight + j * entryPaddingVertical
                    addChild(gridEntry)
                }
            }
        }
    }

    companion object {
        val logger = Logger<UIFixedGrid>()
    }
}