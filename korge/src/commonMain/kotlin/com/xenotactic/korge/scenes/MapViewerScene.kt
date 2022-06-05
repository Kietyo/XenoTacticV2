package com.xenotactic.korge.scenes

import com.soywiz.klogger.Logger
import com.soywiz.korge.annotations.KorgeExperimental
import com.soywiz.korge.component.onStageResized
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.xenotactic.gamelogic.mapid.MapToId
import com.xenotactic.korge.daos.PlayerDataApi
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.events.PlayMapEvent
import com.xenotactic.korge.korge_components.ResizeDebugComponent
import com.xenotactic.korge.korge_utils.alignLeftToLeftOfWindow
import com.xenotactic.korge.korge_utils.alignRightToRightOfWindow
import com.xenotactic.korge.korge_utils.alignTopToTopOfWindow
import com.xenotactic.korge.ui.MapWithMetadata
import com.xenotactic.korge.ui.UIHeader
import com.xenotactic.korge.ui.UIHeaderSection
import com.xenotactic.korge.ui.UIMapEntry
import com.xenotactic.korge.ui.uiFixedGrid
import com.xenotactic.korge.ui.uiMapInspector
import getGoldenJsonFiles
import kotlinx.coroutines.launch
import toGameMap
import verify


class MapViewerScene(
    val globalEventBus: EventBus
) : Scene() {

    @OptIn(KorgeExperimental::class)
    override suspend fun Container.sceneInit() {
        println("MapViewerScene: Init")

        val HEADER_HEIGHT = 50.0

        val playerData = PlayerDataApi.getPlayerData()
        playerData.userName = "XenoTactic"
        PlayerDataApi.savePlayerData(playerData)

        val header = UIHeader(playerData.userName, HEADER_HEIGHT, getVisibleGlobalArea().width)
            .addTo(this)

        val maxColumns = 4
        val maxRows = 5

        val goldenMaps = getGoldenJsonFiles().take(maxColumns * maxRows).map {
            val gameMap = it.toGameMap()!!
            MapWithMetadata(
                it,
                gameMap,
                gameMap.verify()
            )
        }

        val mapInspector = this.uiMapInspector()

        val playEntries = goldenMaps.map { mapWithMetadata ->
            { width: Double, height: Double ->
                UIMapEntry(
                    mapWithMetadata.map,
                    width, height
                ).apply {
                    onPlayButtonClick {
                        globalEventBus.send(PlayMapEvent(mapWithMetadata.map))
                    }
                    onSaveButtonClick {
                        playerData.maps[MapToId.calculateId(mapWithMetadata.map)] =
                            mapWithMetadata.map
                        launch {
                            PlayerDataApi.savePlayerData(playerData)
                        }
                        Unit
                    }
                    onMapSectionClick {
                        println("Map clicked! ${mapWithMetadata.map}")
                        mapInspector.setMap(mapWithMetadata.map)
                    }
                }
            }
        }

        val mapGrid = this.uiFixedGrid(
            globalEventBus,
            maxColumns,
            maxRows,
            700.0, 500.0,
            10.0,
            10.0
        )
        mapGrid.resetWithEntries(
            playEntries
        )


        this.onStageResized(true) { width, height ->
            header.alignLeftToLeftOfWindow()
            header.alignTopToTopOfWindow()
            mapGrid.alignTopToBottomOf(header)
            mapGrid.alignLeftToLeftOfWindow()
            mapInspector.alignTopToBottomOf(header)
            mapInspector.alignRightToRightOfWindow()
        }

        addComponent(ResizeDebugComponent(this))

        header.onHeaderSectionClick {
            when (it) {
                UIHeaderSection.PLAY -> {
                    header.updateSelectionBox(it)
                    mapGrid.resetWithEntries(playEntries)
                }
                UIHeaderSection.EDITOR -> TODO()
                UIHeaderSection.MY_MAPS -> {
                    header.updateSelectionBox(it)
                    mapGrid.resetWithEntries(
                        playerData.maps.map { (_, map) ->
                            { width: Double, height: Double ->
                                UIMapEntry(
                                    map,
                                    width, height
                                ).apply {
                                    onPlayButtonClick {
                                        globalEventBus.send(PlayMapEvent(map))
                                    }
                                    onSaveButtonClick {
                                        playerData.maps[MapToId.calculateId(map)] = map
                                        launch {
                                            PlayerDataApi.savePlayerData(playerData)
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        logger.info {
            "Finished initializing MapViewerScene."
        }

    }

    companion object {
        val logger = Logger<MapViewerScene>()
    }
}
