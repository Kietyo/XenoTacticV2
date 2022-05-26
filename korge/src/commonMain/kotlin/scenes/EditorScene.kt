package scenes

import com.soywiz.korge.input.*
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiHorizontalStack
import com.soywiz.korge.view.*
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntityType
import components.EditorEComponent
import components.GameMapControllerEComponent
import components.NotificationTextEComponent
import components.UIMapEComponent
import engine.Engine
import events.EventBus
import input_processors.EditorPlacementMouseKomponent
import input_processors.KeyInputProcessor
import input_processors.MouseDragKomponent
import korge_utils.alignBottomToBottomOfWindow
import renderer.MapRendererUpdater
import ui.UIEditorButtons
import ui.UINotificationText
import ui.uiMap

class EditorScene() : Scene() {
    override suspend fun Container.sceneInit() {

        val gameMap = GameMap(20, 20)

        //        lateinit var draggableCloseable: DraggableCloseable

        val uiMap =
            uiMap(gameMap).apply { //                draggableCloseable = draggableCloseable()
                centerOnStage()
            }



        val mouseDragKomponent = MouseDragKomponent(uiMap)
        uiMap.addComponent(mouseDragKomponent)

        val editorComponent = EditorEComponent()

        val eventBus = EventBus(this@EditorScene)

        val engine = Engine(eventBus)
        engine.setOneTimeComponent(editorComponent)
        engine.setOneTimeComponent(
            GameMapControllerEComponent(
                engine, eventBus, gameMap = gameMap
            )
        )
        engine.setOneTimeComponent(UIMapEComponent(uiMap))
        engine.setOneTimeComponent(mouseDragKomponent)

        val notificationText = UINotificationText(engine, "N/A").addTo(this).apply {
            centerXOnStage()
        }
        engine.setOneTimeComponent(NotificationTextEComponent(notificationText))


        val editorPlacementMouseKomponent = EditorPlacementMouseKomponent(
            this, uiMap, engine
        )

        addComponent(editorPlacementMouseKomponent)
        addComponent(KeyInputProcessor(this, engine))

        MapRendererUpdater(engine, uiMap, eventBus)

        val uiEditorButtons = UIEditorButtons(engine).addTo(this).apply {
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