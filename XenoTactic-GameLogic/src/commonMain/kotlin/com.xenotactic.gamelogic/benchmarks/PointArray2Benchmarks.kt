package com.xenotactic.gamelogic.benchmarks

import korlibs.datastructure.Array2
import kotlinx.benchmark.*

@State(Scope.Benchmark)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.NANOSECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
class PointArray2Benchmarks {
    private val smallPointArray2 = Array2<Boolean>(4, 4, false).apply {
        this[1, 1] = true
        this[2, 2] = true
        this[3, 3] = true
    }

    private val mediumPointArray2 = Array2<Boolean>(30, 30, false).apply {
        repeat(30) {
            this[it, it] = true
        }
    }

    @Benchmark
    fun init(): Array2<Boolean> {
        return Array2<Boolean>(4, 4, false).apply {
            this[1, 1] = true
            this[2, 2] = true
            this[3, 3] = true
        }
    }

    @Benchmark
    fun initMedium(): Array2<Boolean> {
        return Array2<Boolean>(30, 30, false).apply {
            repeat(30) {
                this[it, it] = true
            }
        }
    }

    @Benchmark
    fun addValueToImmutableSmallSet(): Array2<Boolean> {
        return smallPointArray2.clone().apply {
            this[0, 1] = true
        }
    }

    @Benchmark
    fun addValueToImmutableMediumSet(): Array2<Boolean> {
        return mediumPointArray2.clone().apply {
            this[0, 1] = true
        }
    }

    @Benchmark
    fun valueExists(): Boolean {
        return smallPointArray2[1, 1]
    }

    @Benchmark
    fun valueDontExists(): Boolean {
        return smallPointArray2[0, 0]
    }

    @Benchmark
    fun clone(): Array2<Boolean> {
        return smallPointArray2.clone()
    }

    @Benchmark
    fun cloneMedium(): Array2<Boolean> {
        return mediumPointArray2.clone()
    }
}