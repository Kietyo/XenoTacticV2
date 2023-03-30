package daos

import korlibs.datastructure.iterators.parallelMap
import korlibs.io.async.launch
import korlibs.io.async.runBlockingNoJs
import com.xenotactic.gamelogic.daos.DatastoreKorge
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.test_utils.getAllGoldenMaps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class DatastoreKorgeTest {

    val datastoreKorge = DatastoreKorge()

    @Test
    fun getData() = runBlockingNoJs {
        val data = datastoreKorge.getData()

        println(data)
        println(data.content.toString())
        println(data.rawContent.toString())
        println(data.readAllString())

    }

    @Test
    fun getData2() = runBlockingNoJs {
        val data = datastoreKorge.getData2()

        println(data)
        println(data.content.toString())
        println(data.rawContent.toString())
        println(data.readAllString())

    }

    @Test
    fun headData() = runBlockingNoJs {
        val data = datastoreKorge.headData()

        println(data)
        println(data.content.toString())
        println(data.rawContent.toString())
        println(data.readAllString())

    }

    @Test
    fun putData() = runBlockingNoJs {
        val data = datastoreKorge.putData()

        println(data)
    }

    @Test
    fun putData2() = runBlockingNoJs {
        val data = datastoreKorge.putData2()

        println(data)
        println(data.toString())
    }

    @Test
    fun putData3() = runBlockingNoJs {
        val data = datastoreKorge.putData3()

        println(data)
        println(data.toString())
    }

    @Test
    fun mapExists() = runBlockingNoJs {
        val map = getAllGoldenMaps().first()

        val data = datastoreKorge.mapExists(map)

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
        datastoreKorge.addMapIfNotExists(TEST_GAME_MAP)

        datastoreKorge.getMapEntry(TEST_GAME_MAP)

        Unit
//        datastoreKorge.deleteMap(gameMap)
    }

    @Test
    fun deleteMap() = runBlockingNoJs {
        datastoreKorge.deleteMap(TEST_GAME_MAP)
    }

    @Test
    fun addMapIfNotExists() = runBlockingNoJs {

        val jobs = getAllGoldenMaps().parallelMap {
            launch(Dispatchers.Default) {
                datastoreKorge.addMapIfNotExists(it)
            }
        }

        jobs.joinAll()

        //        getAllGoldenMaps().parallelMap {
        //            launch(Dispatchers.Default) {
        //                datastoreKorge.addMapIfNotExists(it)
        //            }
        //        }

        //        for (map in getAllGoldenMaps()) {
        //            datastoreKorge.addMapIfNotExists(map)
        //        }

        //        val map = loadGameMapFromGoldensBlocking("00005.json")
        //        datastoreKorge.addMapIfNotExists(map)

        //        println(data)
        //        println(data.content.toString())
        //        println(data.rawContent.toString())
        //        println(data.readAllString())
    }

    @Test
    fun getAllMapData() = runBlockingNoJs {
        val data = datastoreKorge.getAllMapData()

        println("data: $data")
        //        println("data.readAllString(): ${data.readAllString()}")
        //        println("data.readAllString(): ${data.readAllString()}")
    }

    @Test
    fun deleteAllMaps() = runBlockingNoJs {
        datastoreKorge.deleteAllMaps()
    }
}