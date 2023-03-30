package random

import korlibs.math.random.nextDoubleInclusive
import kotlin.random.Random

class RestrictionGenerator {
    fun generate() {
        val random = Random
        random.nextDoubleInclusive()
    }
}