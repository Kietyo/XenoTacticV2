package com.xenotactic.korge.state

import com.kietyo.ktruth.assertThat
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.tup
import com.xenotactic.gamelogic.pathing.EntityPath
import com.xenotactic.gamelogic.pathing.GamePath
import com.xenotactic.gamelogic.pathing.Path
import com.xenotactic.gamelogic.pathing.PathFindingResult
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.gamelogic.utils.wH
import com.xenotactic.gamelogic.utils.wW
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.korge.utils.StagingEntityUtils
import com.xenotactic.gamelogic.model.GameWorld
import com.xenotactic.gamelogic.utils.GameSimulator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class GameMapApiTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun regressionTest1() = runTest {
        val width = 30.toGameUnit()
        val height = 11.toGameUnit()
        val gameWorld = GameWorld()
        val eventBus = EventBus(this)
        val engine = Engine(eventBus, gameWorld)
//        engine.stateInjections.setSingletonOrThrow(GameMapPathState(engine))
//        engine.stateInjections.setSingletonOrThrow(
//            GameMapDimensionsState(
//                engine,
//                width, height
//            )
//        )
//        engine.stateInjections.setSingletonOrThrow(
//            GameplayState.DEFAULT
//        )
//        engine.stateInjections.setSingletonOrThrow(
//            MutableEventQueueState()
//        )


        val gameSimulator = GameSimulator(width, height, engine)

        gameSimulator.gameMapApi.placeEntities(
            StagingEntityUtils.createStart(22 tup 0),
            StagingEntityUtils.createFinish(3 tup 2),
            StagingEntityUtils.createRock(22 tup 6 wW 2 wH 4),
            StagingEntityUtils.createRock(10 tup 3 wW 4 wH 2),
            StagingEntityUtils.createTower(20 tup 0)
        )

        gameSimulator.tick()

        assertThat(
            gameWorld.getPathFindingResult(
                width, height
            ).toGamePathOrNull()!!.toPathSequence()
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



