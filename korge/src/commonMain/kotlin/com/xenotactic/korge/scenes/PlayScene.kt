package com.xenotactic.korge.scenes

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.SContainer
import com.xenotactic.gamelogic.random.MapGeneratorConfiguration
import com.xenotactic.korge.events.EventBus
import com.xenotactic.gamelogic.random.RandomMapGenerator
import kotlin.random.Random

class PlayScene : Scene() {
    override suspend fun SContainer.sceneInit() {
        val eventBus = EventBus(this@PlayScene)

        val seed = 1337L

        val randomMap = RandomMapGenerator.generate(MapGeneratorConfiguration(
            seed,
            30, 20, 2, 10, 2
        ))

        println(randomMap)


//        val gameWorld = World()
//        val settingsContainer = SettingsContainer()
//        val engine = Engine(eventBus, GameWorld(gameWorld)).apply {
//            injections.setSingleton(GameMapDimensionsState(this, 10, 10))
//            injections.setSingleton(GameMapApi(this, eventBus))
//            injections.setSingleton(settingsContainer)
//        }
//        val uiMapV2 = UIMapV2(engine).addTo(this)
//        uiMapV2.centerOnStage()
//
//        val mouseDragInputProcessor = MouseDragInputProcessor(uiMapV2, settingsContainer.mouseDragStateSettings)
//        addComponent(mouseDragInputProcessor)
    }
}