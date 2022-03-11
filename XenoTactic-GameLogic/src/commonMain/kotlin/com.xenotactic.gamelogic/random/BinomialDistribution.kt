package random

import kotlin.random.Random

object BinomialDistribution {
    data class Config(val n: Int, val p: Double)

    val DEFAULT_CONFIG = Config(1000, 0.5)

    /**
     * Simulates `n` coin flips where each flip has a `p` probability of landing on heads.
     * Returns the number of coin flips that returned heads.
     *
     * n: Number of trials to run
     * p: Probability of heads. A double between [0 (inclusive), 1, (exclusive))
     */
    fun simulate(n: Int, p: Double, random: Random = Random): Int {
        require(p >= 0.0 && p < 1.0)
        var numHeads = 0
        repeat(n) {
            if (random.nextDouble(0.0, 1.0) <= p) {
                numHeads++
            }
        }
        return numHeads
    }

    fun simulate(config: Config, random: Random = Random): Int {
        return simulate(config.n, config.p, random)
    }

    /**
     * Returns an int that is binomially distributed between
     * `start` (inclusive) to `end` (inclusve).
     */
    fun nextInt(start: Int, end: Int, p: Double = 0.5, random: Random = Random):
            Int {
        require(end > start) {
            "`until` should be greater than `from`! Got: from: $start, until: $end"
        }
        val diff = end - start
        val n = diff
        val binomialVar = simulate(n, p, random)
        val binomialDiff = (binomialVar / n.toDouble()) * diff

        return start + binomialDiff.toInt()
    }
}