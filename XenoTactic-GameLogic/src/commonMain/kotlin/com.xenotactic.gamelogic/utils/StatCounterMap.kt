package utils

import com.xenotactic.gamelogic.utils.CounterName
import com.xenotactic.gamelogic.utils.IntStatCounter
import com.xenotactic.gamelogic.utils.TimeStatCounter
import kotlin.time.ExperimentalTime

class StatCounterMap(val name: String) {
    private val nameToIntCounterMap = mutableMapOf<String, IntStatCounter>()
    private val enumToIntCounterMap = mutableMapOf<CounterName, IntStatCounter>()
    private val nameToTimeCounter = mutableMapOf<String, TimeStatCounter>()

    fun getTimeCounter(counterName: String): TimeStatCounter {
        return nameToTimeCounter.getOrPut(counterName) { TimeStatCounter(counterName) }
    }

    fun getIntCounter(counterName: String): IntStatCounter {
        return nameToIntCounterMap.getOrPut(counterName) { IntStatCounter(counterName) }
    }

    fun getIntCounter(counter: CounterName): IntStatCounter {
        return enumToIntCounterMap.getOrPut(counter) { IntStatCounter(counter.name) }
    }

    fun clear() {
        nameToIntCounterMap.clear()
        enumToIntCounterMap.clear()
        nameToTimeCounter.clear()
    }

    @OptIn(ExperimentalTime::class)
    override fun toString(): String {
        val sb = StringBuilder()
        sb.appendLine(name)
        if (nameToIntCounterMap.size > 0) {
            sb.appendLine("nameToIntCounterMap:")
            for (counter in nameToIntCounterMap.values) {
                sb.appendLine(counter)
            }
        }
        if (enumToIntCounterMap.size > 0) {
            sb.appendLine()
            sb.appendLine("enumToIntCounterMap:")
            for (counter in enumToIntCounterMap.values) {
                sb.appendLine(counter)
            }
        }
        if (nameToTimeCounter.size > 0) {
            sb.appendLine()
            sb.appendLine("nameToTimeCounter:")
            for (counter in nameToTimeCounter.values) {
                sb.appendLine(counter.getString())
            }
        }
        return sb.toString()
    }
}