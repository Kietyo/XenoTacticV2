package com.xenotactic.gamelogic.state

import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.State
import com.xenotactic.gamelogic.events.UpdatedPathLineEvent
import com.xenotactic.gamelogic.pathing.PathSequence

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