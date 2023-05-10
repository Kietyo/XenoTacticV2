package com.xenotactic.gamelogic.events

import korlibs.datastructure.iterators.fastForEach
import korlibs.io.async.launchImmediately
import korlibs.io.async.launchUnscoped
import korlibs.io.lang.Closeable
import korlibs.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

interface EventBusInterface {
    fun send(message: Any)
    fun <T : Any> register(clazz: KClass<out T>, handler: suspend (T) -> Unit): Closeable
}

object DummyEventBus : EventBusInterface {
    override fun send(message: Any) = Unit
    override fun <T : Any> register(clazz: KClass<out T>, handler: suspend (T) -> Unit): Closeable =
        object : Closeable {
            override fun close() {
                return
            }
        }
}

class GlobalBus(
    val coroutineContext: CoroutineContext
) {
    private val perClassHandlers = HashMap<KClass<*>, ArrayList<suspend (Any) -> Unit>>()

    suspend fun send(message: Any) {
        val clazz = message::class
        perClassHandlers[clazz]?.fastForEach { handler ->
            handler(message)
        }
    }

    fun sendAsync(message: Any, coroutineContext: CoroutineContext = this.coroutineContext) {
        coroutineContext.launchUnscoped { send(message) }
    }

    private fun forClass(clazz: KClass<*>) = perClassHandlers.getOrPut(clazz) { arrayListOf() }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> register(clazz: KClass<out T>, handler: suspend (T) -> Unit): Closeable {
        val chandler = handler as (suspend (Any) -> Unit)
        forClass(clazz).add(chandler)
        return Closeable {
            unregister(clazz, chandler)
        }
    }

    fun <T : Any> unregister(clazz: KClass<out T>, handler: suspend (T) -> Unit) {
        forClass(clazz).remove(handler)
    }

    inline fun <reified T : Any> register(noinline handler: suspend (T) -> Unit): Closeable {
        return register(T::class, handler)
    }
}

/**
 * Global event bus which distributes events to registered receivers.
 */
class EventBus(private val scope: CoroutineScope) : EventBusInterface {

    private val globalBus = GlobalBus(scope.coroutineContext)

    override fun send(message: Any) {
        scope.launchImmediately {
            globalBus.send(message)
        }
    }

    override fun <T : Any> register(clazz: KClass<out T>, handler: suspend (T) -> Unit): Closeable {
        return globalBus.register(clazz, handler)
    }

    inline fun <reified T : Any> register(noinline handler: suspend (T) -> Unit): Closeable {
        return register(T::class, handler)
    }

    companion object {
        val logger = Logger<EventBus>()
    }
}