package scenes

import korge_components.ResizeDebugComponent
import bridges.MapBridge
import com.soywiz.klogger.Logger
import com.soywiz.korge.annotations.KorgeExperimental
import com.soywiz.korge.component.onStageResized
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import daos.PlayerDataApi
import events.EventBus
import events.PlayMapEvent
import getGoldenJsonFiles
import korge_utils.alignLeftToLeftOfWindow
import korge_utils.alignTopToTopOfWindow
import kotlinx.coroutines.launch
import toGameMap
import ui.*
import verify


class MapViewerScene(
    val globalEventBus: EventBus,
    val mapBridge: MapBridge
) : Scene() {

    @OptIn(KorgeExperimental::class)
    override suspend fun Container.sceneInit() {
        println("MapViewerScene: Init")

        val playerData = PlayerDataApi.getPlayerData()
        playerData.userName = "XenoTactic"
        PlayerDataApi.savePlayerData(playerData)

        val header = UIHeader(playerData.userName, getVisibleGlobalArea().width).addTo(this)

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

        val mapGrid = this.uiFixedGrid(
            globalEventBus,
            maxColumns,
            maxRows,
            700.0, 500.0,
            10.0,
            10.0
        )
        mapGrid.resetWithEntries(
            goldenMaps.map { mapWithMetadata ->
                { width: Double, height: Double ->
                    UIMapEntry(
                        mapWithMetadata.map,
                        width, height
                    ).apply {
                        onPlayButtonClick {
                            globalEventBus.send(PlayMapEvent(mapWithMetadata.map))
                        }
                        onSaveButtonClick {
                            playerData.maps.add(mapWithMetadata.map)
                            launch {
                                PlayerDataApi.savePlayerData(playerData)
                            }
                        }
                    }
                }
            }
        )

        val mapInspector = this.uiMapInspector()

        this.onStageResized(true) { width, height ->
            header.alignLeftToLeftOfWindow()
            header.alignTopToTopOfWindow()
            mapGrid.alignTopToBottomOf(header)
            mapGrid.alignLeftToLeftOfWindow()
            mapInspector.alignTopToBottomOf(header)
            mapInspector.alignLeftToRightOf(mapGrid, padding = 10.0)

            //            println(
            //                """
            //                resize:
            //                width: $width,
            //                height: $height,
            //                gameMapView.width: ${gameMapView.width}
            //                gameMapView.height: ${gameMapView.height}
            //                gameMapView.scaledWidth: ${gameMapView.scaledWidth}
            //                gameMapView.scaledHeight: ${gameMapView.scaledHeight}
            //                this.actualWidth: ${this.actualWidth}
            //                this.actualHeight: ${this.actualHeight}
            //                this.virtualWidth: ${this.virtualWidth}
            //                this.virtualHeight: ${this.virtualHeight}
            //                this.actualVirtualWidth: ${this.actualVirtualWidth}
            //                this.actualVirtualHeight: ${this.actualVirtualHeight}
            //            """.trimIndent()
            //            )
        }

        addComponent(ResizeDebugComponent(this))

        header.onMyMapsClick {
            mapGrid.resetWithEntries(
                playerData.maps.map { map ->
                    { width: Double, height: Double ->
                        UIMapEntry(
                            map,
                            width, height
                        ).apply {
                            onPlayButtonClick {
                                globalEventBus.send(PlayMapEvent(map))
                            }
                            onSaveButtonClick {
                                playerData.maps.add(map)
                                launch {
                                    PlayerDataApi.savePlayerData(playerData)
                                }
                            }
                        }
                    }
                }
            )
        }

        logger.info {
            "Finished initializing MapViewerScene."
        }

    }

    companion object {
        val logger = Logger<MapViewerScene>()
    }
}
