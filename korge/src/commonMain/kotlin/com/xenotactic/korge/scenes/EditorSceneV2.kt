package com.xenotactic.korge.scenes

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.SContainer
import com.soywiz.korge.view.addTo
import com.xenotactic.ecs.World
import com.xenotactic.korge.components.MapEntityComponent
import com.xenotactic.korge.components.SIZE_2X2_COMPONENT
import com.xenotactic.korge.components.SizeComponent
import com.xenotactic.korge.components.StartEntityComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.input_processors.MouseDragKomponent
import com.xenotactic.korge.state.EditorState
import com.xenotactic.korge.ui.UIEditorButtonsV2
import com.xenotactic.korge.ui.UIMapV2

class EditorSceneV2 : Scene() {
    override suspend fun SContainer.sceneInit() {
        val uiMapV2 = UIMapV2().addTo(this)


        val eventBus = EventBus(this@EditorSceneV2)
        val gameWorld = World()
        val uiWorld = World()
        val engine = Engine(eventBus, gameWorld)

        uiWorld.apply {
            this.injections.setSingleton(EditorState())
        }
        gameWorld.apply {

            addEntity {
                addComponentOrThrow(MapEntityComponent)
                addComponentOrThrow(SIZE_2X2_COMPONENT)
                addComponentOrThrow(StartEntityComponent)
            }


        }

//        val uiEditorButtonsV2 = UIEditorButtonsV2(gameWorld, uiWorld, engine)


        val mouseDragKomponent = MouseDragKomponent(uiMapV2)
        addComponent(mouseDragKomponent)
    }
}