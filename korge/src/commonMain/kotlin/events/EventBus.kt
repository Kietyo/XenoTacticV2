package events

import com.soywiz.klogger.Logger
import com.soywiz.korge.bus.GlobalBus
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korio.lang.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
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