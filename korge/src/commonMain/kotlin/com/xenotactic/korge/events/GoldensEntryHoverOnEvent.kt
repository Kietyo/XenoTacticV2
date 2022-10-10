package com.xenotactic.korge.events

import MapVerificationResult
import com.soywiz.korio.file.VfsFile
import com.xenotactic.gamelogic.model.GameMap

data class GoldensEntryHoverOnEvent(
    val mapFile: VfsFile,
    val gameMap: GameMap,
    val verificationResult: MapVerificationResult
)