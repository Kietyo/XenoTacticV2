package com.xenotactic.korge.scenes

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.xenotactic.ecs.World
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.events.ResizeMapEvent
import com.xenotactic.korge.events.UpdatedPathLineEvent
import com.xenotactic.korge.family_listeners.AddEntityFamilyListener
import com.xenotactic.korge.input_processors.*
import com.xenotactic.korge.korge_utils.alignBottomToBottomOfWindow
import com.xenotactic.korge.models.GameWorld
import com.xenotactic.korge.models.SettingsContainer
import com.xenotactic.korge.state.EditorState
import com.xenotactic.korge.state.GameMapApi
import com.xenotactic.korge.state.GameMapDimensionsState
import com.xenotactic.korge.ui.UIEditorButtonsV2
import com.xenotactic.korge.ui.UIMapV2
import com.xenotactic.korge.ui.UINotificationText

class EditorSceneV2 : Scene() {
    override suspend fun SContainer.sceneInit() {
        val eventBus = EventBus(this@EditorSceneV2)
        val gameWorld = World()
        val uiWorld = World()
        val settingsContainer = SettingsContainer()
        val engine = Engine(eventBus, GameWorld(gameWorld)).apply {
            injections.setSingleton(GameMapDimensionsState(this, 10, 10))
            injections.setSingleton(GameMapApi(this, eventBus))
            injections.setSingleton(settingsContainer)
        }
        val uiMapV2 = UIMapV2(engine).addTo(this)
        uiMapV2.centerOnStage()

        val mouseDragInputProcessor = MouseDragInputProcessor(uiMapV2, settingsContainer.mouseDragStateSettings)
        addComponent(mouseDragInputProcessor)

        engine.apply {
            injections.setSingleton(EditorState())
            injections.setSingleton(mouseDragInputProcessor)
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

        addComponent(EditorPlacementInputProcessorV2(
            this, uiMapV2, uiWorld, engine
        ))

        addComponent(CameraInputProcessor(uiMapV2, engine))

        addComponent(KeyInputProcessor(this, engine))

        val uiEditorButtonsV2 =
            UIEditorButtonsV2(uiWorld, engine, uiMapV2, this).addTo(this).apply {
                centerXOnStage()
                alignBottomToBottomOfWindow()
            }

        val notificationText = UINotificationText(engine, "N/A").addTo(this).apply {
            centerXOnStage()
        }

        SelectorMouseProcessor(this, engine).let {
            addComponent(it)
            engine.injections.setSingleton(it)
        }

        eventBus.register<UpdatedPathLineEvent> {
            uiMapV2.renderPathLines(it.pathSequence)
        }
        eventBus.register<ResizeMapEvent> {
            uiMapV2.handleResizeMapEvent(it)
//            uiMapV2.centerOnStage()
        }
    }
}