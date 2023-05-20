package com.xenotactic.korge.scenes

import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.events.ResizeMapEvent
import com.xenotactic.gamelogic.events.UpdatedPathLineEvent
import com.xenotactic.gamelogic.model.GameWorld
import com.xenotactic.gamelogic.state.GameMapDimensionsState
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.GameMapApi
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.input_processors.*
import com.xenotactic.korge.listeners_component.PreSelectionComponentListener
import com.xenotactic.korge.listeners_component.SelectionComponentListener
import com.xenotactic.korge.state.EditorState
import com.xenotactic.korge.state.MouseDragSettingsState
import com.xenotactic.korge.ui.UIEditorButtonsV2
import com.xenotactic.korge.ui.UIMapV2
import com.xenotactic.korge.ui.UINotificationText
import korlibs.korge.scene.Scene
import korlibs.korge.view.SContainer
import korlibs.korge.view.addTo
import korlibs.korge.view.align.centerOnStage
import korlibs.korge.view.align.centerXOnStage

class EditorSceneV2 : Scene() {
    override suspend fun SContainer.sceneInit() {
        val eventBus = EventBus(this@EditorSceneV2)
        val gameWorld = World()
        val mouseDragSettingsState = MouseDragSettingsState()
        val width = 10.toGameUnit()
        val height = 10.toGameUnit()
        val engine = Engine(eventBus, GameWorld(world = gameWorld)).apply {
            stateInjections.setSingletonOrThrow(GameMapDimensionsState(this, width, height))
            injections.setSingletonOrThrow(GameMapApi(this))
            injections.setSingletonOrThrow(mouseDragSettingsState)
        }
        val uiMapV2 = UIMapV2(engine).addTo(this)
        uiMapV2.centerOnStage()

        val mouseDragInputProcessor =
            MouseDragInputProcessor(views, uiMapV2, mouseDragSettingsState.mouseDragStateSettings)
        mouseDragInputProcessor.setup(this)

        engine.apply {
            stateInjections.setSingletonOrThrow(EditorState(engine))
            injections.setSingletonOrThrow(mouseDragInputProcessor)
            injections.setSingletonOrThrow(uiMapV2)
        }

        gameWorld.apply {
            injections = engine.injections
            addComponentListener(PreSelectionComponentListener(engine))
            addComponentListener(SelectionComponentListener(engine))
        }

        val editorPlacementInputProcessor = EditorPlacementInputProcessor(
            views, this, engine
        )
        editorPlacementInputProcessor.setup(this)

        val cameraInputProcessor = CameraInputProcessor(uiMapV2, engine)
        cameraInputProcessor.setup(this)

        val keyInputProcessor = KeyInputProcessor(this, engine)
        keyInputProcessor.setup(this)

        val selectorMouseProcessorV2 = SelectorMouseProcessorV2(views, this@sceneInit, engine).apply {
            engine.injections.setSingletonOrThrow(this)
        }
        selectorMouseProcessorV2.setup(this)

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