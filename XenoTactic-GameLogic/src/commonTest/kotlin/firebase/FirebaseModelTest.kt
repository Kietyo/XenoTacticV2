package firebase

import com.xenotactic.gamelogic.firebase_models.FbMapData
import com.xenotactic.gamelogic.firebase_models.FbMapEntry
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

internal class FirebaseModelTest {

    val jsonPrettyPrint = Json { prettyPrint = true }

    @Test
    fun encodeFbMapDataToString() {
        val mapData = FbMapData(
            mutableMapOf(
                "a6c00d68a95a5043374c1017b982d8f0b3009179" to FbMapEntry(
                    GameMap.create(
                        10, 10,
                        MapEntity.Start(2, 2),
                        MapEntity.Finish(4, 4),
                        MapEntity.Checkpoint(0, 7, 5),
                        MapEntity.Checkpoint(1, 5, 7),
                        MapEntity.TeleportIn(0, 8, 3),
                        MapEntity.TeleportOut(0, 6, 3),
                        MapEntity.ROCK_2X4,
                    ).toFbGameMap(),
                    123456789
                )
            )
        )

        val jsonString = Json.encodeToString(mapData)

        println(jsonString)
    }

    @Test
    fun decodeFbMapDataFromString1() {
        val str = """
        {"data": {
                "a6c00d68a95a5043374c1017b982d8f0b3009179": {
                    "data":{
                        "height":10,
                        "width":10
                    },
                    "timestamp" : 123456789
                }
            }
        }
        """.trimIndent()

        val map = Json.decodeFromString<FbMapData>(str)

        println(map)
    }
//
//    @Test
//    fun decodeFbMapDataFromString2() {
//        val str = """
//            {"data":{
//            "a6c00d68a95a5043374c1017b982d8f0b3009179":
//            {"data":{"000_width":13,"001_height":17,
//            "002_checkpoints":[{"sequenceNumber":0,"x":7,"y":5},{"sequenceNumber":1,"x":5,"y":7}],
//            "003_teleportIns":[{"sequenceNumber":0,"x":8,"y":3},{"sequenceNumber":1,"x":3,"y":14},{"sequenceNumber":2,"x":11,"y":15}],
//            "004_teleportOuts":[{"sequenceNumber":0,"x":6,"y":6},{"sequenceNumber":1,"x":3,"y":0},{"sequenceNumber":2,"x":2,"y":6}],"006_rocks":[{"height":2,"width":4,"x":0,"y":11},{"height":2,"width":4,"x":5,"y":3},{"height":2,"width":4,"x":6,"y":4},{"height":4,"width":2,"x":4,"y":12},{"height":2,"width":4,"x":4,"y":14},{"height":2,"width":4,"x":2,"y":12},{"height":2,"width":4,"x":6,"y":4},{"height":4,"width":2,"x":0,"y":3},{"height":4,"width":2,"x":5,"y":8},{"height":2,"width":4,"x":2,"y":2},{"height":4,"width":2,"x":1,"y":13},{"height":2,"width":4,"x":5,"y":2},{"height":2,"width":4,"x":1,"y":2},{"height":4,"width":2,"x":7,"y":1},{"height":4,"width":2,"x":1,"y":11},{"height":4,"width":2,"x":7,"y":1},{"height":4,"width":2,"x":0,"y":13},{"height":2,"width":4,"x":1,"y":3},{"height":2,"width":4,"x":4,"y":15},{"height":4,"width":2,"x":11,"y":10},{"height":2,"width":4,"x":9,"y":12},{"height":4,"width":2,"x":11,"y":12},{"height":2,"width":4,"x":1,"y":9},{"height":4,"width":2,"x":8,"y":4},{"height":4,"width":2,"x":5,"y":9},{"height":2,"width":4,"x":3,"y":10}]},"timestamp":1648783827585}}}
//        """.trimIndent()
//
//        val map = Json.decodeFromString<FbMapData>(str)
//
//        println(map)
//    }
}