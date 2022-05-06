package scenes

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.text

class EditorScene() : Scene() {
    override suspend fun Container.sceneInit() {

        text("Hello world")
    }
}