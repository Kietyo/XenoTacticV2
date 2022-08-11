package com.xenotactic.korge.components

object MapEntityComponent

object BlockingEntityComponent

data class SizeComponent(
    val width: Int,
    val height: Int
)

val SIZE_2X2_COMPONENT = SizeComponent(2, 2)

object StartEntityComponent
object FinishEntityComponent
object RockEntityComponent