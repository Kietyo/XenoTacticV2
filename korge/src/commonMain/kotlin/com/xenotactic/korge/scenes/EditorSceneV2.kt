package com.xenotactic.korge.scenes

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.xenotactic.ecs.World
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.events.UpdatedPathLineEvent
import com.xenotactic.korge.family_listeners.AddEntityFamilyListener
import com.xenotactic.korge.input_processors.*
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

        val mouseDragInputProcessor = MouseDragInputProcessor(uiMapV2)
        addComponent(mouseDragInputProcessor)

        val engine = Engine(eventBus, gameWorld).apply {
            injections.setSingleton(EditorState())
            injections.setSingleton(mouseDragInputProcessor)
            injections.setSingleton(GameMapState(this, eventBus, uiMapV2, gameWorld))
            injections.setSingleton(SelectorMouseProcessorV2(this@sceneInit, this, uiWorld))
            injections.setSingleton(uiMapV2)
        }

        uiWorld.apply {
            injections = engine.injections
        }
        gameWorld.apply {
            injections = engine.injections
            addFamilyListener(AddEntityFamilyListener(this))
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

        addComponent(CameraInputProcessor(uiMapV2, engine))

        addComponent(KeyInputProcessor(this, engine))

        val uiEditorButtonsV2 = UIEditorButtonsV2(uiWorld, engine, uiMapV2).addTo(this).apply {
            centerXOnStage()
            alignBottomToBottomOfWindow()
        }

        val notificationText = UINotificationText(engine, "N/A").addTo(this).apply {
            centerXOnStage()
        }

        eventBus.register<UpdatedPathLineEvent> {
            uiMapV2.renderPathLines(it.pathSequence)
        }
    }
}