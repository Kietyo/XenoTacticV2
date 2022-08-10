package com.xenotactic.korge.ui

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.SContainer
import com.xenotactic.ecs.World

class EditorSceneV2 : Scene() {
    override suspend fun SContainer.sceneInit() {

        val world = World()
    }
}