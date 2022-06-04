package com.xenotactic.korge.scenes

import com.soywiz.klock.TimeSpan
import com.soywiz.klogger.Logger
import com.soywiz.korev.Key
import com.soywiz.korge.component.onStageResized
import com.soywiz.korge.input.draggable
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.xenotactic.korge.bridges.MapBridge
import com.xenotactic.korge.components.GameMapControllerEComponent
import com.xenotactic.korge.components.GoalEComponent
import com.xenotactic.korge.components.ObjectPlacementEComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.events.ExitGameSceneEvent
import com.xenotactic.korge.events.UpdatedPathLengthEvent
import com.xenotactic.korge.input_processors.CameraInputProcessor
import com.xenotactic.korge.input_processors.KeyInputProcessor
import com.xenotactic.korge.input_processors.ObjectPlacementInputProcessor
import com.xenotactic.korge.korge_components.MonstersEComponent
import com.xenotactic.korge.korge_components.ResizeDebugComponent
import com.xenotactic.korge.korge_utils.alignBottomToBottomOfWindow
import com.xenotactic.korge.korge_utils.alignRightToRightOfWindow
import com.xenotactic.korge.renderer.MapRendererUpdater
import com.xenotactic.korge.ui.UIMap
import com.xenotactic.korge.ui.UIPathText
import com.xenotactic.korge.ui.UIPlacementButton
import com.xenotactic.korge.ui.uiActiveTextNotifier
import com.xenotactic.korge.ui.uiPlacement
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class GameScene(val mapBridge: MapBridge) : Scene() {
    val eventBus = EventBus(MainScope())

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
        val engine = Engine(eventBus)
        val gameMapControllerComponent = GameMapControllerEComponent(engine, eventBus)
        val objectPlacementComponent = ObjectPlacementEComponent()
        engine.setOneTimeComponent(gameMapControllerComponent)
        engine.setOneTimeComponent(objectPlacementComponent)

        //        val gameMap = loadGameMapFromGoldensBlocking("00051.json")
        val gameMap = mapBridge.gameMap

        gameMapControllerComponent.updateMap(gameMap)

        //        this.setSize(gameMap.width * GRID_SIZE, gameMap.height * GRID_SIZE)

        val uiMap =
            UIMap(gameMap, engine, shortestPath = gameMapControllerComponent.shortestPath).apply {
                draggable()
            }
        MapRendererUpdater(engine, uiMap, eventBus)
        engine.setOneTimeComponent(uiMap)

        val cameraInputProcessor = CameraInputProcessor(uiMap, engine)
        cameraInputProcessor.setZoomFactor(0.7)
        addComponent(cameraInputProcessor)


        val objectPlacementInputProcessor = ObjectPlacementInputProcessor(
            this, uiMap, engine, eventBus
        )
        addComponent(objectPlacementInputProcessor)
        addComponent(ResizeDebugComponent(this))

        val uiPlacement = uiPlacement(engine, eventBus).apply {
            onStageResized(true) { width: Int, height: Int ->
                alignRightToRightOfWindow()
                alignBottomToBottomOfWindow()
            }

            onButtonClick {
                println("Button clicked: $it")
                when (it) {
                    UIPlacementButton.VIEW_ROCK_COUNTERS -> {
                        uiMap.viewRockCounters()
                    }
                }
            }
        }

        uiActiveTextNotifier(engine, eventBus).run {
            centerXOnStage()
        }

        val pathText = UIPathText().addTo(this@sceneInit) {
            onStageResized(true) { width: Int, height: Int ->
                alignBottomToBottomOfWindow()
            }

            eventBus.register<UpdatedPathLengthEvent> {
                updatePathLength(it.newPathLength)
            }

            updatePathLength(gameMapControllerComponent.shortestPath?.pathLength)
        }


        addComponent(KeyInputProcessor(this, engine))
        val monstersComponent = MonstersEComponent(uiMap, engine, eventBus, uiMap._gridSize)
        addComponent(monstersComponent)

        val goalComponent = GoalEComponent(engine, eventBus)
        engine.setOneTimeComponent(goalComponent)
        //        goalComponent.calculateGoalForMap()

        val MOVE_MAP_DELTA = 7

        addFixedUpdater(TimeSpan(10.0)) {
            val keys = views.keys
            if (keys[Key.UP]) uiMap.y = uiMap.y + MOVE_MAP_DELTA
            if (keys[Key.DOWN]) uiMap.y = uiMap.y - MOVE_MAP_DELTA
            if (keys[Key.LEFT]) uiMap.x = uiMap.x + MOVE_MAP_DELTA
            if (keys[Key.RIGHT]) uiMap.x = uiMap.x - MOVE_MAP_DELTA
        }

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
