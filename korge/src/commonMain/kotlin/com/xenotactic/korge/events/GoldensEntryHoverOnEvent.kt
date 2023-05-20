package com.xenotactic.korge.events

import MapVerificationResult
import com.xenotactic.gamelogic.model.GameMap
import korlibs.io.file.VfsFile

data class GoldensEntryHoverOnEvent(
    val mapFile: VfsFile,
    val gameMap: GameMap,
    val verificationResult: MapVerificationResult
)