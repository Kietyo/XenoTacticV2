package com.xenotactic.testing

import com.xenotactic.ecs.StatefulEntity
import com.xenotactic.gamelogic.utils.GameUnit
import kotlin.test.assertEquals

fun assertThat(actual: GameUnit) = DoubleSubject(actual.toDouble())
fun assertThat(actual: StatefulEntity) = StatefulEntitySubject(actual)
