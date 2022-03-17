package scenes

import korge_components.ResizeDebugComponent
import bridges.MapBridge
import com.soywiz.klogger.Logger
import com.soywiz.korge.component.docking.dockedTo
import com.soywiz.korge.component.onStageResized
import com.soywiz.korge.input.keys
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.Anchor
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
import korge_utils.alignBottomToBottomOfWindow
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

        val objectPlacementInputProcessor = ObjectPlacementInputProcessor(
            this, engine, mapView,
            mapRenderer._gridSize
        )

        addComponent(cameraInputProcessor)
        addComponent(objectPlacementInputProcessor)

        addComponent(ResizeDebugComponent(this))

        uiPlacement = uiPlacement(engine, eventBus)

        uiActiveTextNotifier(engine, eventBus).run {
            centerXOnStage()
        }

        val pathText = text("Path Length: XX") {
            onStageResized(true) {width: Int, height: Int ->
                alignBottomToBottomOfWindow()
            }
        }

        addComponent(KeyInputProcessor(this, eventBus))
//        addComponent(InformationalUI(this, engine, eventBus))
        val monstersComponent = MonstersComponent(mapView, engine, eventBus, mapRenderer._gridSize)
        addComponent(monstersComponent)

        val goalComponent = GoalComponent(engine, eventBus)
        engine.setOneTimeComponent(goalComponent)
        //        goalComponent.calculateGoalForMap()

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
