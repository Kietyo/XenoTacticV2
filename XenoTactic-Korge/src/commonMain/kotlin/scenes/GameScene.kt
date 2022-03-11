package scenes

import korge_components.ResizeDebugComponent
import bridges.MapBridge
import com.soywiz.klogger.Logger
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.camera
import com.soywiz.korge.view.centerXOnStage
import com.soywiz.korge.view.text
import components.GameMapComponent
import components.GoalComponent
import components.ObjectPlacementComponent
import engine.Engine
import events.EventBus
import events.ExitGameSceneEvent
import input_processors.CameraInputProcessor
import input_processors.KeyInputProcessor
import input_processors.ObjectPlacementInputProcessor
import korge_components.MonstersComponent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import renderer.MapRendererUpdater
import ui.*

class GameScene(val mapBridge: MapBridge) : Scene() {
    val eventBus = EventBus(MainScope())

    lateinit var uiPlacement: UIPlacement

    init {
        logger.debug {
            "init called"
        }
    }

    override suspend fun Container.sceneInit() {
        Logger.defaultLevel = Logger.Level.DEBUG
        logger.debug {
            "sceneInit called"
        }
        val engine = Engine()
        val gameMapComponent = GameMapComponent(engine, eventBus)
        val objectPlacementComponent = ObjectPlacementComponent(eventBus)
        engine.setOneTimeComponent(gameMapComponent)
        engine.setOneTimeComponent(objectPlacementComponent)

        //        val gameMap = loadGameMapFromGoldensBlocking("00051.json")
        val gameMap = mapBridge.gameMap

        gameMapComponent.updateMap(gameMap)

        //        this.setSize(gameMap.width * GRID_SIZE, gameMap.height * GRID_SIZE)

        val mapView = camera()
        val mapRenderer =
            mapView.uiMap(gameMap, shortestPath = gameMapComponent.shortestPath)
        val mapRendererUpdater = MapRendererUpdater(engine, mapRenderer, eventBus)
        engine.setOneTimeComponent(mapRenderer)

        val cameraInputProcessor = CameraInputProcessor(mapView, eventBus)
        cameraInputProcessor.setZoomFactor(0.7)
        val zoomSettingText = text("Zoom Factor: ${mapView.scale}", textSize = 20.0)

        val objectPlacementInputProcessor = ObjectPlacementInputProcessor(
            this, engine, mapView,
            mapRenderer._gridSize
        )

        addComponent(cameraInputProcessor)
        addComponent(objectPlacementInputProcessor)

        //        val dockedContainer = container {
        //            this.dockedTo(Anchor.BOTTOM_RIGHT)
        //        }
        //
        //        val uiStack = dockedContainer.uiVerticalStack {
        //            uiButton {
        //                text = "Tower"
        //            }
        //            uiButton {
        //                text = "Rock"
        //            }
        //            uiButton {
        //                text = "Delete"
        //            }
        //        }
        //
        //        uiStack.xy(-uiStack.width, -uiStack.height)


        //        val width = placementContainer.width
        //        val height = placementContainer.height
        addComponent(ResizeDebugComponent(this))
        //        addComponent(PlacementUI(this, engine, eventBus))

        uiPlacement = uiPlacement(engine, eventBus)
        //                uiPlacement.alignBottomToBottomOfWindow()

        uiActiveTextNotifier(engine, eventBus).run {
            centerXOnStage()
        }


        addComponent(KeyInputProcessor(this, eventBus))
        addComponent(InformationalUI(this, engine, eventBus))
        //        addComponent(GoalUI(this, eventBus))
        val monstersComponent = MonstersComponent(mapView, engine, eventBus, mapRenderer._gridSize)
        addComponent(monstersComponent)

        val goalComponent = GoalComponent(engine, eventBus)
        engine.setOneTimeComponent(goalComponent)
        //        goalComponent.calculateGoalForMap()

        //
        //        placementContainer.xy(500, 400)

        //        placementContainer.alignRightToRightOf(sceneContainer)
        //        placementContainer.alignBottomToBottomOf(sceneContainer)

        eventBus.register<ExitGameSceneEvent> {
            launch {
                sceneContainer.back()
            }
        }
    }

    override suspend fun sceneAfterInit() {
        logger.info {
            "sceneAfterInit called"
        }

    }

    override suspend fun sceneDestroy() {
        logger.debug {
            "sceneDestroy called"
        }
    }

    override suspend fun sceneBeforeLeaving() {
        logger.debug {
            "sceneBeforeLeaving called"
        }
    }

    override suspend fun Container.sceneMain() {
        logger.debug {
            "sceneMain called"
        }
    }

    override suspend fun sceneAfterDestroy() {
        logger.debug {
            "sceneAfterDestroy called"
        }
    }


    companion object {
        val logger = Logger<GameScene>()
    }
}
