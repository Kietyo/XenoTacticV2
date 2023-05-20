package com.xenotactic.korge.ui

import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.utils.size
import korlibs.image.color.Colors
import korlibs.io.async.Signal
import korlibs.korge.input.MouseEvents
import korlibs.korge.input.onClick
import korlibs.korge.input.onOut
import korlibs.korge.input.onOver
import korlibs.korge.ui.uiButton
import korlibs.korge.ui.uiVerticalStack
import korlibs.korge.view.*
import korlibs.korge.view.align.alignLeftToRightOf
import korlibs.korge.view.align.alignTopToTopOf
import korlibs.korge.view.align.centerOn
import korlibs.logger.Logger
import korlibs.math.geom.RectCorners
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
    val MAP_SECTION_LEFT_RIGHT_PADDING = 5f

    val BUTTON_SECTION_WIDTH = _width * _buttonSectionWidthRatio
    val BUTTON_SECTION_LEFT_RIGHT_PADDING = 3f

    val OUTLINE_RECT_STROKE_THICKNESS =
        min(MAP_SECTION_LEFT_RIGHT_PADDING, BUTTON_SECTION_LEFT_RIGHT_PADDING)

    var mapSection: UIMapBox

    val onPlayButtonClick = Signal<MouseEvents>()
    val onSaveButtonClick = Signal<MouseEvents>()
    val onMapSectionClick = Signal<Unit>()

    init {
        mapSection = this.uiMapBox(gameMap, MAP_SECTION_WIDTH, _height)
        mapSection.onClick {
            onMapSectionClick(Unit)
        }

        val buttonsSection = this.solidRect(BUTTON_SECTION_WIDTH, _height, Colors.ROSYBROWN)
        buttonsSection.alignLeftToRightOf(mapSection)

        val buttonStackWidth = (BUTTON_SECTION_WIDTH - BUTTON_SECTION_LEFT_RIGHT_PADDING * 2).toFloat()
        val buttonPaddingHeight = 5f
        val buttonHeight = (_height - buttonPaddingHeight * (2 + 1)) / 3

        val buttonStack =
            this.uiVerticalStack(buttonStackWidth, padding = buttonPaddingHeight) {
                val playButton = this.uiButton("Play", buttonStackWidth size buttonHeight) {
                    this.onClick {
                        onPlayButtonClick(it)
                    }
                }
                val saveButton = this.uiButton("Save", buttonStackWidth size buttonHeight) {
                    onClick {
                        onSaveButtonClick(it)
                    }
                }
                val deleteButton = this.uiButton("Hide", buttonStackWidth size buttonHeight)
            }

        val outlineRect = this.roundRect(
            _width size _height, RectCorners(0.0, 0.0), Colors.TRANSPARENT_WHITE, Colors.YELLOW,
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