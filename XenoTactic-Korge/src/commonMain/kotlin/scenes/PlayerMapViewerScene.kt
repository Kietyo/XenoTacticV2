package scenes

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.text
import daos.PlayerDataApi

class PlayerMapViewerScene: Scene() {
    override suspend fun Container.sceneInit() {
        val playerData = PlayerDataApi.getPlayerData()
        this.text("Hello world!")


    }
}