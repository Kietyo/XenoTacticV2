import MainModule.logger
import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.utils.GlobalResources
import com.xenotactic.korge.bridges.MapBridge
import com.xenotactic.korge.scenes.*
import korlibs.image.color.Colors
import korlibs.image.color.RGBA
import korlibs.inject.AsyncInjector
import korlibs.korge.KorgeConfig
import korlibs.korge.scene.Module
import korlibs.korge.scene.Scene
import korlibs.korge.scene.sceneContainer
import korlibs.korge.view.Views
import korlibs.korge.view.views
import korlibs.logger.Logger
import korlibs.math.geom.Anchor
import korlibs.math.geom.SizeInt
import korlibs.render.GameWindow
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
