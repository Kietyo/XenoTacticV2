import bridges.MapBridge
import com.soywiz.klogger.Logger
import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Views
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korma.geom.Anchor
import com.soywiz.korma.geom.SizeInt
import com.xenotactic.gamelogic.model.GameMap
import events.EventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import com.xenotactic.gamelogic.model.MapEntity
import scenes.*
import kotlin.reflect.KClass

object MainModule : Module() {
    override val bgcolor: RGBA = Colors["#2b2b2b"]
    override val size: SizeInt = SizeInt(1000, 480)
    override val clipBorders: Boolean = false
    override val mainScene: KClass<out Scene> = RootScene::class
    override val scaleAnchor: Anchor
        get() = Anchor.MIDDLE_CENTER

    val logger = Logger<MainModule>()

    override suspend fun AsyncInjector.configure() {
        Logger.defaultLevel = Logger.Level.DEBUG
        val views = this.get<Views>()
        //        val globalBus = EventBus(CoroutineScope(Dispatchers.Main))
        val globalBus = EventBus(CoroutineScope(Dispatchers.Default))
        println("Preparing main module")
        println(views)

        val gameMap = GameMap.create(
            10, 10,
            MapEntity.Start(4, 8),
            MapEntity.Finish(4, 0),
            MapEntity.SpeedArea(3, 3, 2, 0.5)
        )

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
        mapPrototype { GoldensViewerScene(globalBus) }
        mapPrototype { TestScene() }
        mapPrototype {
            EditorScene()
        }

    }
}

suspend fun main() = Korge(Korge.Config(module = MainModule))