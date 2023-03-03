package com.xenotactic.korge.ui

import MapVerificationResult
import com.soywiz.klogger.Logger
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOver
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.ScalingOption
import com.soywiz.korge.view.Text
import com.soywiz.korge.view.alignBottomToBottomOf
import com.soywiz.korge.view.alignLeftToLeftOf
import com.soywiz.korge.view.alignRightToRightOf
import com.soywiz.korge.view.alignTopToBottomOf
import com.soywiz.korge.view.roundRect
import com.soywiz.korge.view.scaleWhileMaintainingAspect
import com.soywiz.korge.view.solidRect
import com.soywiz.korge.view.text
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.MaterialColors
import com.soywiz.korim.color.RGBA
import com.soywiz.korio.async.launch
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.baseName
import com.xenotactic.gamelogic.korge_utils.existsBlocking
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.korge.events.GoldensEntryClickEvent
import com.xenotactic.korge.events.GoldensEntryHoverOnEvent
import kotlinx.coroutines.Dispatchers

data class MapWithMetadata(
    val file: VfsFile,
    val map: GameMap,
    val verificationResult: MapVerificationResult
)

private data class EntrySettings(
    val outlineRectangleColor: RGBA,
    val isDeleteButtonVisible: Boolean
)

class UIGoldensViewerEntry(
    val eventBus: EventBus,
    val mapData: MapWithMetadata,
    val entryWidth: Double,
    val entryHeight: Double
) : Container() {
    val OUTLINE_RECT_STROKE_THICKNESS = 3
    val TEXT_SECTION_WIDTH = entryWidth - OUTLINE_RECT_STROKE_THICKNESS * 2
    val TEXT_SECTION_HEIGHT = 20
    val titleView: Text

    init {
        val gameMapFile = mapData.file
        val entryBackground = this.solidRect(
            entryWidth, entryHeight, color = MaterialColors
                .GRAY_500
        )

        val entryTextSection = this.solidRect(
            entryWidth, TEXT_SECTION_HEIGHT.toDouble(),
            color = MaterialColors.GRAY_800
        )

        titleView = this.text(
            gameMapFile.baseName, textSize = TEXT_SECTION_HEIGHT.toDouble(),
            color = Colors.WHITE
        ).apply {
            alignLeftToLeftOf(entryTextSection, padding = OUTLINE_RECT_STROKE_THICKNESS)
        }

        val GAME_MAP_SECTION_HEIGHT = entryHeight - TEXT_SECTION_HEIGHT
        val gameMap = mapData.map
        val verificationResult = mapData.verificationResult
        val mapBox = this.uiMapBox(gameMap, entryWidth, GAME_MAP_SECTION_HEIGHT, 1.0)
        mapBox.alignTopToBottomOf(entryTextSection)

        val entrySettings = when (verificationResult) {
            MapVerificationResult.Success -> EntrySettings(Colors.GREEN, false)
            is MapVerificationResult.Failure -> EntrySettings(Colors.RED, true)
            else -> TODO()
        }

        val outlineRect = this.roundRect(
            entryWidth, entryHeight, 0.0, 0.0, Colors.TRANSPARENT_WHITE,
            stroke = entrySettings.outlineRectangleColor,
            strokeThickness = OUTLINE_RECT_STROKE_THICKNESS.toDouble()
        )

        if (gameMapFile.existsBlocking()) {
            val deleteButton = this.uiButton(
                width = 55.0, height = 25.0,
                label = "Delete"
            ) {
                alignRightToRightOf(entryBackground, padding = 10.0)
                alignBottomToBottomOf(entryBackground, padding = 10.0)
                visible = entrySettings.isDeleteButtonVisible
                onPress {
                    println("${gameMapFile.baseName}: delete button clicked!")
                    launch(Dispatchers.Default) {
                        gameMapFile.delete()
                        setTitle(titleView.text + " (Deleted)")
                        this.visible = false
                    }
                }
            }
        } else {
            setTitle(titleView.text + " (Deleted)")
        }

        mapBox.onClick {
            println("${gameMapFile.baseName}: This was clicked!")
            eventBus.send(
                GoldensEntryClickEvent(
                    gameMap
                )
            )
        }

        onOver {
            eventBus.send(GoldensEntryHoverOnEvent(gameMapFile, gameMap, verificationResult))
        }
    }

    fun setTitle(title: String) {
        titleView.text = title
        titleView.scaleWhileMaintainingAspect(
            ScalingOption.ByWidthAndHeight(
                TEXT_SECTION_WIDTH,
                TEXT_SECTION_HEIGHT.toDouble()
            )
        )
    }

    companion object {
        val logger = Logger<UIGoldensViewerEntry>()
    }
}


