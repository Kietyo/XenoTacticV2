package korge.model.korge_test_utils

import com.soywiz.korio.file.VfsFile
import com.xenotactic.gamelogic.test_utils.generateRandomFileName

suspend fun VfsFile.createTempFile(extension: String = "txt"): VfsFile {
    while (true) {
        val tempFileName = "${generateRandomFileName()}.$extension"
        val possibleTempFile = this.get(tempFileName)
        if (!possibleTempFile.exists()) {
            println("Creating temp file: $possibleTempFile")
            return possibleTempFile
        }
    }
}