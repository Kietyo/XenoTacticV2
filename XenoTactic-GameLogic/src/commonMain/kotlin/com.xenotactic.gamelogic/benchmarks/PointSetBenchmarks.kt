package com.xenotactic.gamelogic.benchmarks

import com.xenotactic.gamelogic.model.IntPoint
import kotlinx.benchmark.*

@State(Scope.Benchmark)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.NANOSECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
class PointSetBenchmarks {
    private val smallPointSet = setOf<IntPoint>(
        IntPoint(1, 1),
        IntPoint(2, 2),
        IntPoint(3, 3),
    )

    private val mediumPointSet: Set<IntPoint> = mutableSetOf<IntPoint>().apply {
        repeat(30) {
            add(IntPoint(it, it))
        }
    }

    @Benchmark
    fun init(): Set<IntPoint> {
        return setOf<IntPoint>(
            IntPoint(1, 1),
            IntPoint(2, 2),
            IntPoint(3, 3),
        )
    }

    @Benchmark
    fun initMedium(): Set<IntPoint> {
        return mutableSetOf<IntPoint>().apply {
            repeat(30) {
                add(IntPoint(it, it))
            }
        }
    }

    @Benchmark
    fun addValueToImmutableSmallSet(): Set<IntPoint> {
        return smallPointSet + IntPoint(50, 50)
    }

    @Benchmark
    fun addValueToImmutableMediumSet(): Set<IntPoint> {
        return mediumPointSet + IntPoint(50, 50)
    }

    @Benchmark
    fun valueExists(): Boolean {
        return smallPointSet.contains(IntPoint(1, 1))
    }

    @Benchmark
    fun valueDontExists(): Boolean {
        return smallPointSet.contains(IntPoint(0, 0))
    }

    @Benchmark
    fun clone(): Set<IntPoint> {
        return smallPointSet.toSet()
    }

    @Benchmark
    fun cloneMedium(): Set<IntPoint> {
        return mediumPointSet.toSet()
    }
}