import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.korge.scenes.*
import com.xenotactic.korge.utils.MapBridge
import korlibs.image.color.Colors
import korlibs.korge.KorgeConfig
import korlibs.korge.KorgeDisplayMode
import korlibs.korge.scene.sceneContainer
import korlibs.korge.view.views
import korlibs.logger.Logger
import korlibs.math.geom.Anchor
import korlibs.math.geom.ScaleMode
import korlibs.math.geom.Size
import korlibs.render.GameWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

suspend fun main() = KorgeConfig(
    virtualSize = Size(1920, 1080),
    windowSize = Size(1280, 720),
    backgroundColor = Colors["#2b2b2b"],
    displayMode = KorgeDisplayMode(
        ScaleMode.FIT,
        Anchor.MIDDLE_CENTER,
        clipBorders = false
    ),
    quality = GameWindow.Quality.AUTOMATIC,
    forceRenderEveryFrame = true
).start() {

    Logger.defaultLevel = Logger.Level.DEBUG
    //        val globalBus = EventBus(CoroutineScope(Dispatchers.Main))
    val globalBus = EventBus(CoroutineScope(Dispatchers.Default))
    println("Preparing main module")
    println(views)

    val mapBridge = MapBridge()

    //        mapInstance(GameScene(mapBridge))

    injector.apply {
        mapPrototype { RootScene(views(), globalBus, mapBridge) }
        mapPrototype {
            println("mapping GameScene")
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
        //        changeTo<TestScene>()
        //        changeTo({
        //            TestScene()
        //        })
    }
}
