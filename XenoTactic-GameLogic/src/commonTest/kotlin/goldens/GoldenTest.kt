import com.soywiz.kds.iterators.parallelMap
import com.soywiz.korio.async.launch
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.file.VfsFile
import com.xenotactic.gamelogic.korge_utils.GOLDENS_DATA_VFS
import com.xenotactic.gamelogic.korge_utils.getGoldenJsonFiles
import com.xenotactic.gamelogic.korge_utils.toGameMap
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import random.MapGeneratorConfiguration
import random.MapGeneratorResult
import random.RandomMapGenerator
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
                    width = random.nextInt(10, 51),
                    height = random.nextInt(10, 51),
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