package com.xenotactic.korge.ui

import com.soywiz.korge.annotations.KorgeExperimental
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiScrollable
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Text
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.alignBottomToBottomOf
import com.soywiz.korge.view.alignLeftToLeftOf
import com.soywiz.korge.view.alignLeftToRightOf
import com.soywiz.korge.view.alignTopToBottomOf
import com.soywiz.korge.view.alignTopToTopOf
import com.soywiz.korge.view.centerOn
import com.soywiz.korge.view.centerXOn
import com.soywiz.korge.view.container
import com.soywiz.korge.view.solidRect
import com.soywiz.korge.view.text
import com.soywiz.korge.view.visible
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.MaterialColors
import com.xenotactic.gamelogic.mapid.MapToId
import com.xenotactic.gamelogic.model.GameMap

inline fun Container.uiMapInspector(): UIMapInspector =
    UIMapInspector().addTo(this)

@OptIn(KorgeExperimental::class)
class UIMapInspector(
    val inspectorHeight: Double = 430.0
) : Container() {
    val WIDTH = 250.0

    val PADDING_TOP = 5.0
    val PADDING_BOTTOM = 5.0
    val PADDING_LEFT_AND_RIGHT = 5.0

    val INNER_ELEMENT_WIDTH = WIDTH - PADDING_LEFT_AND_RIGHT * 2
    val MAP_BOX_HEIGHT = 175.0


    val mapInspectorBackground = this.solidRect(WIDTH, inspectorHeight, MaterialColors.GRAY_800)
    val mapBoxArea = this.solidRect(INNER_ELEMENT_WIDTH, MAP_BOX_HEIGHT, MaterialColors.GRAY_500)
        .apply {
            centerXOn(mapInspectorBackground)
            alignTopToTopOf(mapInspectorBackground, PADDING_TOP)
        }
    val noMapSelectedText = this.text("No map selected") {
        centerOn(mapBoxArea)
    }

    lateinit var mapSection: UIMapBox

    val content = this.uiScrollable(width = INNER_ELEMENT_WIDTH, 200.0,
        config = {
            this.backgroundColor = Colors.TRANSPARENT_WHITE
        })

    lateinit var mapIdText: Text

    init {

        val SECTION_BUTTONS_PADDING = 10.0
        val SECTION_BUTTONS_HEIGHT = 25.0


        val gameMap = GameMap(10, 10)

        //            val mapSection = this.solidRect(MAP_BOX_WIDTH, MAP_BOX_HEIGHT)
        mapSection = this.uiMapBox(
            gameMap, INNER_ELEMENT_WIDTH, MAP_BOX_HEIGHT
        ).apply {
            centerXOn(mapInspectorBackground)
            alignTopToTopOf(mapInspectorBackground, PADDING_TOP)
            visible(false)
        }

        //            mapSection.y += PADDING_TOP

        //            val sectionButtons = this.solidRect(INNER_ELEMENT_WIDTH, SECTION_BUTTONS_HEIGHT, MaterialColors.GREEN_100)
        //            sectionButtons.centerXOn(mapInspectorBackground)
        //            sectionButtons.alignTopToBottomOf(mapSection, 10.0)


        val sectionButtonWidth =
            (WIDTH - PADDING_LEFT_AND_RIGHT * 2 - SECTION_BUTTONS_PADDING) / 2

        val sectionButtonsContainer = this.container {
            val sectionButton1 = this.uiButton(
                "Details",
                sectionButtonWidth, SECTION_BUTTONS_HEIGHT,
            )

            val sectionButton2 = this.uiButton(
                "Scores",
                sectionButtonWidth, SECTION_BUTTONS_HEIGHT,
            )

            //                val sectionButton1 = this.solidRect(
            //                    sectionButtonWidth, SECTION_BUTTONS_HEIGHT, MaterialColors
            //                        .GREEN_400
            //                )
            //                val sectionButton2 = this.solidRect(
            //                    sectionButtonWidth, SECTION_BUTTONS_HEIGHT, MaterialColors
            //                        .GREEN_400
            //                )
            sectionButton2.alignLeftToRightOf(sectionButton1, SECTION_BUTTONS_PADDING)
        }

        sectionButtonsContainer.centerXOn(mapInspectorBackground)
        sectionButtonsContainer.alignTopToBottomOf(mapSection, PADDING_TOP)

        content.centerXOn(mapInspectorBackground)
        content.alignTopToBottomOf(sectionButtonsContainer, PADDING_TOP)

//        val text("Map ID: ${MapToId.calculateId(gameMap)}", textSize = 10.0)
        mapIdText = text("Map ID: asdfasdfasdfasdfsdf", textSize = 10.0) {
            alignLeftToLeftOf(mapInspectorBackground, PADDING_LEFT_AND_RIGHT)
            alignBottomToBottomOf(mapInspectorBackground, PADDING_BOTTOM)
            visible(false)
        }

    }

    fun setMap(gameMap: GameMap) {
        mapSection.apply {
            updateMap(gameMap, true)
            visible(true)
        }

        noMapSelectedText.visible = false

        content.removeChildren()
        content.apply {
            val mapSettingsSection = this.container {
                val texts = mutableListOf(
                    text("Map Settings"),
                    text("- Width: ${gameMap.width}"),
                    text("- Height: ${gameMap.height}"),
                    text("- # Checkpoints: ${gameMap.numCheckpoints}"),
                    text("- # Teleports: ${gameMap.numTeleports}"),
                    text("- # Rocks: ${gameMap.numRocks}"),
                    text("- # Speed Areas: ${gameMap.numSpeedAreas}"),
                )
                texts.windowed(2) {
                    it[1].alignTopToBottomOf(it[0])
                }
            }
        }

        mapIdText.text = "Map ID: ${MapToId.calculateId(gameMap)}"
        mapIdText.visible = true
    }
}