package random

import com.soywiz.korma.random.nextDoubleInclusive
import kotlin.random.Random

class RestrictionGenerator {
    fun generate() {
        val random = Random
        random.nextDoubleInclusive()
    }
}