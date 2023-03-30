import MainModule.logger
import korlibs.logger.Logger
import com.soywiz.korge.Korge
import com.soywiz.korge.KorgeConfig
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.sceneContainer
import com.soywiz.korge.view.Views
import com.soywiz.korge.view.views
import com.soywiz.korgw.GameWindow
import korlibs.image.color.Colors
import korlibs.image.color.RGBA
import com.soywiz.korinject.AsyncInjector
import korlibs.math.geom.Anchor
import korlibs.math.geom.MSizeInt
import korlibs.math.geom.SizeInt
import com.xenotactic.gamelogic.utils.GlobalResources
import com.xenotactic.korge.bridges.MapBridge
import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.korge.scenes.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.reflect.KClass

object MainModule : Module() {
    override val bgcolor: RGBA = Colors["#2b2b2b"]
    override val virtualSize: SizeInt = SizeInt(1920, 1080)
    override val windowSize: SizeInt = SizeInt(1000, 720)
    override val clipBorders: Boolean = false
    override val mainScene: KClass<out Scene> = RootScene::class
    override val scaleAnchor: Anchor
        get() = Anchor.MIDDLE_CENTER
    override val quality: GameWindow.Quality = GameWindow.Quality.AUTOMATIC

    val logger = Logger<MainModule>()

    override suspend fun AsyncInjector.configure() {
        Logger.defaultLevel = Logger.Level.DEBUG
        val views = this.get<Views>()
        //        val globalBus = EventBus(CoroutineScope(Dispatchers.Main))
        val globalBus = EventBus(CoroutineScope(Dispatchers.Default))
        println("Preparing main module")
        println(views)

        GlobalResources.init()

        val mapBridge = MapBridge()

        //        mapInstance(GameScene(mapBridge))

        mapPrototype { RootScene(views, globalBus, mapBridge) }
        mapPrototype {
            logger.debug {
                "mapping GameScene"
            }
            GameScene(mapBridge)
        }
        mapPrototype { MapViewerScene(globalBus) }
        mapPrototype { GoldensViewerScene() }
        mapPrototype { TestScene() }
        mapPrototype { EditorSceneV2() }
        mapPrototype { PlayScene() }

    }
}

suspend fun main() = KorgeConfig(module = MainModule, forceRenderEveryFrame = false).start() {
    Logger.defaultLevel = Logger.Level.DEBUG
    //        val globalBus = EventBus(CoroutineScope(Dispatchers.Main))
    val globalBus = EventBus(CoroutineScope(Dispatchers.Default))
    println("Preparing main module")
    println(views)

    GlobalResources.init()

    val mapBridge = MapBridge()

    //        mapInstance(GameScene(mapBridge))

    injector.apply {
        mapPrototype { RootScene(views(), globalBus, mapBridge) }
        mapPrototype {
            logger.debug {
                "mapping GameScene"
            }
            GameScene(mapBridge)
        }
        mapPrototype { MapViewerScene(globalBus) }
        mapPrototype { GoldensViewerScene() }
        mapPrototype { TestScene() }
        mapPrototype { EditorSceneV2() }
        mapPrototype { PlayScene() }
        mapPrototype { RootScene(views, globalBus, mapBridge) }
    }


    sceneContainer {
        changeTo<RootScene>()
    }
}
