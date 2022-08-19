package com.xenotactic.korge.engine

import com.xenotactic.ecs.Injections
import com.xenotactic.ecs.World
import com.xenotactic.korge.events.EventBus
import kotlin.reflect.KClass

class Engine(val eventBus: EventBus,
             val gameWorld: World = World()
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