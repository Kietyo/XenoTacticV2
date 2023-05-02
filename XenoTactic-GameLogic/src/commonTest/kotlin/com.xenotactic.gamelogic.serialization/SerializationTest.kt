package com.xenotactic.gamelogic.serialization

import com.xenotactic.ecs.StagingEntity
import com.xenotactic.gamelogic.components.BottomLeftPositionComponent
import com.xenotactic.gamelogic.components.SizeComponent
import com.xenotactic.gamelogic.model.SerializableComponentI2
import com.xenotactic.gamelogic.model.SerializableComponents
import com.xenotactic.gamelogic.model.StableEntity
import com.xenotactic.gamelogic.model.StableEntityV3
import com.xenotactic.gamelogic.utils.toGameUnit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.test.Test

internal class SerializationTest {

    fun StagingEntity.toStableEntity(): StableEntity {
        return StableEntity(
            listOf())
    }

//    fun StagingEntity.toStableEntity2(): StableEntityV2 {
//        val bottomLeftPositionComponent = get(BottomLeftPositionComponent::class)
//        return StableEntityV2(listOf(bottomLeftPositionComponent as SerializableComponentI<Any>))
//    }

    fun StagingEntity.toStableEntity3(): StableEntityV3 {
        val bottomLeftPositionComponent = get(BottomLeftPositionComponent::class)
        val sizeComponent = get(SizeComponent::class)
        return StableEntityV3.create(listOf(sizeComponent, bottomLeftPositionComponent))
    }

    @Test
    fun testSerialization() {

        val entity = StableEntity(
            listOf(SerializableComponents.BottomLeftPositionComponent(1.toGameUnit(), 2.toGameUnit())),
        )

        val json = Json

        println(entity)
        println(json.encodeToString(entity))
    }

    @Test
    fun testSerialization2() {
        val stagingEntity = StagingEntity {
            addComponentOrThrow(BottomLeftPositionComponent(1, 2))
            addComponentOrThrow(SizeComponent(5, 5))
        }

        val entity = stagingEntity.toStableEntity()

        val json = Json

        println(entity)
        println(json.encodeToString(entity))
    }

//    @Test
//    fun testSerialization3() {
//        val stagingEntity = StagingEntity {
//            addComponentOrThrow(BottomLeftPositionComponent(1, 2))
//            addComponentOrThrow(SizeComponent(5, 5))
//        }
//
//        val entity = stagingEntity.toStableEntity2()
//
//        val json = Json {
//            serializersModule = SerializersModule {
//                polymorphic(SerializableComponentI::class) {
//                    subclass(BottomLeftPositionComponent::class)
//                }
//            }
//        }
//
//        println(entity)
//        println(json.encodeToString(entity))
//    }

    @Test
    fun testSerialization4() {
        val stagingEntity = StagingEntity {
            addComponentOrThrow(BottomLeftPositionComponent(1, 2))
            addComponentOrThrow(SizeComponent(5, 5))
        }

        val entity = stagingEntity.toStableEntity3()

        val json = Json {
            serializersModule = SerializersModule {
                polymorphic(SerializableComponentI2::class) {
                    subclass(BottomLeftPositionComponent::class)
                    subclass(SizeComponent::class)
                }
            }
        }

        println(entity)
        println(json.encodeToString(entity))


        val map = mutableMapOf<String, String>()
    }
}