package daos

import com.soywiz.korio.async.runBlockingNoJs
import com.xenotactic.gamelogic.daos.Datastore
import kotlin.test.Test

internal class DatastoreTest {

    @Test
    fun testDatastore() = runBlockingNoJs {
        val db = Datastore()

        db.getData()
        val data = db.putData()

        println(data)
    }
}