package daos

import com.xenotactic.gamelogic.daos.Datastore
import kotlin.test.Test

internal class DatastoreTest {

    @Test
    fun testDatastore() {
        val db = Datastore()

        db.getData()
//        db.putData()
    }
}