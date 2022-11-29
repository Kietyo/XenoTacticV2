package state

import com.soywiz.korio.async.suspendTest
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.tup
import com.xenotactic.gamelogic.pathing.EntityPath
import com.xenotactic.gamelogic.pathing.GamePath
import com.xenotactic.gamelogic.pathing.Path
import com.xenotactic.gamelogic.pathing.PathFindingResult
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.gamelogic.utils.wH
import com.xenotactic.gamelogic.utils.wW
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.korge_utils.StagingEntityUtils
import com.xenotactic.korge.models.GameWorld
import com.xenotactic.korge.state.GameMapApi
import com.xenotactic.korge.state.GameMapDimensionsState
import com.xenotactic.korge.state.GameMapPathState
import com.xenotactic.testing.assertThat
import kotlin.test.Test

internal class GameMapApiTest {
    @Test
    fun regressionTest1() = suspendTest {
        val width = 30.toGameUnit()
        val height = 11.toGameUnit()
        val gameWorld = GameWorld()
        val eventBus = EventBus(this)
        val engine = Engine(eventBus, gameWorld)
        engine.injections.setSingletonOrThrow(GameMapPathState(engine))
        engine.injections.setSingletonOrThrow(
            GameMapDimensionsState(
                engine,
                width, height
            )
        )
        val gameMapApi = GameMapApi(engine)


//        gameMapApi.placeEntities(
//            MapEntity.Start(22, 0),
//            MapEntity.Finish(3, 2),
//            MapEntity.ROCK_2X4.at(22, 6),
//            MapEntity.ROCK_4X2.at(10, 3),
//            MapEntity.TOWER.at(20, 0)
//        )

        gameMapApi.placeEntitiesV2(
            StagingEntityUtils.createStart(22 tup 0),
            StagingEntityUtils.createFinish(3 tup 2),
            StagingEntityUtils.createRock(22 tup 6 wW 2 wH 4),
            StagingEntityUtils.createRock(10 tup 3 wW 4 wH 2),
            StagingEntityUtils.createTower(20 tup 0)
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



