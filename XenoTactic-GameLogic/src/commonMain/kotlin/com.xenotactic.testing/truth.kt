package com.xenotactic.testing

import com.xenotactic.gamelogic.utils.GameUnit

fun assertThat(v: Double) = DoubleSubject(v)
fun assertThat(v: GameUnit) = DoubleSubject(v.toDouble())