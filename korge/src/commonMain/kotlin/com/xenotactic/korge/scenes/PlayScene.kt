package com.xenotactic.korge.scenes

import com.soywiz.klock.TimeSpan
import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.random.MapGeneratorConfiguration
import com.xenotactic.korge.events.EventBus
import com.xenotactic.gamelogic.random.RandomMapGenerator
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.component_listeners.PreSelectionComponentListener
import com.xenotactic.korge.component_listeners.SelectionComponentListener
import com.xenotactic.korge.component_listeners.UIMapEntityComponentListener
import com.xenotactic.korge.component_listeners.UIMapEntityTextComponentListener
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.event_listeners.TowerUpgradeEventListeners
import com.xenotactic.korge.event_listeners.UIMapEventListeners
import com.xenotactic.korge.events.UpdatedPathLineEvent
import com.xenotactic.korge.family_listeners.SetInitialPositionFamilyListener
import com.xenotactic.korge.input_processors.EditorPlacementInputProcessor
import com.xenotactic.korge.input_processors.MouseDragInputProcessor
import com.xenotactic.korge.input_processors.SelectorMouseProcessorV2
import com.xenotactic.korge.models.GameWorld
import com.xenotactic.korge.models.SettingsState
import com.xenotactic.korge.random.MapGeneratorConfigurationV2
import com.xenotactic.korge.random.RandomMapGeneratorV2
import com.xenotactic.korge.random.generators.*
import com.xenotactic.korge.state.*
import com.xenotactic.korge.systems.*
import com.xenotactic.korge.ui.UIGuiContainer
import com.xenotactic.korge.ui.UIMapV2
import com.xenotactic.korge.ui.UINotificationText
import kotlin.time.Duration.Companion.milliseconds
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


        val world = World()
        val gameWorld = GameWorld(world)
        val settingsState = SettingsState()
        val engine = Engine(eventBus, gameWorld).apply {
            stateInjections.setSingletonOrThrow(GameMapDimensionsState(this, width, height))
            stateInjections.setSingletonOrThrow(settingsState)
            stateInjections.setSingletonOrThrow(GameMapPathState(this))
            stateInjections.setSingletonOrThrow(GameplayState(61, 0.04, 7))
            stateInjections.setSingletonOrThrow(DeadUIZonesState())
        }
        val uiMapV2 = UIMapV2(engine).addTo(this)
        engine.injections.setSingletonOrThrow(uiMapV2)
        val gameMapApi = GameMapApi(engine)
        engine.injections.setSingletonOrThrow(gameMapApi)
        uiMapV2.centerOnStage()

        val mouseDragInputProcessor =
            MouseDragInputProcessor(uiMapV2, settingsState.mouseDragStateSettings)
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

        world.apply {
            injections = engine.injections
            addFamilyListener(SetInitialPositionFamilyListener(this))
            addComponentListener(PreSelectionComponentListener(engine))
            addComponentListener(SelectionComponentListener(engine))
            addComponentListener(UIMapEntityComponentListener())
            addComponentListener(UIMapEntityTextComponentListener(engine))
            addSystem(MonsterMoveSystem(this))
            addSystem(MonsterRemoveSystem(this))
            addSystem(MonsterComputeSpeedEffectSystem(engine))
            addSystem(EightDirectionalMonsterSpriteDirectionSystem(this))
            addSystem(EightDirectionalMonsterAnimationSystem(this))
            addSystem(ProjectileRemoveSystem(this))
            addSystem(TowerTargetingRemoveSystem(gameWorld.world))
            addSystem(TargetingAddSystem(gameWorld))
            addSystem(TargetingRenderSystem(engine))
            addSystem(TowerGunRotatingSystem(engine))
            addSystem(ProjectileMoveSystem(world))
            addSystem(ProjectileCollideSystem(world))
            addSystem(ProjectileRenderSystem(engine))
            addSystem(MonsterDeathSystem(engine))
            addSystem(MonsterHealthRenderSystem(world))
            addSystem(ReloadSystem(engine))
            addSystem(TowerAttackSystem(world, gameMapApi))
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

        UIGuiContainer(this, engine, world, gameWorld, gameMapApi)




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
                world.update(deltaTime.milliseconds.milliseconds)
            }
            accumulatedTime += deltaTime
            if (accumulatedTime >= updateInfoTextFrequency) {
                infoText.text = "Update time: $updateTime"
                accumulatedTime = TimeSpan.ZERO
            }
        }

    }
}

