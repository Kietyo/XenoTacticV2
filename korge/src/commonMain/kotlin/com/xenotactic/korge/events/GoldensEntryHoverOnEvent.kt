package com.xenotactic.korge.events

import MapVerificationResult
import korlibs.io.file.VfsFile
import com.xenotactic.gamelogic.model.GameMap

data class GoldensEntryHoverOnEvent(
    val mapFile: VfsFile,
    val gameMap: GameMap,
    val verificationResult: MapVerificationResult
)