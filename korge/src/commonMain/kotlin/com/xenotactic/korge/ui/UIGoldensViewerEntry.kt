package com.xenotactic.korge.ui

import MapVerificationResult
import korlibs.logger.Logger
import korlibs.korge.input.onClick
import korlibs.korge.input.onOver
import korlibs.korge.ui.uiButton
import korlibs.korge.view.Container
import korlibs.korge.view.ScalingOption
import korlibs.korge.view.Text
import korlibs.korge.view.align.alignBottomToBottomOf
import korlibs.korge.view.align.alignLeftToLeftOf
import korlibs.korge.view.align.alignRightToRightOf
import korlibs.korge.view.align.alignTopToBottomOf
import korlibs.korge.view.roundRect
import korlibs.korge.view.scaleWhileMaintainingAspect
import korlibs.korge.view.solidRect
import korlibs.korge.view.text
import korlibs.image.color.Colors
import korlibs.image.color.MaterialColors
import korlibs.image.color.RGBA
import korlibs.io.async.launch
import korlibs.io.file.VfsFile
import korlibs.io.file.baseName
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


