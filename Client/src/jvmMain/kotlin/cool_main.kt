import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.xenotactic.gamelogic.daos.Datastore
import com.xenotactic.gamelogic.daos.DatastoreKorge
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import kotlinx.coroutines.runBlocking
import java.io.FileInputStream
import java.util.*


fun main() {

    //    val refreshToken = FileInputStream("C:\\Users\\kietm\\Downloads\\service-account-info.json")
    //
    //    val options = FirebaseOptions.builder()
    //        .setCredentials(GoogleCredentials.fromStream(refreshToken))
    //        .setDatabaseUrl("https://xenotactic-default-rtdb.firebaseio.com/")
    //        .build()
    //
    //    val app = FirebaseApp.initializeApp(options)
    //
    //    val database = FirebaseDatabase.getInstance(app)
    //
    //    val ref = database.getReference("users")
    //
    //    println(ref)
    //
    //    val wat = ref.limitToFirst(10)
    //
    //    println(wat.spec)
    //    println(wat.repo)
    //    println(wat.path)

    //    val token = FirebaseAuth.getInstance(app).createCustomToken("blahblah")


    // Load the service account key JSON file
    val serviceAccount = FileInputStream("C:\\Users\\kietm\\Downloads\\service-account-info.json")

    // Authenticate a Google credential with the service account
    val googleCred = GoogleCredential.fromStream(serviceAccount)

    // Add the required scopes to the Google credential
    val scoped = googleCred.createScoped(
        Arrays.asList(
            "https://www.googleapis.com/auth/firebase.database",
            "https://www.googleapis.com/auth/userinfo.email"
        )
    )

    // Use the Google credential to generate an access token
    scoped.refreshToken()
    val token = scoped.accessToken

    println(token)


    // See the "Using the access token" section below for information
    // on how to use the access token to send authenticated requests to the
    // Realtime Database REST API.

    val datastore = Datastore(token)

    runBlocking {
        println(datastore.getData())

        datastore.addMapIfNotExists(
            GameMap.create(
                10, 10,
                MapEntity.Start(0, 0),
                MapEntity.Finish(5, 5)
            )
        )

    }

}