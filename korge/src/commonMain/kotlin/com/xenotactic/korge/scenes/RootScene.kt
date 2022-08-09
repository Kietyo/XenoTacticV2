package com.xenotactic.korge.scenes

import com.soywiz.klogger.Logger
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.SContainer
import com.soywiz.korge.view.Views
import com.soywiz.korge.view.centerOnStage
import com.soywiz.korge.view.text
import com.soywiz.korio.file.std.resourcesVfs
import com.xenotactic.korge.bridges.MapBridge
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.events.PlayMapEvent

lateinit var VIEWS_INSTANCE: Views

class RootScene(
    override var views: Views, val globalEventBus: EventBus,
    val mapBridge: MapBridge
) : Scene() {
    override suspend fun SContainer.sceneInit() {
        VIEWS_INSTANCE = views

        this.text("Loading...", textSize = 50.0).centerOnStage()

        val test = resourcesVfs["test.txt"]

        logger.info {
            """
                test: $test
                test.path: ${test.path}
                test.absolutePath: ${test.absolutePath}
            """.trimIndent()
        }

        test.writeString("test string")

        globalEventBus.register<PlayMapEvent> {
            logger.info {
                "Setting up game scene..."
            }
            mapBridge.gameMap = it.gameMap
            sceneContainer.pushTo<GameScene>()
        }

        sceneContainer.changeTo<EditorScene>()
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
