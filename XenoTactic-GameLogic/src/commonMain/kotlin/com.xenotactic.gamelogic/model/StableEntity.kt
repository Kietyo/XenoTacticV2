package com.xenotactic.gamelogic.model

import com.xenotactic.gamelogic.utils.GameUnit
import kotlinx.serialization.Serializable

@Serializable
sealed class SerializableComponents {

    @Serializable
    data class BottomLeftPositionComponent(
        val x: GameUnit, val y: GameUnit
    ): SerializableComponents()

}

//@Serializable(with = SerializableComponentISerializer::class)
//interface SerializableComponentI<T: Any> {
//    val klassName: String
//    val data: T
//}

interface SerializableComponentI2

//class SerializableComponentISerializer<T : Any>(
//    private val dataSerializer: KSerializer<T>
//) : KSerializer<SerializableComponentI<T>> {
//    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
//        "SerializedComponent"
//    ) {
//        element<String>("TypeName")
//        this.element("Data", dataSerializer.descriptor)
//    }
//
//    override fun deserialize(decoder: Decoder): SerializableComponentI<T> {
//        TODO("Not yet implemented")
//    }
//
//    override fun serialize(encoder: Encoder, value: SerializableComponentI<T>) {
//        encoder.encodeString(value.klassName)
//        dataSerializer.serialize(encoder, value.data)
//    }
//
//}

@Serializable
data class StableEntity(
    val components: List<SerializableComponents>) {
    init {
        components.sortedBy {
            it::class.simpleName
        }
    }
}

//@Serializable
//data class StableEntityV2(
//    val objs: List<SerializableComponentI<@Polymorphic Any>>
//)

@Serializable
data class StableEntityV3 private constructor(
    val objs: List<SerializableComponentI2>
) {

    companion object {
        fun create(objs: List<SerializableComponentI2>): StableEntityV3 {
            // TODO: Make this sorted. Can't used `it::class.qualifiedName`
            // because reflection api not supported yet in JS.
//            return StableEntityV3(objs.sortedBy { it::class.qualifiedName })
            return StableEntityV3(objs)
        }
    }
}