package com.xenotactic.korge.scenes

import com.soywiz.klock.TimeSpan
import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.xenotactic.gamelogic.api.GameMapApi
import com.xenotactic.gamelogic.api.GameSimulator
import com.xenotactic.gamelogic.random.MapGeneratorConfiguration
import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.random.RandomMapGenerator
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.component_listeners.PreSelectionComponentListener
import com.xenotactic.korge.component_listeners.SelectionComponentListener
import com.xenotactic.korge.component_listeners.UIMapEntityComponentListener
import com.xenotactic.korge.component_listeners.UIMapEntityTextComponentListener
import com.xenotactic.gamelogic.engine.Engine
import com.xenotactic.korge.event_listeners.TowerUpgradeEventListeners
import com.xenotactic.korge.event_listeners.UIMapEventListeners
import com.xenotactic.gamelogic.events.UpdatedPathLineEvent
import com.xenotactic.korge.family_listeners.SetInitialPositionFamilyListener
import com.xenotactic.korge.input_processors.EditorPlacementInputProcessor
import com.xenotactic.korge.input_processors.MouseDragInputProcessor
import com.xenotactic.korge.input_processors.SelectorMouseProcessorV2
import com.xenotactic.gamelogic.model.GameWorld
import com.xenotactic.korge.models.MouseDragSettingsState
import com.xenotactic.korge.random.MapGeneratorConfigurationV2
import com.xenotactic.korge.random.RandomMapGeneratorV2
import com.xenotactic.korge.random.generators.*
import com.xenotactic.korge.state.*
import com.xenotactic.korge.systems.*
import com.xenotactic.korge.ui.UIGuiContainer
import com.xenotactic.korge.ui.UIMapV2
import com.xenotactic.korge.ui.UINotificationText
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
        val gameSimulator = GameSimulator(width, height, engine, gameWorld)

        val uiMapV2 = UIMapV2(engine).addTo(this)
        engine.injections.setSingletonOrThrow(uiMapV2)
        val gameMapApi = GameMapApi(engine)
        engine.injections.setSingletonOrThrow(gameMapApi)
        uiMapV2.centerOnStage()

        val mouseDragInputProcessor =
            MouseDragInputProcessor(uiMapV2, mouseDragSettingsState.mouseDragStateSettings)
        addComponent(mouseDragInputProcessor)
        engine.injections.setSingletonOrThrow(mouseDragInputProcessor)
        addComponent(SelectorMouseProcessorV2(this@sceneInit, engine).apply {
            engine.injections.setSingletonOrThrow(this)
        })
        val editorState = EditorState(engine)
        engine.stateInjections.setSingletonOrThrow(editorState)

        engine.eventListeners.apply {
            add(UIMapEventListeners(engine))
            add(TowerUpgradeEventListeners(engine))
        }

        gameWorld.world.apply {
            injections = engine.injections
            addFamilyListener(SetInitialPositionFamilyListener(this))
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
            addSystem(MonsterDeathSystem(engine))
            addSystem(MonsterHealthRenderSystem(this))
            addSystem(ReloadSystem(engine))
            addSystem(TowerAttackSystem(this, gameMapApi))
        }

        val infoText = text("Hello world")
        val pathLengthText = text("Hello world") {
            alignTopToBottomOf(infoText)
            eventBus.register<UpdatedPathLineEvent> {
                text = "Path length: ${it.newPathLength?.toInt()}"
            }
        }

//        gameMapApi.placeEntities(randomMap.map.getAllEntities())
        gameMapApi.placeEntitiesV2(randomMap2.gameWorld)
//        gameMapApi.placeEntities(
//            MapEntity.Start(22, 0),
//            MapEntity.Finish(3, 2),
//            MapEntity.ROCK_2X4.at(22, 6),
//            MapEntity.ROCK_4X2.at(10, 3),
////            MapEntity.Tower(20, 0)
//        )

        UIGuiContainer(this, engine, gameWorld, gameMapApi)




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



        addComponent(
            EditorPlacementInputProcessor(
                this, engine
            )
        )

        val deltaTime = TimeSpan(1000.0 / 60)
        var accumulatedTime = TimeSpan.ZERO
        val updateInfoTextFrequency = TimeSpan(250.0)
        addFixedUpdater(deltaTime) {
            val updateTime = measureTime {
                gameSimulator.update(deltaTime)
            }
            accumulatedTime += deltaTime
            if (accumulatedTime >= updateInfoTextFrequency) {
                infoText.text = "Update time: $updateTime"
                accumulatedTime = TimeSpan.ZERO
            }
        }

    }
}

