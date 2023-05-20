package com.xenotactic.korge.scenes

import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.utils.GlobalResources
import com.xenotactic.korge.events.PlayMapEvent
import com.xenotactic.korge.utils.MapBridge
import korlibs.korge.scene.Scene
import korlibs.korge.view.SContainer
import korlibs.korge.view.Views
import korlibs.korge.view.align.centerOnStage
import korlibs.korge.view.text
import korlibs.logger.Logger

lateinit var VIEWS_INSTANCE: Views

class RootScene(
    override var views: Views, val globalEventBus: EventBus,
    val mapBridge: MapBridge
) : Scene() {
    override suspend fun SContainer.sceneInit() {
        VIEWS_INSTANCE = views

        this.text("Loading...", textSize = 50f).centerOnStage()

        GlobalResources.init()

        //        val test = resourcesVfs["test.txt"]
        //
        //        logger.info {
        //            """
        //                test: $test
        //                test.path: ${test.path}
        //                test.absolutePath: ${test.absolutePath}
        //            """.trimIndent()
        //        }
        //
        //        test.writeString("test string")

        globalEventBus.register<PlayMapEvent> {
            logger.info {
                "Setting up game scene..."
            }
            mapBridge.gameMap = it.gameMap
            sceneContainer.pushTo<GameScene>()
        }

        //        sceneContainer.changeTo<TestScene>()
        //        sceneContainer.changeTo<EditorSceneV2>()
        sceneContainer.changeTo<PlayScene>()
        //        sceneContainer.changeTo<MapViewerScene>()
    }

    override suspend fun sceneAfterDestroy() {
        super.sceneAfterDestroy()
        logger.info {
            "sceneAfterDestroy called"
        }
    }

    override suspend fun sceneDestroy() {
        super.sceneDestroy()
        logger.info {
            "sceneDestroy called"
        }
    }

    companion object {
        val logger = Logger<RootScene>()
    }
}
