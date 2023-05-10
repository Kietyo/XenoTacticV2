package com.xenotactic.gamelogic.utils

import com.xenotactic.ecs.Injections
import com.xenotactic.ecs.TypedInjections
import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.model.GameWorld

interface EventListener
interface State

class Engine(val eventBus: EventBus,
             val gameWorld: GameWorld = GameWorld()
) {
    val eventListeners = mutableListOf<EventListener>()
    val stateInjections = TypedInjections<State>()
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