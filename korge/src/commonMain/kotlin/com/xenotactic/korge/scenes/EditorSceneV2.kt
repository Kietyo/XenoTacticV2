package com.xenotactic.korge.scenes

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.BottomLeftPositionComponent
import com.xenotactic.gamelogic.components.MapEntityComponent
import com.xenotactic.gamelogic.components.SizeComponent
import com.xenotactic.gamelogic.model.MapEntityData
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.family_listeners.AddEntityToUIMapFamilyListener
import com.xenotactic.korge.input_processors.EditorPlacementInputProcessorV2
import com.xenotactic.korge.input_processors.MouseDragInputProcessor
import com.xenotactic.korge.input_processors.SelectorMouseProcessorV2
import com.xenotactic.korge.korge_utils.alignBottomToBottomOfWindow
import com.xenotactic.korge.state.EditorState
import com.xenotactic.korge.state.GameMapState
import com.xenotactic.korge.ui.UIEditorButtonsV2
import com.xenotactic.korge.ui.UIMapV2
import com.xenotactic.korge.ui.UINotificationText

class EditorSceneV2 : Scene() {
    override suspend fun SContainer.sceneInit() {
        val uiMapV2 = UIMapV2().addTo(this)
        uiMapV2.centerOnStage()

        val eventBus = EventBus(this@EditorSceneV2)
        val gameWorld = World()
        val uiWorld = World()
        val engine = Engine(eventBus, gameWorld)

        val mouseDragInputProcessor = MouseDragInputProcessor(uiMapV2)
        addComponent(mouseDragInputProcessor)


        uiWorld.apply {
            this.injections.setSingleton(EditorState())
            this.injections.setSingleton(mouseDragInputProcessor)
            this.injections.setSingleton(GameMapState(eventBus, uiMapV2, gameWorld))
            this.injections.setSingleton(SelectorMouseProcessorV2(this@sceneInit, engine, uiWorld))
        }
        gameWorld.apply {
            injections.setSingleton(uiMapV2)
            addFamilyListener(AddEntityToUIMapFamilyListener(this))
//            addEntity {
//                addComponentOrThrow(MapEntityComponent(MapEntityData.Start))
//                addComponentOrThrow(SizeComponent.SIZE_2X2_COMPONENT)
//                addComponentOrThrow(BottomLeftPositionComponent(0, 0))
//            }
//
//            addEntity {
//                addComponentOrThrow(MapEntityComponent(MapEntityData.Finish))
//                addComponentOrThrow(SizeComponent.SIZE_2X2_COMPONENT)
//                addComponentOrThrow(BottomLeftPositionComponent(5, 5))
//            }

        }

        val editorPlacementInputProcessor = EditorPlacementInputProcessorV2(
            this, uiMapV2, uiWorld, gameWorld, engine
        )
        addComponent(editorPlacementInputProcessor)

        val uiEditorButtonsV2 = UIEditorButtonsV2(uiWorld, engine).addTo(this).apply {
            centerXOnStage()
            alignBottomToBottomOfWindow()
        }



        val notificationText = UINotificationText(engine, "N/A").addTo(this).apply {
            centerXOnStage()
        }
    }
}