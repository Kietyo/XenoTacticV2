package com.xenotactic.korge.scenes

import korlibs.time.TimeSpan
import korlibs.event.Key
import korlibs.korge.input.keys
import korlibs.korge.scene.Scene
import korlibs.korge.view.*
import com.xenotactic.gamelogic.utils.GameSimulator
import com.xenotactic.gamelogic.random.MapGeneratorConfiguration
import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.random.RandomMapGenerator
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.listeners_component.PreSelectionComponentListener
import com.xenotactic.korge.listeners_component.SelectionComponentListener
import com.xenotactic.korge.listeners_component.UIMapEntityComponentListener
import com.xenotactic.korge.listeners_component.UIMapEntityTextComponentListener
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.korge.listeners_event.TowerUpgradeEventListeners
import com.xenotactic.korge.listeners_event.UIMapEventListeners
import com.xenotactic.korge.listeners_family.SetInitialPositionForUIEntityFamilyListener
import com.xenotactic.korge.input_processors.EditorPlacementInputProcessor
import com.xenotactic.korge.input_processors.MouseDragInputProcessor
import com.xenotactic.korge.input_processors.SelectorMouseProcessorV2
import com.xenotactic.gamelogic.model.GameWorld
import com.xenotactic.korge.input_processors.CameraInputProcessor
import com.xenotactic.korge.state.MouseDragSettingsState
import com.xenotactic.korge.random.MapGeneratorConfigurationV2
import com.xenotactic.korge.random.RandomMapGeneratorV2
import com.xenotactic.korge.random.generators.*
import com.xenotactic.korge.state.*
import com.xenotactic.korge.systems.*
import com.xenotactic.korge.ui.UIDebugInfo
import com.xenotactic.korge.ui.UIGuiContainer
import com.xenotactic.korge.ui.UIMapV2
import com.xenotactic.korge.ui.UINotificationText
import korlibs.korge.view.align.centerOnStage
import korlibs.korge.view.align.centerXOnStage
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class PlayScene : Scene() {
    @OptIn(ExperimentalTime::class)
    override suspend fun SContainer.sceneInit() {
        val eventBus = EventBus(this@PlayScene)

//        val seed = 1337L
//        val seed = 1338L
//        val seed = 1349L
//        val seed = 1350L
        val seed = 1351L

        val width = 30.toGameUnit()
        val height = 20.toGameUnit()

        val randomMap = RandomMapGenerator.generate(
            MapGeneratorConfiguration(
                seed,
                width, height, 2, 10, 2, 5
            )
        )

        val randomMap2 = RandomMapGeneratorV2.generate(
            MapGeneratorConfigurationV2(
                seed,
                listOf(
                    StartGenerator,
                    FinishGenerator,
                    CheckpointsGenerator(2),
                    RocksGenerator(10),
                    TeleportsGenerator(2),
                    SpeedAreaGenerator(5)
                ),
                width, height
            )
        )

        println(randomMap)


        val gameWorld = GameWorld()
        val mouseDragSettingsState = MouseDragSettingsState()
        val engine = Engine(eventBus, gameWorld).apply {
            stateInjections.setSingletonOrThrow(mouseDragSettingsState)
            stateInjections.setSingletonOrThrow(DeadUIZonesState())

        }

        val gameSimulator = GameSimulator(width, height, engine)
        val uiMapV2 = UIMapV2(engine).addTo(this)
        engine.injections.setSingletonOrThrow(uiMapV2)

        uiMapV2.centerOnStage()

        val cameraInputProcessor = CameraInputProcessor(uiMapV2, engine)
        cameraInputProcessor.setup(this)

        val mouseDragInputProcessor =
            MouseDragInputProcessor(views, uiMapV2, mouseDragSettingsState.mouseDragStateSettings)
        mouseDragInputProcessor.setup(this)
        engine.injections.setSingletonOrThrow(mouseDragInputProcessor)

        val selectorMouseProcessorV2 = SelectorMouseProcessorV2(views, this@sceneInit, engine).apply {
            engine.injections.setSingletonOrThrow(this)
        }
        selectorMouseProcessorV2.setup(this)

        val editorState = EditorState(engine)
        engine.stateInjections.setSingletonOrThrow(editorState)

        engine.eventListeners.apply {
            add(UIMapEventListeners(engine))
            add(TowerUpgradeEventListeners(engine))
        }

        gameWorld.world.apply {
            injections = engine.injections
            addFamilyListener(SetInitialPositionForUIEntityFamilyListener(this))
            addComponentListener(PreSelectionComponentListener(engine))
            addComponentListener(SelectionComponentListener(engine))
            addComponentListener(UIMapEntityComponentListener())
            addComponentListener(UIMapEntityTextComponentListener(engine))

            addSystem(UIMonsterUpdatePositionSystem(this))
            addSystem(UIEightDirectionalMonsterSpriteDirectionSystem(this))
            addSystem(UIEightDirectionalMonsterAnimationSystem(this))
            addSystem(UITargetingRenderSystem(engine))
            addSystem(UITowerGunRotatingSystem(engine))

            addSystem(UIProjectileRenderSystem(engine))

            addSystem(UIMonsterHealthRenderSystem(this))
        }

//        gameMapApi.placeEntities(randomMap.map.getAllEntities())
        gameSimulator.gameMapApi.placeEntities(randomMap2.gameWorld)
//        gameMapApi.placeEntities(
//            MapEntity.Start(22, 0),
//            MapEntity.Finish(3, 2),
//            MapEntity.ROCK_2X4.at(22, 6),
//            MapEntity.ROCK_4X2.at(10, 3),
////            MapEntity.Tower(20, 0)
//        )

        val uiDebugInfo = UIDebugInfo(engine)
        engine.injections.setSingletonOrThrow(uiDebugInfo)

        UIGuiContainer(this, engine, gameWorld, gameSimulator.gameMapApi)

        keys {
            this.down(Key.ESCAPE) {
                editorState.switchToPlayingMode()
            }
        }

        val notificationText = UINotificationText(engine, "N/A").addTo(this).apply {
//            centerXOnStage()
//            dockedTo(Anchor.CENTER)
            centerXOnStage()
        }

        val editorPlacementInputProcessor = EditorPlacementInputProcessor(
            views, this, engine
        )
        editorPlacementInputProcessor.setup(this)


        val deltaTime = TimeSpan(gameSimulator.millisPerTick.inWholeMilliseconds.toDouble())
        var accumulatedTime = TimeSpan.ZERO
        val updateInfoTextFrequency = TimeSpan(250.0)
        addFixedUpdater(deltaTime) {
            val updateTime = measureTime {
                gameSimulator.tick()
            }
            accumulatedTime += deltaTime
            if (accumulatedTime >= updateInfoTextFrequency) {
                uiDebugInfo.infoText.text = "Update time: $updateTime"
                accumulatedTime = TimeSpan.ZERO
            }
        }

    }
}

