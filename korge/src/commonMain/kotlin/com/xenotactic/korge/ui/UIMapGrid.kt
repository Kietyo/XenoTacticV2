package com.xenotactic.korge.ui

import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.utils.size
import korlibs.image.color.MaterialColors
import korlibs.korge.ui.uiScrollable
import korlibs.korge.view.Container
import korlibs.korge.view.addTo
import korlibs.logger.Logger

inline fun Container.uiMapGrid(
    eventBus: EventBus,
    maxColumns: Int,
    maxRows: Int,
    gameMapViewWidth: Double,
    gameMapViewHeight: Double,
    paddingHorizontal: Float,
    paddingVertical: Float,
    gameMaps: List<GameMap>
): UIMapGrid = UIMapGrid(
    eventBus,
    maxColumns,
    maxRows,
    gameMapViewWidth,
    gameMapViewHeight,
    paddingHorizontal,
    paddingVertical,
    gameMaps,
).addTo(this)

/**
 * Creates a scrollable grid of maps.
 *
 * The maximum number of columns and rows are controlled by
 * the `maxColumns` and `maxHeight` parameters.
 *
 * If not enough game maps are provided to fill the entire grid,
 * then the grid will just be partially populated.
 *
 * If the number of provided game maps is more than the grid size,
 * then it will stop rendering anymore maps once it reaches the
 * desired grid size.
 */
class UIMapGrid(
    val eventBus: EventBus,
    val maxColumns: Int,
    val maxRows: Int,
    val gameMapViewWidth: Double,
    val gameMapViewHeight: Double,
    val entryPaddingHorizontal: Float,
    val entryPaddingVertical: Float,
    val gameMaps: List<GameMap>
) : Container() {
    init {
        val mapIterator = gameMaps.iterator()

        this.uiScrollable(
            gameMapViewWidth size
                    gameMapViewHeight,
            config = {
                this.backgroundColor = MaterialColors.GRAY_600
            }
        ) {
            val mapEntryWidth = (gameMapViewWidth - entryPaddingHorizontal * (maxColumns - 1)) /
                    maxColumns
            val mapEntryHeight = (gameMapViewHeight - entryPaddingVertical * (maxRows - 1)) / maxRows
            logger.info {
                """
                mapEntryWidth: $mapEntryWidth, mapEntryHeight: $mapEntryHeight
            """.trimIndent()
            }
            rowloop@ for (j in 0 until maxRows) {
                for (i in 0 until maxColumns) {
                    if (!mapIterator.hasNext()) break@rowloop
                    val map = mapIterator.next()
                    val mapEntry = this.uiMapEntry(
                        map, _width = mapEntryWidth,
                        _height = mapEntryHeight
                    )
                    mapEntry.x += i * mapEntry.width + i * entryPaddingHorizontal
                    mapEntry.y += j * mapEntry.height + j * entryPaddingVertical
                }
            }
        }

        logger.info {
            """
                this.scaledWidth: ${this.scaledWidth}
                this.scaledHeight: ${this.scaledHeight}
                Finished initializing.
            """.trimIndent()
        }
    }

    companion object {
        val logger = Logger<UIMapGrid>()
    }
}