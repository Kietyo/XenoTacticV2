package scenes

import bridges.MapBridge
import com.soywiz.klogger.Logger
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Views
import com.soywiz.korge.view.centerOnStage
import com.soywiz.korge.view.text
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.file.std.userHomeVfs
import events.EventBus
import events.PlayMapEvent

lateinit var VIEWS_INSTANCE: Views

class RootScene(
    override var views: Views, val globalEventBus: EventBus,
    val mapBridge: MapBridge
) : Scene() {
    override suspend fun Container.sceneInit() {
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

        sceneContainer.changeTo<MapViewerScene>()
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
