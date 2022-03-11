package ui

import com.soywiz.klogger.Logger
import com.soywiz.korge.component.ResizeComponent
import com.soywiz.korge.input.MouseEvents
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiVerticalStack
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korio.async.Signal
import events.EventBus
import events.PlayMapEvent
import model.GameMap
import kotlin.math.min

inline fun Container.uiMapEntry(
    gameMap: GameMap,
    _width: Double = 200.0,
    _height: Double = 125.0,
    _mapSectionWidthRatio: Double = 0.75,
    _buttonSectionWidthRatio: Double = 0.25
): UIMapEntry = UIMapEntry(
    gameMap, _width, _height, _mapSectionWidthRatio, _buttonSectionWidthRatio
).addTo(this)

class UIMapEntry(
    val gameMap: GameMap,
    _width: Double = 200.0,
    _height: Double = 125.0,
    _mapSectionWidthRatio: Double = 0.75,
    _buttonSectionWidthRatio: Double = 0.25
) : Container() {
    val MAP_SECTION_WIDTH = _width * _mapSectionWidthRatio
    val MAP_SECTION_LEFT_RIGHT_PADDING = 5.0

    val BUTTON_SECTION_WIDTH = _width * _buttonSectionWidthRatio
    val BUTTON_SECTION_LEFT_RIGHT_PADDING = 3.0

    val OUTLINE_RECT_STROKE_THICKNESS =
        min(MAP_SECTION_LEFT_RIGHT_PADDING, BUTTON_SECTION_LEFT_RIGHT_PADDING)

    var mapSection: UIMapBox

    val onPlayButtonClick = Signal<MouseEvents>()
    val onSaveButtonClick = Signal<MouseEvents>()

    init {
        mapSection = this.uiMapBox(gameMap, MAP_SECTION_WIDTH, _height)

        val buttonsSection = this.solidRect(BUTTON_SECTION_WIDTH, _height, Colors.ROSYBROWN)
        buttonsSection.alignLeftToRightOf(mapSection)

        val buttonStackWidth = BUTTON_SECTION_WIDTH - BUTTON_SECTION_LEFT_RIGHT_PADDING * 2
        val buttonPaddingHeight = 5.0
        val buttonHeight = (_height - buttonPaddingHeight * (2 + 1)) / 3

        val buttonStack =
            this.uiVerticalStack(buttonStackWidth, padding = buttonPaddingHeight) {
                val playButton = this.uiButton(buttonStackWidth, buttonHeight, "Play") {
                    this.onClick {
                        onPlayButtonClick(it)
                    }
                }
                val saveButton = this.uiButton(buttonStackWidth, buttonHeight, "Save") {
                    onClick {
                        onSaveButtonClick(it)
                    }
                }
                val deleteButton = this.uiButton(buttonStackWidth, buttonHeight, "Delete")
            }

        val outlineRect = this.roundRect(
            _width, _height, 0.0, 0.0, Colors.TRANSPARENT_WHITE, Colors.YELLOW,
            OUTLINE_RECT_STROKE_THICKNESS
        ).apply {
            alpha(0.0)
        }

        buttonStack.alignTopToTopOf(buttonsSection)
        buttonStack.centerOn(buttonsSection)

        this.onOver {
            outlineRect.alpha(1.0)
        }

        this.onOut {
            outlineRect.alpha(0.0)
        }
    }

    companion object {
        val log = Logger<UIMapEntry>()
    }
}