package state

import com.soywiz.korio.async.suspendTest
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.pathing.EntityPath
import com.xenotactic.gamelogic.pathing.GamePath
import com.xenotactic.gamelogic.pathing.Path
import com.xenotactic.gamelogic.pathing.PathFindingResult
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.models.GameWorld
import com.xenotactic.korge.state.GameMapApi
import com.xenotactic.testing.assertThat
import pathing.AStarSearcher
import kotlin.test.Test

internal class GameMapApiTest {
    @Test
    fun regressionTest1() = suspendTest {
        val gameWorld = GameWorld()
        val eventBus = EventBus(this)
        val engine = Engine(eventBus, gameWorld)
        val gameMapApi = GameMapApi(engine)

        gameMapApi.placeEntities(
            MapEntity.Start(22, 0),
            MapEntity.Finish(3, 2),
            MapEntity.ROCK_2X4.at(22, 6),
            MapEntity.ROCK_4X2.at(10, 3),
        )

        assertThat(
            gameMapApi.gameMapPathState.shortestPath!!
        ).isEqualTo(
            PathFindingResult.Success(
                GamePath(
                    listOf(
                        EntityPath.EntityToEntityPath(
                            MapEntity.Start(22, 0),
                            MapEntity.Finish(3, 2),
                            Path.create(
                                23 to 1,
                                22.007071067811864 to 2.0070710678118653,
                                4 to 3
                            )
                        )
                    )
                )
            ).toGamePathOrNull()!!.toPathSequence()
        )
    }
}