package com.xenotactic.korge.scenes

import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.SContainer
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.centerOnStage
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.MapEntityComponent
import com.xenotactic.gamelogic.components.PathSequenceTraversalComponent
import com.xenotactic.gamelogic.components.SizeComponent
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.MapEntityData
import com.xenotactic.gamelogic.random.MapGeneratorConfiguration
import com.xenotactic.korge.events.EventBus
import com.xenotactic.gamelogic.random.RandomMapGenerator
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.component_listeners.PreSelectionComponentListener
import com.xenotactic.korge.component_listeners.SelectionComponentListener
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.family_listeners.AddEntityFamilyListener
import com.xenotactic.korge.family_listeners.SetInitialPositionFamilyListener
import com.xenotactic.korge.input_processors.MouseDragInputProcessor
import com.xenotactic.korge.korge_utils.alignBottomToBottomOfWindow
import com.xenotactic.korge.models.GameWorld
import com.xenotactic.korge.models.SettingsContainer
import com.xenotactic.korge.state.GameMapApi
import com.xenotactic.korge.state.GameMapDimensionsState
import com.xenotactic.korge.state.GameMapPathState
import com.xenotactic.korge.ui.UIMapV2
import pathing.PathSequenceTraversal

class PlayScene : Scene() {
    override suspend fun SContainer.sceneInit() {
        val eventBus = EventBus(this@PlayScene)

        val seed = 1337L

        val randomMap = RandomMapGenerator.generate(
            MapGeneratorConfiguration(
                seed,
                30.toGameUnit(), 20.toGameUnit(), 2, 10, 2
            )
        )

        println(randomMap)

        val gameWorld = World()
        val settingsContainer = SettingsContainer()
        val engine = Engine(eventBus, GameWorld(gameWorld)).apply {
            injections.setSingletonOrThrow(GameMapDimensionsState(this, 30.toGameUnit(), 20.toGameUnit()))
            injections.setSingletonOrThrow(settingsContainer)
            injections.setSingletonOrThrow(GameMapPathState(this))
        }
        val uiMapV2 = UIMapV2(engine).addTo(this)
        engine.injections.setSingletonOrThrow(uiMapV2)
        val gameMapApi = GameMapApi(engine, eventBus)
        engine.injections.setSingletonOrThrow(gameMapApi)
        uiMapV2.centerOnStage()

        val mouseDragInputProcessor =
            MouseDragInputProcessor(uiMapV2, settingsContainer.mouseDragStateSettings)
        addComponent(mouseDragInputProcessor)

        engine.apply {
            injections.setSingletonOrThrow(mouseDragInputProcessor)
        }


        gameWorld.apply {
            injections = engine.injections
            addFamilyListener(AddEntityFamilyListener(this))
            addFamilyListener(SetInitialPositionFamilyListener(this))
            addComponentListener(PreSelectionComponentListener(engine))
            addComponentListener(SelectionComponentListener(engine))
        }

        gameMapApi.placeEntities(randomMap.map.getAllEntities())


        uiButton("Spawn creep") {
            alignBottomToBottomOfWindow()
            onClick {
                println("Spawn creep button clicked!")
                gameWorld.addEntity {
                    addComponentOrThrow(
                        MapEntityComponent(
                            MapEntityData.Monster
                        )
                    )
                    addComponentOrThrow(SizeComponent(1.toGameUnit(), 1.toGameUnit()))
                    val pathSequenceTraversal = PathSequenceTraversal(
                        gameMapApi.gameMapPathState.shortestPath!!
                    )
                    addComponentOrThrow(
                        PathSequenceTraversalComponent(
                            pathSequenceTraversal
                        )
                    )
                }
            }
        }

        /**
         * Failure(map=GameMap(
        start=Start(x=23, y=17),
        finish=Finish(x=21, y=1),
        checkpoints=[Checkpoint(sequenceNumber=0, x=3, y=7), Checkpoint(sequenceNumber=1, x=8, y=14)],
        teleportIns={},
        teleportOuts={},
        towers=[],
        rocks=[],
        smallBlockers=[]
        ), errors=[Failed to place TELEPORT OUT 0. Attempted points: [IntPoint(x=22, y=2), IntPoint(x=16, y=14), IntPoint(x=11, y=4), IntPoint(x=19, y=4), IntPoint(x=22, y=9), IntPoint(x=1, y=16), IntPoint(x=15, y=8), IntPoint(x=22, y=15), IntPoint(x=0, y=11), IntPoint(x=16, y=8), IntPoint(x=21, y=13), IntPoint(x=28, y=5), IntPoint(x=16, y=3), IntPoint(x=28, y=18), IntPoint(x=2, y=17), IntPoint(x=3, y=9), IntPoint(x=9, y=1), IntPoint(x=4, y=9), IntPoint(x=8, y=8), IntPoint(x=4, y=6), IntPoint(x=11, y=12), IntPoint(x=21, y=15), IntPoint(x=17, y=15), IntPoint(x=22, y=4), IntPoint(x=26, y=9), IntPoint(x=0, y=17), IntPoint(x=5, y=11), IntPoint(x=26, y=1), IntPoint(x=11, y=6), IntPoint(x=2, y=5), IntPoint(x=12, y=11), IntPoint(x=9, y=16), IntPoint(x=10, y=15), IntPoint(x=6, y=4), IntPoint(x=20, y=4), IntPoint(x=25, y=13), IntPoint(x=25, y=17), IntPoint(x=15, y=3), IntPoint(x=16, y=5), IntPoint(x=3, y=10), IntPoint(x=4, y=5), IntPoint(x=28, y=13), IntPoint(x=18, y=4), IntPoint(x=28, y=11), IntPoint(x=4, y=1), IntPoint(x=9, y=4), IntPoint(x=6, y=3), IntPoint(x=25, y=4), IntPoint(x=9, y=14), IntPoint(x=8, y=1), IntPoint(x=14, y=14), IntPoint(x=14, y=9), IntPoint(x=10, y=1), IntPoint(x=5, y=2), IntPoint(x=13, y=12), IntPoint(x=27, y=12), IntPoint(x=17, y=9), IntPoint(x=7, y=1), IntPoint(x=27, y=13), IntPoint(x=27, y=15), IntPoint(x=2, y=8), IntPoint(x=25, y=10), IntPoint(x=0, y=9), IntPoint(x=4, y=12), IntPoint(x=1, y=0), IntPoint(x=16, y=15), IntPoint(x=19, y=9), IntPoint(x=20, y=18), IntPoint(x=26, y=5), IntPoint(x=22, y=12), IntPoint(x=5, y=16), IntPoint(x=26, y=12), IntPoint(x=13, y=3), IntPoint(x=10, y=10), IntPoint(x=26, y=3), IntPoint(x=6, y=13), IntPoint(x=7, y=14), IntPoint(x=17, y=12), IntPoint(x=2, y=13), IntPoint(x=23, y=11), IntPoint(x=16, y=11), IntPoint(x=13, y=10), IntPoint(x=17, y=13), IntPoint(x=23, y=0), IntPoint(x=15, y=15), IntPoint(x=28, y=10), IntPoint(x=28, y=2), IntPoint(x=18, y=15), IntPoint(x=19, y=12), IntPoint(x=14, y=5), IntPoint(x=16, y=12), IntPoint(x=3, y=6), IntPoint(x=14, y=16), IntPoint(x=18, y=17), IntPoint(x=13, y=17), IntPoint(x=22, y=7), IntPoint(x=6, y=9), IntPoint(x=14, y=8), IntPoint(x=0, y=8), IntPoint(x=26, y=6), IntPoint(x=5, y=9), IntPoint(x=0, y=1), IntPoint(x=10, y=12), IntPoint(x=26, y=17), IntPoint(x=8, y=5), IntPoint(x=15, y=12), IntPoint(x=8, y=15), IntPoint(x=1, y=17), IntPoint(x=12, y=10), IntPoint(x=7, y=16), IntPoint(x=3, y=0), IntPoint(x=19, y=6), IntPoint(x=15, y=6), IntPoint(x=20, y=2), IntPoint(x=19, y=10), IntPoint(x=21, y=14), IntPoint(x=7, y=4), IntPoint(x=24, y=6), IntPoint(x=28, y=12), IntPoint(x=6, y=5), IntPoint(x=5, y=3), IntPoint(x=24, y=11), IntPoint(x=3, y=8), IntPoint(x=3, y=13), IntPoint(x=21, y=18), IntPoint(x=24, y=7), IntPoint(x=13, y=6), IntPoint(x=0, y=18), IntPoint(x=7, y=17), IntPoint(x=2, y=7), IntPoint(x=10, y=17), IntPoint(x=24, y=5), IntPoint(x=17, y=0), IntPoint(x=20, y=11), IntPoint(x=5, y=14), IntPoint(x=1, y=9), IntPoint(x=12, y=15), IntPoint(x=11, y=13), IntPoint(x=10, y=11), IntPoint(x=10, y=4), IntPoint(x=23, y=12), IntPoint(x=21, y=17), IntPoint(x=0, y=5), IntPoint(x=21, y=7), IntPoint(x=12, y=7), IntPoint(x=8, y=4), IntPoint(x=3, y=18), IntPoint(x=4, y=18), IntPoint(x=4, y=14), IntPoint(x=14, y=10), IntPoint(x=9, y=11), IntPoint(x=6, y=1), IntPoint(x=7, y=12), IntPoint(x=28, y=6), IntPoint(x=2, y=6), IntPoint(x=24, y=8), IntPoint(x=2, y=1), IntPoint(x=21, y=6), IntPoint(x=10, y=7), IntPoint(x=7, y=13), IntPoint(x=5, y=18), IntPoint(x=5, y=6), IntPoint(x=23, y=1), IntPoint(x=7, y=18), IntPoint(x=10, y=8), IntPoint(x=2, y=14), IntPoint(x=18, y=3), IntPoint(x=20, y=12), IntPoint(x=15, y=17), IntPoint(x=10, y=16), IntPoint(x=2, y=4), IntPoint(x=16, y=7), IntPoint(x=5, y=5), IntPoint(x=5, y=10), IntPoint(x=19, y=15), IntPoint(x=22, y=13), IntPoint(x=27, y=0), IntPoint(x=23, y=17), IntPoint(x=11, y=3), IntPoint(x=25, y=0), IntPoint(x=25, y=3), IntPoint(x=25, y=8), IntPoint(x=24, y=3), IntPoint(x=1, y=15), IntPoint(x=28, y=17), IntPoint(x=18, y=12), IntPoint(x=2, y=9), IntPoint(x=23, y=3), IntPoint(x=18, y=2), IntPoint(x=14, y=11), IntPoint(x=5, y=4), IntPoint(x=9, y=12), IntPoint(x=14, y=18), IntPoint(x=4, y=13), IntPoint(x=4, y=0), IntPoint(x=25, y=5), IntPoint(x=10, y=13), IntPoint(x=11, y=14), IntPoint(x=19, y=3), IntPoint(x=1, y=3), IntPoint(x=19, y=0), IntPoint(x=17, y=18), IntPoint(x=13, y=14), IntPoint(x=8, y=17), IntPoint(x=11, y=7), IntPoint(x=24, y=0), IntPoint(x=14, y=12), IntPoint(x=23, y=8), IntPoint(x=26, y=2), IntPoint(x=27, y=4), IntPoint(x=4, y=17), IntPoint(x=5, y=8), IntPoint(x=10, y=14), IntPoint(x=16, y=10), IntPoint(x=27, y=11), IntPoint(x=23, y=18), IntPoint(x=17, y=11), IntPoint(x=24, y=14), IntPoint(x=23, y=7), IntPoint(x=12, y=17), IntPoint(x=0, y=2), IntPoint(x=27, y=8), IntPoint(x=21, y=2), IntPoint(x=24, y=4), IntPoint(x=7, y=9), IntPoint(x=15, y=7), IntPoint(x=14, y=15), IntPoint(x=1, y=5), IntPoint(x=25, y=7), IntPoint(x=21, y=1), IntPoint(x=13, y=2), IntPoint(x=0, y=6), IntPoint(x=18, y=0), IntPoint(x=13, y=15), IntPoint(x=9, y=9), IntPoint(x=20, y=5), IntPoint(x=12, y=6), IntPoint(x=6, y=16), IntPoint(x=27, y=7), IntPoint(x=4, y=4), IntPoint(x=27, y=2), IntPoint(x=24, y=16), IntPoint(x=20, y=9), IntPoint(x=28, y=14), IntPoint(x=7, y=5), IntPoint(x=17, y=2), IntPoint(x=17, y=5), IntPoint(x=5, y=1), IntPoint(x=23, y=5), IntPoint(x=13, y=9), IntPoint(x=11, y=10), IntPoint(x=20, y=6), IntPoint(x=16, y=9), IntPoint(x=22, y=11), IntPoint(x=25, y=12), IntPoint(x=3, y=15), IntPoint(x=13, y=7), IntPoint(x=7, y=3), IntPoint(x=0, y=4), IntPoint(x=12, y=4), IntPoint(x=15, y=1), IntPoint(x=0, y=14), IntPoint(x=27, y=16), IntPoint(x=14, y=3), IntPoint(x=9, y=18), IntPoint(x=25, y=11), IntPoint(x=19, y=2), IntPoint(x=12, y=13), IntPoint(x=1, y=1), IntPoint(x=13, y=18), IntPoint(x=14, y=6), IntPoint(x=15, y=5), IntPoint(x=20, y=15), IntPoint(x=13, y=0), IntPoint(x=18, y=9), IntPoint(x=14, y=7), IntPoint(x=5, y=13), IntPoint(x=1, y=6), IntPoint(x=19, y=14), IntPoint(x=9, y=10), IntPoint(x=12, y=3), IntPoint(x=2, y=18), IntPoint(x=9, y=7), IntPoint(x=11, y=11), IntPoint(x=4, y=8), IntPoint(x=18, y=10), IntPoint(x=8, y=11), IntPoint(x=22, y=14), IntPoint(x=24, y=15), IntPoint(x=20, y=16), IntPoint(x=21, y=16), IntPoint(x=13, y=5), IntPoint(x=8, y=9), IntPoint(x=17, y=17), IntPoint(x=11, y=0), IntPoint(x=26, y=11), IntPoint(x=22, y=17), IntPoint(x=26, y=15), IntPoint(x=7, y=11), IntPoint(x=8, y=12), IntPoint(x=11, y=17), IntPoint(x=22, y=10), IntPoint(x=27, y=9), IntPoint(x=1, y=14), IntPoint(x=18, y=16), IntPoint(x=3, y=12), IntPoint(x=5, y=7), IntPoint(x=27, y=18), IntPoint(x=16, y=2), IntPoint(x=4, y=16), IntPoint(x=17, y=16), IntPoint(x=2, y=10), IntPoint(x=6, y=10), IntPoint(x=24, y=9), IntPoint(x=6, y=18), IntPoint(x=22, y=0), IntPoint(x=15, y=2), IntPoint(x=17, y=4), IntPoint(x=15, y=18), IntPoint(x=1, y=8), IntPoint(x=10, y=2), IntPoint(x=17, y=6), IntPoint(x=16, y=1), IntPoint(x=22, y=18), IntPoint(x=27, y=6), IntPoint(x=26, y=10), IntPoint(x=12, y=18), IntPoint(x=12, y=5), IntPoint(x=7, y=0), IntPoint(x=10, y=5), IntPoint(x=4, y=15), IntPoint(x=20, y=13), IntPoint(x=23, y=6), IntPoint(x=1, y=10), IntPoint(x=17, y=7), IntPoint(x=6, y=0), IntPoint(x=25, y=14), IntPoint(x=3, y=7), IntPoint(x=21, y=4), IntPoint(x=26, y=0), IntPoint(x=8, y=10), IntPoint(x=15, y=11), IntPoint(x=1, y=7), IntPoint(x=14, y=1), IntPoint(x=11, y=18), IntPoint(x=20, y=0), IntPoint(x=0, y=0), IntPoint(x=15, y=14), IntPoint(x=19, y=18), IntPoint(x=28, y=7), IntPoint(x=3, y=1), IntPoint(x=27, y=14), IntPoint(x=2, y=11), IntPoint(x=18, y=11), IntPoint(x=25, y=15), IntPoint(x=9, y=15), IntPoint(x=13, y=4), IntPoint(x=9, y=3), IntPoint(x=18, y=14), IntPoint(x=24, y=12), IntPoint(x=11, y=1), IntPoint(x=26, y=8), IntPoint(x=7, y=7), IntPoint(x=19, y=1), IntPoint(x=4, y=11), IntPoint(x=12, y=12), IntPoint(x=19, y=5), IntPoint(x=8, y=14), IntPoint(x=7, y=6), IntPoint(x=22, y=3), IntPoint(x=3, y=17), IntPoint(x=23, y=4), IntPoint(x=27, y=10), IntPoint(x=20, y=1), IntPoint(x=6, y=2), IntPoint(x=6, y=11), IntPoint(x=17, y=3), IntPoint(x=22, y=1), IntPoint(x=14, y=13), IntPoint(x=13, y=16), IntPoint(x=12, y=2), IntPoint(x=21, y=3), IntPoint(x=0, y=13), IntPoint(x=22, y=6), IntPoint(x=20, y=10), IntPoint(x=4, y=7), IntPoint(x=9, y=5), IntPoint(x=28, y=4), IntPoint(x=16, y=18), IntPoint(x=4, y=2), IntPoint(x=1, y=4), IntPoint(x=24, y=2), IntPoint(x=20, y=3), IntPoint(x=2, y=12), IntPoint(x=25, y=6), IntPoint(x=17, y=8), IntPoint(x=5, y=15), IntPoint(x=24, y=17), IntPoint(x=12, y=14), IntPoint(x=3, y=14), IntPoint(x=23, y=13), IntPoint(x=8, y=6), IntPoint(x=28, y=8), IntPoint(x=24, y=10), IntPoint(x=8, y=18), IntPoint(x=1, y=18), IntPoint(x=15, y=0), IntPoint(x=0, y=16), IntPoint(x=10, y=6), IntPoint(x=23, y=2), IntPoint(x=0, y=7), IntPoint(x=13, y=8), IntPoint(x=17, y=1), IntPoint(x=5, y=17), IntPoint(x=18, y=7), IntPoint(x=25, y=16), IntPoint(x=9, y=13), IntPoint(x=24, y=1), IntPoint(x=25, y=1), IntPoint(x=18, y=18), IntPoint(x=9, y=6), IntPoint(x=26, y=4), IntPoint(x=2, y=16), IntPoint(x=2, y=3), IntPoint(x=1, y=11), IntPoint(x=13, y=13), IntPoint(x=20, y=7), IntPoint(x=24, y=18), IntPoint(x=15, y=10), IntPoint(x=16, y=6), IntPoint(x=6, y=17), IntPoint(x=11, y=5), IntPoint(x=15, y=16), IntPoint(x=15, y=4), IntPoint(x=0, y=3), IntPoint(x=1, y=2), IntPoint(x=28, y=15), IntPoint(x=19, y=8), IntPoint(x=3, y=4), IntPoint(x=21, y=5), IntPoint(x=4, y=10), IntPoint(x=20, y=14), IntPoint(x=16, y=16), IntPoint(x=3, y=2), IntPoint(x=21, y=0), IntPoint(x=4, y=3), IntPoint(x=3, y=5), IntPoint(x=8, y=16), IntPoint(x=18, y=5), IntPoint(x=2, y=15), IntPoint(x=23, y=16), IntPoint(x=26, y=14), IntPoint(x=8, y=2), IntPoint(x=14, y=4), IntPoint(x=20, y=8), IntPoint(x=5, y=12), IntPoint(x=21, y=11), IntPoint(x=27, y=1), IntPoint(x=23, y=10), IntPoint(x=3, y=11), IntPoint(x=3, y=3), IntPoint(x=7, y=8), IntPoint(x=24, y=13), IntPoint(x=16, y=13), IntPoint(x=8, y=3), IntPoint(x=5, y=0), IntPoint(x=21, y=10), IntPoint(x=26, y=13), IntPoint(x=27, y=3), IntPoint(x=14, y=0), IntPoint(x=23, y=15), IntPoint(x=16, y=4), IntPoint(x=19, y=7), IntPoint(x=18, y=1), IntPoint(x=10, y=3), IntPoint(x=6, y=7), IntPoint(x=14, y=17), IntPoint(x=9, y=17), IntPoint(x=17, y=14), IntPoint(x=26, y=16), IntPoint(x=1, y=13), IntPoint(x=6, y=8), IntPoint(x=16, y=17), IntPoint(x=11, y=16), IntPoint(x=23, y=14), IntPoint(x=9, y=8), IntPoint(x=12, y=8), IntPoint(x=20, y=17), IntPoint(x=27, y=5), IntPoint(x=25, y=18), IntPoint(x=13, y=11), IntPoint(x=7, y=10), IntPoint(x=18, y=6), IntPoint(x=9, y=2), IntPoint(x=26, y=7), IntPoint(x=28, y=3), IntPoint(x=21, y=12), IntPoint(x=14, y=2), IntPoint(x=22, y=5), IntPoint(x=19, y=16), IntPoint(x=15, y=9), IntPoint(x=28, y=9), IntPoint(x=6, y=12), IntPoint(x=9, y=0), IntPoint(x=0, y=15), IntPoint(x=12, y=1), IntPoint(x=13, y=1), IntPoint(x=0, y=10), IntPoint(x=19, y=13), IntPoint(x=28, y=0), IntPoint(x=6, y=14), IntPoint(x=12, y=16), IntPoint(x=8, y=13), IntPoint(x=6, y=6), IntPoint(x=12, y=9), IntPoint(x=11, y=9), IntPoint(x=2, y=2), IntPoint(x=2, y=0), IntPoint(x=25, y=2), IntPoint(x=0, y=12), IntPoint(x=22, y=16), IntPoint(x=8, y=0), IntPoint(x=7, y=2), IntPoint(x=10, y=18), IntPoint(x=21, y=9), IntPoint(x=1, y=12), IntPoint(x=10, y=0), IntPoint(x=11, y=15), IntPoint(x=17, y=10), IntPoint(x=22, y=8), IntPoint(x=10, y=9), IntPoint(x=28, y=16), IntPoint(x=25, y=9), IntPoint(x=23, y=9), IntPoint(x=19, y=17), IntPoint(x=18, y=8), IntPoint(x=12, y=0), IntPoint(x=18, y=13), IntPoint(x=11, y=8), IntPoint(x=28, y=1), IntPoint(x=11, y=2), IntPoint(x=8, y=7), IntPoint(x=7, y=15), IntPoint(x=19, y=11)]])
         */
    }
}