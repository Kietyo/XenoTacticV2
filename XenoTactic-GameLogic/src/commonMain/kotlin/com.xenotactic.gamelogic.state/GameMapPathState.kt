package com.xenotactic.gamelogic.state

import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.engine.Engine
import com.xenotactic.gamelogic.engine.State
import com.xenotactic.gamelogic.events.UpdatedPathLineEvent

class GameMapPathState(
    val engine: Engine
) : State {
    var shortestPath: PathSequence? = null
        private set

    fun updatePath(newPath: PathSequence?) {
        shortestPath = newPath
        engine.eventBus.send(
            UpdatedPathLineEvent(
                shortestPath,
                shortestPath?.pathLength
            )
        )
    }

}