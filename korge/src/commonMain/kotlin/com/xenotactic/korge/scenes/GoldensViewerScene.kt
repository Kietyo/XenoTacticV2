package com.xenotactic.korge.scenes

import MapVerificationResult
import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.getGoldenJsonFiles
import com.xenotactic.gamelogic.utils.measureTime
import com.xenotactic.gamelogic.utils.toGameMap
import com.xenotactic.korge.events.GoldensEntryClickEvent
import com.xenotactic.korge.events.GoldensEntryHoverOnEvent
import com.xenotactic.korge.ui.*
import korlibs.datastructure.iterators.parallelMap
import korlibs.event.Key
import korlibs.event.KeyEvent
import korlibs.io.file.baseName
import korlibs.korge.input.onClick
import korlibs.korge.scene.Scene
import korlibs.korge.ui.uiButton
import korlibs.korge.view.SContainer
import korlibs.korge.view.align.alignLeftToRightOf
import korlibs.korge.view.align.alignTopToBottomOf
import korlibs.korge.view.text
import korlibs.logger.Logger
import korlibs.math.geom.Size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import pathing.PathFinder
import verify

enum class MapFilterOptions(val text: String) {
    ALL("All maps"),
    GOOD_MAPS_ONLY("Good maps"),
    BAD_MAPS_ONLY("Bad maps"),
}

class GoldensViewerScene : Scene() {
    companion object {
        val logger = Logger<GoldensViewerScene>()
    }

    override suspend fun SContainer.sceneInit() {
        logger.info {
            "sceneInit"
        }

        val eventBus = EventBus(CoroutineScope(Dispatchers.Default))

        val engine = Engine(eventBus)

        val maxColumns = 7
        val maxRows = 5
        val allGoldenMapsVfsFiles = getGoldenJsonFiles().parallelMap {
            val gameMap = it.toGameMap()!!
            MapWithMetadata(
                it,
                gameMap,
                gameMap.verify()
            )
        }

        var chunkedMaps = allGoldenMapsVfsFiles.chunked(maxColumns * maxRows)

        val mapGrid = this.uiFixedGrid(
            maxColumns,
            maxRows,
            1000.0, 450.0,
            10.0,
            10.0,
        )
        val pageDropdown = this.uiDropdown()
        val prevButton = this.uiButton(
            size = Size(50.0, 20.0),
            label = "Prev"
        ) {
            alignLeftToRightOf(pageDropdown)
            onClick {
                pageDropdown.previousEntry()
            }
        }
        val nextButton = this.uiButton(
            size = Size(50.0, 20.0), label = "Next"
        ) {
            alignLeftToRightOf(prevButton)
            onClick {
                pageDropdown.nextEntry()
            }
        }
        val mapFilterDropdown = this.uiDropdown(
            MapFilterOptions.values().map {
                UIDropdownOption.StringOption(
                    it.text, it.toString()
                )
            }
        ).apply {
            alignLeftToRightOf(nextButton, padding = 20.0)
        }

        pageDropdown.resetWithOptions(
            allGoldenMapsVfsFiles.chunked(maxColumns * maxRows).withIndex().map {
                UIDropdownOption.NumberOption(
                    "Page ${it.index}",
                    it.index
                )
            })

        fun resetGridWithCurrentDropdownValue() {
            measureTime("resetGridWithCurrentDropdownValue time") {
                val currOption = pageDropdown.getCurrentOption()
                require(currOption is UIDropdownOption.NumberOption)
                mapGrid.resetWithEntries(chunkedMaps[currOption.data.toInt()].map { it ->
                    { _, _, width: Float, height: Float ->
                        UIGoldensViewerEntry(eventBus, it, width, height)
                    }
                })
            }

        }

        resetGridWithCurrentDropdownValue()

        pageDropdown.onDropdownChange {
            resetGridWithCurrentDropdownValue()
        }

        mapFilterDropdown.onDropdownChange {
            require(it.newOption is UIDropdownOption.StringOption)
            val enum = MapFilterOptions.valueOf(it.newOption.data)
            chunkedMaps = when (enum) {
                MapFilterOptions.ALL -> {
                    allGoldenMapsVfsFiles
                }

                MapFilterOptions.GOOD_MAPS_ONLY -> {
                    allGoldenMapsVfsFiles.filter { it.verificationResult is MapVerificationResult.Success }
                }

                MapFilterOptions.BAD_MAPS_ONLY -> {
                    allGoldenMapsVfsFiles.filter { it.verificationResult is MapVerificationResult.Failure }
                }
            }.chunked(maxColumns * maxRows)
            pageDropdown.resetWithOptions(chunkedMaps.withIndex().map { idxVal ->
                UIDropdownOption.NumberOption(
                    "Page ${idxVal.index}",
                    idxVal.index
                )
            })
            resetGridWithCurrentDropdownValue()
        }

        mapGrid.alignTopToBottomOf(pageDropdown)


        logger.info {
            """
                mapGrid.scaledWidth: ${mapGrid.scaledWidth}
                mapGrid.scaledHeight: ${mapGrid.scaledHeight}
            """.trimIndent()
        }

        val descriptionText = this.text("")
        descriptionText.alignTopToBottomOf(mapGrid)

        val overlayContainer = UIMapOverlay(engine)

        eventBus.register<GoldensEntryClickEvent> {
            overlayContainer.setOverlay(
                UIMap(
                    it.gameMap,
                    engine,
                    shortestPath = PathFinder.getShortestPath(it.gameMap)
                )
            )
        }

        eventBus.register<UIMapOverlayOutsideClickedEvent> {
            overlayContainer.clearOverlay()
        }

        eventBus.register<GoldensEntryHoverOnEvent> {
            val verificationResult = it.verificationResult
            descriptionText.text = when (verificationResult) {
                MapVerificationResult.Success -> "${it.mapFile.baseName}: LGTM"
                is MapVerificationResult.Failure ->
                    "${it.mapFile.baseName}: ${verificationResult.error}"

                else -> TODO()
            }
        }

        onEvent(KeyEvent.Type.UP) {
            if (it.key == Key.ESCAPE) {
                overlayContainer.clearOverlay()
            }
        }

        logger.info {
            "sceneInit: Finished initializing"
        }
    }

    override suspend fun SContainer.sceneMain() {
        logger.info {
            "sceneMain"
        }
    }

    override suspend fun sceneDestroy() {
        super.sceneDestroy()
        logger.info {
            "sceneDestroy"
        }
    }


}
