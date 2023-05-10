import korlibs.datastructure.iterators.parallelMap
import korlibs.io.async.launch
import korlibs.io.async.runBlockingNoJs
import korlibs.io.file.VfsFile
import com.xenotactic.gamelogic.utils.GOLDENS_DATA_VFS
import com.xenotactic.gamelogic.utils.getGoldenJsonFiles
import com.xenotactic.gamelogic.utils.toGameMap
import com.xenotactic.gamelogic.random.MapGeneratorConfiguration
import com.xenotactic.gamelogic.random.MapGeneratorResult
import com.xenotactic.gamelogic.random.RandomMapGenerator
import com.xenotactic.gamelogic.utils.toGameUnit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertIs

internal class GoldenTest {

    @Test
    fun verifyExistingGoldens() {
        getGoldenJsonFiles().parallelMap { vfsFile: VfsFile ->
            val result = vfsFile.toGameMap()?.verify()
            assertIs<MapVerificationResult.Success>(
                result,
                "Map $vfsFile failed verification. Result: $result"
            )
        }
    }

    @Test
    @Ignore
    fun createGoldens() {
        val jsonFormatter = Json {
            prettyPrint = true
        }
        val random = Random
        val numGoldensToCreate = 1000
        var numOffset = 1000
        var numGoldensCreated = 0

        while (numGoldensCreated < numGoldensToCreate) {
            val map = RandomMapGenerator.generate(
                MapGeneratorConfiguration(
                    random.nextLong(),
                    width = random.nextInt(10, 51).toGameUnit(),
                    height = random.nextInt(10, 51).toGameUnit(),
                    checkpoints = random.nextInt(0, 6),
                    rocks = random.nextInt(0, 50),
                    teleports = random.nextInt(0, 5),
                    failureAfterTotalAttempts = 1000
                )
            )
            if (map is MapGeneratorResult.Failure) continue

            val jsonData = jsonFormatter.encodeToString(map.map)

            runBlockingNoJs {
                launch {
                    val fileName = "${numOffset + numGoldensCreated}.json".padStart(10, '0')
                    println("File to write: $fileName")
                    GOLDENS_DATA_VFS()[fileName].writeString(jsonData)
                }
            }
            numGoldensCreated++
        }

    }
}