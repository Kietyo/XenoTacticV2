package engine

import components.ObjectPlacementComponent
import kotlin.reflect.KClass

class Engine {

    val oneTimeComponents = mutableMapOf<KClass<out Component>, Any>()

    fun <T: Component> setOneTimeComponent(obj: T) {
        this[obj::class] = obj
    }

    fun <T: Component> setOneTimeComponentIfNotExists(obj: T) {
        if (!oneTimeComponents.containsKey(obj::class)) {
            this[obj::class] = obj
        }
    }

    operator fun <T : Component> set(kClass: KClass<out Component>, obj: T) {
        oneTimeComponents[kClass] = obj
        println(oneTimeComponents)
    }

    inline fun <reified T: Component> getOneTimeComponent(): T {
        return (oneTimeComponents[T::class] as T?)!!
    }

    inline fun <reified T: Component> getOneTimeComponentNullable(): T? {
        return oneTimeComponents[T::class] as T?
    }
}

fun main() {
    val engine = Engine()
//    engine[ObjectPlacementComponent::class] = ObjectPlacementComponent("snoop")
//    engine.addOneTimeComponent(ObjectPlacementComponent("blah"))
//
//    val component = engine.getOneTimeComponent<ObjectPlacementComponent>()
    println()
}