package com.xenotactic.korge.scenes

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.SContainer
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.centerOnStage
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.model.MapEntityData
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.korge.components.MapEntityComponent
import com.xenotactic.korge.components.SizeComponent
import com.xenotactic.korge.components.BottomLeftPositionComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.family_listeners.AddEntityToUIMapFamilyListener
import com.xenotactic.korge.input_processors.MouseDragKomponent
import com.xenotactic.korge.state.EditorState
import com.xenotactic.korge.ui.UIMapV2

class EditorSceneV2 : Scene() {
    override suspend fun SContainer.sceneInit() {
        val uiMapV2 = UIMapV2().addTo(this)
        uiMapV2.centerOnStage()

        val eventBus = EventBus(this@EditorSceneV2)
        val gameWorld = World()
        val uiWorld = World()
        val engine = Engine(eventBus, gameWorld)

        uiWorld.apply {
            this.injections.setSingleton(EditorState())
        }
        gameWorld.apply {
            injections.setSingleton(uiMapV2)
            addFamilyListener(AddEntityToUIMapFamilyListener(this))
            addEntity {
                addComponentOrThrow(MapEntityComponent(MapEntityData.Start))
                addComponentOrThrow(SizeComponent.SIZE_2X2_COMPONENT)
                addComponentOrThrow(BottomLeftPositionComponent(0, 0))
            }

        }

//        val uiEditorButtonsV2 = UIEditorButtonsV2(gameWorld, uiWorld, engine)


        val mouseDragKomponent = MouseDragKomponent(uiMapV2)
        addComponent(mouseDragKomponent)
    }
}