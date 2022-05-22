package scenes

import com.soywiz.korge.input.*
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiHorizontalStack
import com.soywiz.korge.view.*
import com.xenotactic.gamelogic.model.GameMap
import components.EditorComponent
import components.GameMapControllerComponent
import engine.Engine
import events.EventBus
import input_processors.EditorPlacementMouseKomponent
import input_processors.MouseDragKomponent
import korge_utils.alignBottomToBottomOfWindow
import renderer.MapRendererUpdater
import ui.BoardType
import ui.UIMapSettings
import ui.uiMap

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

//        lateinit var draggableCloseable: DraggableCloseable

        val uiMap =
            uiMap(gameMap).apply {
//                draggableCloseable = draggableCloseable()
                centerOnStage()
            }

        val mouseDragKomponent = MouseDragKomponent(uiMap)
        uiMap.addComponent(mouseDragKomponent)

        val editorComponent = EditorComponent()

        val eventBus = EventBus(this@EditorScene)

        val engine = Engine()
        engine.setOneTimeComponent(editorComponent)
        engine.setOneTimeComponent(
            GameMapControllerComponent(
                engine, eventBus,
                gameMap = gameMap
            )
        )

        val editorPlacementMouseKomponent = EditorPlacementMouseKomponent(
            this, uiMap, engine
        )

        addComponent(editorPlacementMouseKomponent)

        MapRendererUpdater(engine, uiMap, eventBus)

        uiHorizontalStack {
            uiButton(text = "Add rocks") {
                onClick {
                    when (currentMode) {
                        Mode.PLAYING -> {
                            // Switch to editing mode
                            notificationText.text = "Rock Placement Mode"
                            mouseDragKomponent.allowLeftClickDrag = false
                            editorComponent.isEditingEnabled = true
                            currentMode = Mode.EDITING
                        }
                        Mode.EDITING -> {
                            // Switching to playing mode
                            notificationText.text = "N/A"
                            mouseDragKomponent.allowLeftClickDrag = true
                            editorComponent.isEditingEnabled = false
                            currentMode = Mode.PLAYING
                            uiMap.hideHighlightRectangle()
                        }
                    }

                }
            }

            centerXOnStage()
            alignBottomToBottomOfWindow()
        }


        //        addUpdater {
        //            if (false && currentMode == Mode.EDITING) {
        //                val globalMouse = mouse.currentPosGlobal
        //                val (gridX, gridY) =
        //                    uiMap.getGridPositionsFromGlobalMouse(globalMouse.x, globalMouse.y)
        //
        //                val (roundedGridX, roundedGridY) = uiMap.getRoundedGridCoordinates(
        //                    gridX, gridY,
        //                    1, 1
        //                )
        //
        //                println(
        //                    "gridX: $gridX, gridY: $gridY, roundedGridX: $roundedGridX, roundedGridY:" +
        //                            " $roundedGridY"
        //                )
        //
        //                uiMap.renderRectangle(roundedGridX, roundedGridY, 1, 1)
        //            }
        //        }
        //
    }
}