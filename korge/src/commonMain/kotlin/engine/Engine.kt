package engine

import com.soywiz.korge.bus.Bus
import events.EventBus
import events.EventBusInterface
import kotlin.reflect.KClass

class Engine(val eventBus: EventBus) {
    val oneTimeComponents = mutableMapOf<KClass<out EComponent>, Any>()

    fun <T: EComponent> setOneTimeComponent(obj: T) {
        this[obj::class] = obj
    }

    fun <T: EComponent> setOneTimeComponentIfNotExists(obj: T) {
        if (!oneTimeComponents.containsKey(obj::class)) {
            this[obj::class] = obj
        }
    }

    operator fun <T : EComponent> set(kClass: KClass<out EComponent>, obj: T) {
        oneTimeComponents[kClass] = obj
        println(oneTimeComponents)
    }

    inline fun <reified T: EComponent> getOneTimeComponent(): T {
        return (oneTimeComponents[T::class] as T?)!!
    }

    inline fun <reified T: EComponent> getOneTimeComponentNullable(): T? {
        return oneTimeComponents[T::class] as T?
    }
}

fun main() {
//    val engine = Engine()
//    engine[ObjectPlacementComponent::class] = ObjectPlacementComponent("snoop")
//    engine.addOneTimeComponent(ObjectPlacementComponent("blah"))
//
//    val component = engine.getOneTimeComponent<ObjectPlacementComponent>()
    println()
}