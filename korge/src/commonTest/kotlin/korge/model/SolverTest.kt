import com.soywiz.klogger.Logger
import com.xenotactic.gamelogic.globals.COUNTERS
import com.xenotactic.gamelogic.korge_utils.loadGameMapFromGoldenBlocking
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.utils.measureTime
import solver.*
import kotlin.test.Test
import kotlin.test.assertIs

internal class SolverTest {
    @Test
    fun regressionTest1() {
        Logger.defaultLevel = Logger.Level.INFO
        val map: GameMap = loadGameMapFromGoldenBlocking("00760.json")

        val solver = StandardSolver3(
            SolverSettings(
                numSpotsToConsider = 1,
                numSpotsToExplore = 1
            )
        )

        println("Attempting to solve:")
        val result = measureTime("Time it took to solve") {
            solver.solve(map, SolverParams(15, OptimizationGoal.MaxPath))
        }

        assertIs<SolverResult.Success>(result.second)

        println(result)
        println("Time in seconds: ${result.first / 1e9}")

        println(COUNTERS)
    }
}