package com.xenotactic.korge.events

import MapVerificationResult
import com.soywiz.korge.view.View
import com.soywiz.korio.file.VfsFile
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.korge.ecomponents.GoalData

object EscapeButtonActionEvent

data class UpdatedPathLineEvent(
    val pathSequence: PathSequence?,
    val newPathLength: Double?)

data class RemovedEntityEvent(val entity: MapEntity)

data class AddEntityEvent(val entity: MapEntity)

data class UpdatedGoalDataEvent(val data: GoalData)

object LeftControlAndMinus

object LeftControlAndEqual

object SpawnCreepEvent

data class PlayMapEvent(val gameMap: GameMap)

object ExitGameSceneEvent

object PointerActionChangeEvent

data class GoldensEntryClickEvent(
    val gameMap: GameMap
)

data class GoldensEntryHoverOnEvent(
    val mapFile: VfsFile,
    val gameMap: GameMap,
    val verificationResult: MapVerificationResult
)

object GoldensEntryHoverOutEvent

data class UIEntityClickedEvent(
    val view: View,
    val entity: MapEntity
)

data class ResizeMapEvent(
    val oldMapWidth: Int,
    val oldMapHeight: Int,
    val newMapWidth: Int,
    val newMapHeight: Int
)