package com.xenotactic.korge.scenes

import com.xenotactic.korge.utils.PlayerDataApi
import korlibs.korge.scene.Scene
import korlibs.korge.view.SContainer
import korlibs.korge.view.text

class PlayerMapViewerScene : Scene() {
    override suspend fun SContainer.sceneInit() {
        val playerData = PlayerDataApi.getPlayerData()
        this.text("Hello world!")


    }
}