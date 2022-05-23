package globals

import utils.StatCounterMap

val COUNTERS = StatCounterMap("Global Counters")

enum class CounterName {
    calculateShortestPointFromCircleToPointCacheCacheHit,
    checkContainsKey
}