package daos

import com.soywiz.kds.iterators.parallelMap
import com.soywiz.korio.async.launch
import com.soywiz.korio.async.runBlockingNoJs
import com.xenotactic.gamelogic.daos.DatastoreKorge
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.test_utils.getAllGoldenMaps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlin.test.Test

internal class DatastoreKorgeTest {

    @Test
    fun getData() = runBlockingNoJs {
        val data = DatastoreKorge.getData()

        println(data)
        println(data.content.toString())
        println(data.rawContent.toString())
        println(data.readAllString())

    }

    @Test
    fun getData2() = runBlockingNoJs {
        val data = DatastoreKorge.getData2()

        println(data)
        println(data.content.toString())
        println(data.rawContent.toString())
        println(data.readAllString())

    }

    @Test
    fun headData() = runBlockingNoJs {
        val data = DatastoreKorge.headData()

        println(data)
        println(data.content.toString())
        println(data.rawContent.toString())
        println(data.readAllString())

    }

    @Test
    fun putData() = runBlockingNoJs {
        val data = DatastoreKorge.putData()

        println(data)
    }

    @Test
    fun putData2() = runBlockingNoJs {
        val data = DatastoreKorge.putData2()

        println(data)
        println(data.toString())
    }

    @Test
    fun putData3() = runBlockingNoJs {
        val data = DatastoreKorge.putData3()

        println(data)
        println(data.toString())
    }

    @Test
    fun mapExists() = runBlockingNoJs {
        val map = getAllGoldenMaps().first()

        val data = DatastoreKorge.mapExists(map)

        println("data: $data")
        //        println("data.statusText: ${data.statusText}")
        //        println("data.readAllString(): ${data.readAllString()}")
    }

    val TEST_GAME_MAP = GameMap.create(
        100, 100,
        MapEntity.Start(1, 1),
        MapEntity.Finish(3, 3),
        MapEntity.CheckPoint(0, 5, 5),
        MapEntity.TeleportIn(0, 7, 7),
        MapEntity.TeleportOut(0, 9, 9),
        MapEntity.Tower(11, 11),
        MapEntity.Rock(13, 13, 2, 2),
        MapEntity.SmallBlocker(15, 15),
        MapEntity.SpeedArea(17, 17, 2, 0.5)
    )

    @Test
    fun addMapIfNotExists2() = runBlockingNoJs {
        DatastoreKorge.addMapIfNotExists(TEST_GAME_MAP)

        DatastoreKorge.getMapEntry(TEST_GAME_MAP)

        Unit
//        DatastoreKorge.deleteMap(gameMap)
    }

    @Test
    fun deleteMap() = runBlockingNoJs {
        DatastoreKorge.deleteMap(TEST_GAME_MAP)
    }

    @Test
    fun addMapIfNotExists() = runBlockingNoJs {

        val jobs = getAllGoldenMaps().parallelMap {
            launch(Dispatchers.Default) {
                DatastoreKorge.addMapIfNotExists(it)
            }
        }

        jobs.joinAll()

        //        getAllGoldenMaps().parallelMap {
        //            launch(Dispatchers.Default) {
        //                DatastoreKorge.addMapIfNotExists(it)
        //            }
        //        }

        //        for (map in getAllGoldenMaps()) {
        //            DatastoreKorge.addMapIfNotExists(map)
        //        }

        //        val map = loadGameMapFromGoldensBlocking("00005.json")
        //        DatastoreKorge.addMapIfNotExists(map)

        //        println(data)
        //        println(data.content.toString())
        //        println(data.rawContent.toString())
        //        println(data.readAllString())
    }

    @Test
    fun getAllMapData() = runBlockingNoJs {
        val data = DatastoreKorge.getAllMapData()

        println("data: $data")
        //        println("data.readAllString(): ${data.readAllString()}")
        //        println("data.readAllString(): ${data.readAllString()}")
    }

    @Test
    fun deleteAllMaps() = runBlockingNoJs {
        DatastoreKorge.deleteAllMaps()
    }
}