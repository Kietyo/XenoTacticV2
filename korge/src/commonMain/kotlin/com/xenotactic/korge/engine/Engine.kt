package com.xenotactic.korge.engine

import com.xenotactic.ecs.Injections
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.models.GameWorld
import kotlin.reflect.KClass

class Engine(val eventBus: EventBus,
             val gameWorld: GameWorld = GameWorld()
) {
    val injections = Injections()
}

fun main() {
//    val engine = Engine()
//    engine[ObjectPlacementComponent::class] = ObjectPlacementComponent("snoop")
//    engine.addOneTimeComponent(ObjectPlacementComponent("blah"))
//
//    val component = engine.getOneTimeComponent<ObjectPlacementComponent>()
    println()
}