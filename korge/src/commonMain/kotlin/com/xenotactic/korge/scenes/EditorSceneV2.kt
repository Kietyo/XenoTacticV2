package com.xenotactic.korge.scenes

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.events.ResizeMapEvent
import com.xenotactic.korge.events.UpdatedPathLineEvent
import com.xenotactic.korge.input_processors.*
import com.xenotactic.korge.models.GameWorld
import com.xenotactic.korge.models.SettingsContainer
import com.xenotactic.korge.state.EditorState
import com.xenotactic.korge.state.GameMapApi
import com.xenotactic.korge.state.GameMapDimensionsState
import com.xenotactic.korge.ui.UIEditorButtonsV2
import com.xenotactic.korge.ui.UIMapV2
import com.xenotactic.korge.ui.UINotificationText
import com.xenotactic.korge.component_listeners.PreSelectionComponentListener
import com.xenotactic.korge.component_listeners.SelectionComponentListener

class EditorSceneV2 : Scene() {
    override suspend fun SContainer.sceneInit() {
        val eventBus = EventBus(this@EditorSceneV2)
        val gameWorld = World()
        val settingsContainer = SettingsContainer()
        val engine = Engine(eventBus, GameWorld(gameWorld)).apply {
            injections.setSingletonOrThrow(GameMapDimensionsState(this, 10.toGameUnit(), 10.toGameUnit()))
            injections.setSingletonOrThrow(GameMapApi(this))
            injections.setSingletonOrThrow(settingsContainer)
        }
        val uiMapV2 = UIMapV2(engine).addTo(this)
        uiMapV2.centerOnStage()

        val mouseDragInputProcessor = MouseDragInputProcessor(uiMapV2, settingsContainer.mouseDragStateSettings)
        addComponent(mouseDragInputProcessor)

        engine.apply {
            injections.setSingletonOrThrow(EditorState(engine))
            injections.setSingletonOrThrow(mouseDragInputProcessor)
            injections.setSingletonOrThrow(uiMapV2)
        }

        gameWorld.apply {
            injections = engine.injections
            addComponentListener(PreSelectionComponentListener(engine))
            addComponentListener(SelectionComponentListener(engine))
        }

        addComponent(EditorPlacementInputProcessorV2(
            this, engine
        ))

        addComponent(CameraInputProcessor(uiMapV2, engine))

        addComponent(KeyInputProcessor(this, engine))

        addComponent(SelectorMouseProcessorV2(this@sceneInit, engine).apply {
            engine.injections.setSingletonOrThrow(this)
        })

        val uiEditorButtonsV2 =
            UIEditorButtonsV2(engine, this).addTo(this).apply {
                this.resize()
            }

        val notificationText = UINotificationText(engine, "N/A").addTo(this).apply {
            centerXOnStage()
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