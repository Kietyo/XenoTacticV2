package random

import com.soywiz.korio.lang.assert
import kotlin.test.Test

internal class BinomialDistributionTest {

    @Test
    fun nextInt1() {
        repeat(10000) {
            val randomInt = BinomialDistribution.nextInt(0, 1)
            assert(randomInt == 0 || randomInt == 1) {
                "Expected 0, got $randomInt"
            }
        }
    }

    @Test
    fun nextInt2() {
        repeat(10000) {
            val randomInt = BinomialDistribution.nextInt(0, 2)
            assert(randomInt == 0 || randomInt == 1 || randomInt == 2)
        }
    }

    @Test
    fun nextInt3() {
        val frequencyCount = mutableMapOf<Int, Int>()
        repeat(100000) {
            val randomInt = BinomialDistribution.nextInt(0, 10)
            val currCount = frequencyCount.getOrElse(randomInt) { 0 }
            frequencyCount[randomInt] = currCount + 1
        }

        val sortedKeys = frequencyCount.keys.sorted()
        for (key in sortedKeys) {
            println("$key=${frequencyCount[key]}")
        }
    }
}