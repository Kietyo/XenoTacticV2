package com.xenotactic.testing

import com.kietyo.ktruth.DoubleSubject
import com.xenotactic.ecs.StatefulEntity
import com.xenotactic.gamelogic.utils.GameUnit

fun assertThat(actual: GameUnit) = DoubleSubject(actual.toDouble())
fun assertThat(actual: StatefulEntity) = StatefulEntitySubject(actual)
