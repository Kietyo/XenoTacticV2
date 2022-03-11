package com.xenotactic.gamelogic.utils

import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration


class IntStatCounter(val name: String)  {
    var max = Long.MIN_VALUE
        private set
    var min = Long.MAX_VALUE
        private set

    var total = 0L
        private set
    var count = 0L
        private set

    val average: Double
        get() = total / count.toDouble()

    fun record(x: Int) {
        record(x.toLong())
    }

    fun record(x: Long) {
        max = max(x, max)
        min = min(x, min)
        total += x
        count++
    }

    fun add(other: IntStatCounter) {
        max = max(max, other.max)
        min = min(min, other.min)
        total += other.total
        count += other.count
    }

    fun clear() {
        max = Long.MIN_VALUE
        min = Long.MAX_VALUE
        total = 0
        count = 0
    }

    override fun toString(): String {
        return "IntStatCounter: $name, total=$total, count=$count, min=$min, " +
                "max=$max, average=$average, "
    }
}

class TimeStatCounter(val name: String) {
    val intStatCounter = IntStatCounter(name)

    val count: Long
        get() = intStatCounter.count

    fun recordNanos(nanos: Long) {
        intStatCounter.record(nanos)
    }

    @OptIn(ExperimentalTime::class)
    fun getTotal(): Duration {
        return intStatCounter.total.toDuration(DurationUnit.NANOSECONDS)
    }

    @OptIn(ExperimentalTime::class)
    fun getMin(): Duration {
        return intStatCounter.min.toDuration(DurationUnit.NANOSECONDS)
    }

    @OptIn(ExperimentalTime::class)
    fun getMax(): Duration {
        return intStatCounter.max.toDuration(DurationUnit.NANOSECONDS)
    }

    @OptIn(ExperimentalTime::class)
    fun getAverage(): Duration {
        return intStatCounter.average.toDuration(DurationUnit.NANOSECONDS)
    }

    fun clear() {
        intStatCounter.clear()
    }

    @OptIn(ExperimentalTime::class)
    fun getString(): String {
        return "TimeStatCounter: $name, total=${getTotal()}, count=${count}, " +
                "min=${getMin()}, " +
                "max=${getMax()}, average=${getAverage()}, "
    }
}