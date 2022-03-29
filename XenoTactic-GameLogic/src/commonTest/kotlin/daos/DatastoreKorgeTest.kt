package daos

import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.dynamic.mapper.Mapper
import com.soywiz.korio.dynamic.mapper.ObjectMapper
import com.soywiz.korio.dynamic.serialization.stringifyTyped
import com.soywiz.korio.lang.IOException
import com.soywiz.korio.net.http.Http
import com.soywiz.korio.serialization.json.Json
import com.soywiz.korio.stream.openAsync
import com.xenotactic.gamelogic.daos.DatastoreKorge
import kotlin.test.Test

internal class DatastoreKorgeTest {

    @Test
    fun getData() = runBlockingNoJs {
        val datastore = DatastoreKorge()

        val data = datastore.getData()

        println(data)
        println(data.content.toString())
        println(data.rawContent.toString())
        println(data.readAllString())

    }

    @Test
    fun headData() = runBlockingNoJs {
        val datastore = DatastoreKorge()

        val data = datastore.headData()

        println(data)
        println(data.content.toString())
        println(data.rawContent.toString())
        println(data.readAllString())

    }

    @Test
    fun putData() = runBlockingNoJs {
        val datastore = DatastoreKorge()

        val data = datastore.putData()

        println(data)
    }

    @Test
    fun putData2() = runBlockingNoJs {
        val datastore = DatastoreKorge()

        val data = datastore.putData2()

        println(data)
        println(data.toString())
    }
}

suspend fun request(method: Http.Method, path: String, request: Any?, mapper: ObjectMapper = Mapper): Any {
    val requestContent = request?.let {
        when (it) {
            is RawString -> ...
            else -> Json.stringifyTyped(it, mapper)
        }
    }
    val result = endpoint.request(
        method,
        path,
        content = requestContent?.openAsync(),
        headers = Http.Headers(
            Http.Headers.ContentType to "application/json"
        )
    )
    result.checkErrors()
    val stringResult = result.readAllString()
    //println(stringResult)
    return try {
        Json.parse(stringResult) ?: mapOf<String, String>()
    } catch (e: IOException) {
        mapOf<String, String>()
    }
}