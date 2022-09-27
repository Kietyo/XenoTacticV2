package com.xenotactic.gamelogic.benchmarks

import com.xenotactic.gamelogic.model.GameUnitPoint
import kotlinx.benchmark.*

@State(Scope.Benchmark)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.NANOSECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
class PointSetBenchmarks {
    private val smallPointSet = setOf<GameUnitPoint>(
        GameUnitPoint(1, 1),
        GameUnitPoint(2, 2),
        GameUnitPoint(3, 3),
    )

    private val mediumPointSet: Set<GameUnitPoint> = mutableSetOf<GameUnitPoint>().apply {
        repeat(30) {
            add(GameUnitPoint(it, it))
        }
    }

    @Benchmark
    fun init(): Set<GameUnitPoint> {
        return setOf<GameUnitPoint>(
            GameUnitPoint(1, 1),
            GameUnitPoint(2, 2),
            GameUnitPoint(3, 3),
        )
    }

    @Benchmark
    fun initMedium(): Set<GameUnitPoint> {
        return mutableSetOf<GameUnitPoint>().apply {
            repeat(30) {
                add(GameUnitPoint(it, it))
            }
        }
    }

    @Benchmark
    fun addValueToImmutableSmallSet(): Set<GameUnitPoint> {
        return smallPointSet + GameUnitPoint(50, 50)
    }

    @Benchmark
    fun addValueToImmutableMediumSet(): Set<GameUnitPoint> {
        return mediumPointSet + GameUnitPoint(50, 50)
    }

    @Benchmark
    fun valueExists(): Boolean {
        return smallPointSet.contains(GameUnitPoint(1, 1))
    }

    @Benchmark
    fun valueDontExists(): Boolean {
        return smallPointSet.contains(GameUnitPoint(0, 0))
    }

    @Benchmark
    fun clone(): Set<GameUnitPoint> {
        return smallPointSet.toSet()
    }

    @Benchmark
    fun cloneMedium(): Set<GameUnitPoint> {
        return mediumPointSet.toSet()
    }
}