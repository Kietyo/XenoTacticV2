package com.xenotactic.korge.scenes

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.xenotactic.gamelogic.ecs.World
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.korge.components.EditorEComponent
import com.xenotactic.korge.components.GameMapControllerEComponent
import com.xenotactic.korge.components.NotificationTextEComponent
import com.xenotactic.korge.components.UIMapEComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.fleks.components.EntityRenderComponent
import com.xenotactic.korge.fleks.components.EntityUIComponent
import com.xenotactic.korge.fleks.components.PreSelectionComponent
import com.xenotactic.korge.fleks.listeners.PreSelectionComponentListener
import com.xenotactic.korge.fleks.listeners.RenderEntityComponentListener
import com.xenotactic.korge.fleks.systems.RenderEntitySystem
import com.xenotactic.korge.input_processors.CameraInputProcessor
import com.xenotactic.korge.input_processors.EditorPlacementMouseKomponent
import com.xenotactic.korge.input_processors.KeyInputProcessor
import com.xenotactic.korge.input_processors.MouseDragKomponent
import com.xenotactic.korge.input_processors.SelectorMouseProcessor
import com.xenotactic.korge.korge_utils.alignBottomToBottomOfWindow
import com.xenotactic.korge.renderer.MapRendererUpdater
import com.xenotactic.korge.systems.SelectingEntitiesSystem
import com.xenotactic.korge.ui.UIEditorButtons
import com.xenotactic.korge.ui.UIMap
import com.xenotactic.korge.ui.UINotificationText

class EditorScene : Scene() {
    override suspend fun Container.sceneInit() {

        val gameMap = GameMap(20, 20)
        val eventBus = EventBus(this@EditorScene)

        val uiMap =
            UIMap(gameMap, engine = null, initialRenderEntities = false).addTo(this).apply { //                draggableCloseable = draggableCloseable()
                centerOnStage()
            }

        val world = World().apply {
            addComponentListener()
        }

        val world = World {
            system(::RenderEntitySystem)

            component(::EntityRenderComponent, ::RenderEntityComponentListener)
            component(::EntityUIComponent)

            inject(uiMap)

            component(::PreSelectionComponent, ::PreSelectionComponentListener)
        }

        val engine = Engine(eventBus, world)

        val mouseDragKomponent = MouseDragKomponent(uiMap)
//        uiMap.addComponent(mouseDragKomponent)

        val editorComponent = EditorEComponent()

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

        addComponent(KeyInputProcessor(this, engine))
        addComponent(CameraInputProcessor(uiMap, engine))
        SelectorMouseProcessor(this, engine).let {
            addComponent(it)
            engine.setOneTimeComponent(it)
        }
        addComponent(editorPlacementMouseKomponent)

        MapRendererUpdater(engine, uiMap, eventBus)

        val uiEditorButtons = UIEditorButtons(engine).addTo(this).apply {
            centerXOnStage()
            alignBottomToBottomOfWindow()
        }

        SelectingEntitiesSystem(engine)

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