package com.xenotactic.gamelogic.benchmarks

import com.xenotactic.gamelogic.model.GameUnitTuple
import kotlinx.benchmark.*

@State(Scope.Benchmark)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.NANOSECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
class PointSetBenchmarks {
    private val smallPointSet = setOf<GameUnitTuple>(
        GameUnitTuple(1, 1),
        GameUnitTuple(2, 2),
        GameUnitTuple(3, 3),
    )

    private val mediumPointSet: Set<GameUnitTuple> = mutableSetOf<GameUnitTuple>().apply {
        repeat(30) {
            add(GameUnitTuple(it, it))
        }
    }

    @Benchmark
    fun init(): Set<GameUnitTuple> {
        return setOf<GameUnitTuple>(
            GameUnitTuple(1, 1),
            GameUnitTuple(2, 2),
            GameUnitTuple(3, 3),
        )
    }

    @Benchmark
    fun initMedium(): Set<GameUnitTuple> {
        return mutableSetOf<GameUnitTuple>().apply {
            repeat(30) {
                add(GameUnitTuple(it, it))
            }
        }
    }

    @Benchmark
    fun addValueToImmutableSmallSet(): Set<GameUnitTuple> {
        return smallPointSet + GameUnitTuple(50, 50)
    }

    @Benchmark
    fun addValueToImmutableMediumSet(): Set<GameUnitTuple> {
        return mediumPointSet + GameUnitTuple(50, 50)
    }

    @Benchmark
    fun valueExists(): Boolean {
        return smallPointSet.contains(GameUnitTuple(1, 1))
    }

    @Benchmark
    fun valueDontExists(): Boolean {
        return smallPointSet.contains(GameUnitTuple(0, 0))
    }

    @Benchmark
    fun clone(): Set<GameUnitTuple> {
        return smallPointSet.toSet()
    }

    @Benchmark
    fun cloneMedium(): Set<GameUnitTuple> {
        return mediumPointSet.toSet()
    }
}