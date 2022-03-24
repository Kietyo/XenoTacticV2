package ui

import com.soywiz.korge.annotations.KorgeExperimental
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiScrollable
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.xenotactic.gamelogic.model.GameMap
import random.MapGeneratorConfiguration
import random.RandomMapGenerator
import korge_utils.MaterialColors

inline fun Container.uiMapInspector(): UIMapInspector =
    UIMapInspector().addTo(this)

@OptIn(KorgeExperimental::class)
class UIMapInspector : Container() {
    val WIDTH = 250.0
    val HEIGHT = 600.0

    val mapInspectorBackground = this.solidRect(WIDTH, HEIGHT, MaterialColors.GRAY_800)
    lateinit var mapSection: UIMapBox

    init {
        val PADDING_TOP = 5.0
        val PADDING_LEFT_AND_RIGHT = 5.0

        val INNER_ELEMENT_WIDTH = WIDTH - PADDING_LEFT_AND_RIGHT * 2
        val MAP_BOX_HEIGHT = 175.0

        val SECTION_BUTTONS_PADDING = 10.0
        val SECTION_BUTTONS_HEIGHT = 25.0

        container {

            //            val mapSection = this.solidRect(MAP_BOX_WIDTH, MAP_BOX_HEIGHT)
            mapSection = this.uiMapBox(
                RandomMapGenerator.generate(
                    MapGeneratorConfiguration(10, 10)
                ).map, INNER_ELEMENT_WIDTH, MAP_BOX_HEIGHT
            )

            mapSection.centerXOn(mapInspectorBackground)
            mapSection.alignTopToTopOf(mapInspectorBackground, PADDING_TOP)
            //            mapSection.y += PADDING_TOP

            //            val sectionButtons = this.solidRect(INNER_ELEMENT_WIDTH, SECTION_BUTTONS_HEIGHT, MaterialColors.GREEN_100)
            //            sectionButtons.centerXOn(mapInspectorBackground)
            //            sectionButtons.alignTopToBottomOf(mapSection, 10.0)

            val sectionButtonWidth =
                (WIDTH - PADDING_LEFT_AND_RIGHT * 2 - SECTION_BUTTONS_PADDING) / 2

            val sectionButtonsContainer = this.container {
                val sectionButton1 = this.uiButton(
                    sectionButtonWidth, SECTION_BUTTONS_HEIGHT,
                    text = "Details"
                )

                val sectionButton2 = this.uiButton(
                    sectionButtonWidth, SECTION_BUTTONS_HEIGHT,
                    text = "Scores"
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

            val content = this.uiScrollable(width = INNER_ELEMENT_WIDTH, 200.0,
                config = {
                    this.backgroundColor = Colors.TRANSPARENT_WHITE
                }) {
                val restrictionsSection = this.container {
                    text("Restrictions")
                }
                val mapSettingsSection = this.container {
                    text("Map Settings")
                }
                val goalsSection = this.container {
                    text("Goals")
                }

                mapSettingsSection.alignTopToBottomOf(restrictionsSection, PADDING_TOP)
                goalsSection.alignTopToBottomOf(mapSettingsSection, PADDING_TOP)
            }

            content.centerXOn(mapInspectorBackground)
            content.alignTopToBottomOf(sectionButtonsContainer, PADDING_TOP)

        }
    }

    fun setMap(gameMap: GameMap) {
        mapSection.updateMap(gameMap, true)
    }
}