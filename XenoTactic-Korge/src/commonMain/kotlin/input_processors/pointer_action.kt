package input_processors

import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.model.MapEntity

data class RemoveRockData(val x: Int, val y: Int, val entity: MapEntity)
data class RemoveTowerData(val x: Int, val y: Int, val entity: MapEntity)

sealed class PointerAction {
    object Inactive : PointerAction()
    data class HighlightForPlacement(
        val mapEntity: MapEntity, var placementLocation: IntPoint? =
            null
    ) : PointerAction()

    data class RemoveRockAtPlace(var data: RemoveRockData? = null) : PointerAction()
    data class RemoveTowerAtPlace(var data: RemoveTowerData? = null) : PointerAction()
}

sealed class PointerActionInputConfig {
    object Inactive : PointerActionInputConfig()
    data class HighlightForPlacement(val mapEntity: MapEntity) : PointerActionInputConfig()
    object RemoveEntityAtPlace : PointerActionInputConfig()
}

sealed class PointerActionData {
    object Inactive : PointerActionData()
    data class HighlightForPlacement(val intPoint: IntPoint, val entity: MapEntity) : PointerActionData()
    data class RemoveEntityAtPlace(val x: Int, val y: Int, val entity: MapEntity) :
        PointerActionData()
}
