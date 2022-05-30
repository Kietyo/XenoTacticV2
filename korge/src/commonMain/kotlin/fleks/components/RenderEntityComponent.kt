package fleks.components

import com.xenotactic.gamelogic.model.MapEntity

data class RenderEntityComponent(
    var entity: MapEntity = MapEntity.ROCK_1X1
)