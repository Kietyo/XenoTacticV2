package com.xenotactic.korge.components

import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.korge.events.UpdatedGoalDataEvent
import solver.OptimizationGoal
import solver.SolverParams
import solver.SolverResult
import solver.StandardSolver3
import kotlin.math.ceil

data class GoalData(
    val bronzeGoal: Int,
    val silverGoal: Int,
    val goldGoal: Int,
)

class GoalEComponent(val engine: Engine, val eventBus: EventBus) {
    private var goalData: GoalData? = null
    private var solverResult: SolverResult.Success? = null

    fun calculateGoalForMap() {
        val mapComponent = engine.injections.getSingleton<GameMapControllerEComponent>()
        val shortestPath = mapComponent.shortestPath

        if (shortestPath == null) {
            goalData = null
            solverResult = null
            return
        }

        val result = StandardSolver3().solve(
            mapComponent.getGameMapDebugOnly(),
            SolverParams(5, OptimizationGoal.MaxPath)
        )
        when (result) {
            SolverResult.Unsuccessful -> {
                goalData = null
                solverResult = null
            }

            is SolverResult.Success -> {
                solverResult = result
                val goalPathLength = result.searchResult.pathSequence.pathLength.toInt()
                val currentPathLength = ceil(shortestPath.pathLength.toDouble()).toInt()
                val delta = goalPathLength - currentPathLength
                goalData = GoalData(
                    bronzeGoal = currentPathLength + (delta * 0.4).toInt(),
                    silverGoal = currentPathLength + (delta * 0.7).toInt(),
                    goldGoal = currentPathLength + delta,
                )
                eventBus.send(UpdatedGoalDataEvent(goalData!!))
            }
        }
    }
}