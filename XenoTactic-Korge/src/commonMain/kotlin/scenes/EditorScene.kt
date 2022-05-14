package scenes

import com.soywiz.klock.Frequency
import com.soywiz.klock.TimeProvider
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tween.get
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiHorizontalStack
import com.soywiz.korge.view.*
import com.soywiz.korio.async.Signal
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.IntPoint
import components.ObjectPlacementComponent
import components.UIMapControllerComponent
import engine.Engine
import events.EventBus
import input_processors.MouseEventWithGridCoordinatesProcessor
import input_processors.ObjectPlacementInputProcessor
import korge_utils.alignBottomToBottomOfWindow
import ui.uiMap
import kotlin.reflect.KProperty1

enum class Mode {
    PLAYING,
    EDITING
}
class EditorScene() : Scene() {
    override suspend fun Container.sceneInit() {

        text("Hello world")

        val notificationText = text("N/A") {
            centerXOnStage()
        }

        var currentMode = Mode.PLAYING

        val gameMap = GameMap(10, 10)

        lateinit var draggableCloseable: DraggableCloseable

        val uiMap =
            uiMap(gameMap).apply {
                draggableCloseable = draggableCloseable()
                centerOnStage()
            }

        val engine = Engine()
        val eventBus = EventBus(this@EditorScene)
        engine.setOneTimeComponent(ObjectPlacementComponent())
        engine.setOneTimeComponent(UIMapControllerComponent(engine, eventBus))

        val objectPlacementInputProcessor = ObjectPlacementInputProcessor(
            this, uiMap, Engine()
        )


        uiHorizontalStack {
            uiButton(text = "Add rocks") {
                onClick {
                    when (currentMode) {
                        Mode.PLAYING -> {
                            notificationText.text = "Rock Placement Mode"
                            draggableCloseable.close()
                            currentMode = Mode.EDITING
                        }
                        Mode.EDITING -> {
                            notificationText.text = "N/A"
                            draggableCloseable = uiMap.draggableCloseable()
                            currentMode = Mode.PLAYING
                        }
                    }

                }
            }

            alignBottomToBottomOfWindow()
            centerXOnStage()
        }



        addUpdater {
            if (currentMode == Mode.EDITING) {
                val globalMouse = mouse.currentPosGlobal
                val (gridX, gridY) =
                    uiMap.getGridPositionsFromGlobalMouse(globalMouse.x, globalMouse.y)

                val (roundedGridX, roundedGridY) = uiMap.getRoundedGridCoordinates(gridX, gridY,
                    1, 1)

                println("gridX: $gridX, gridY: $gridY, roundedGridX: $roundedGridX, roundedGridY:" +
                        " $roundedGridY")

                uiMap.renderRectangle(roundedGridX, roundedGridY, 1, 1)
            }
        }

    }
}