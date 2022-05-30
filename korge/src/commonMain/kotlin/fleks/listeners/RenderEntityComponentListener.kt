package fleks.listeners

import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Inject
import fleks.components.RenderEntityComponent
import ui.UIMap

class RenderEntityComponentListener : ComponentListener<RenderEntityComponent> {
    val uiMap = Inject.dependency<UIMap>()

    override fun onComponentAdded(entity: Entity, component: RenderEntityComponent) {
        println("RenderEntityComponentListener::onComponentAdded called")
        uiMap.addEntity(component.entity)
    }

    override fun onComponentRemoved(entity: Entity, component: RenderEntityComponent) {
        TODO("Not yet implemented")
    }

}