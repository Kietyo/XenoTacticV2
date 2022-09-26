package com.xenotactic.korge.state

import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.UpdatedPathLineEvent

class GameMapPathState(
    val engine: Engine
) {
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