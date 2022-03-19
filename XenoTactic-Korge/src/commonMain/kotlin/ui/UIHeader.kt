package ui

import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korio.async.Signal
import korge_utils.scaledDimensions

enum class UIHeaderSection {
    PLAY,
    EDITOR,
    MY_MAPS
}

class UIHeader(
    val userName: String,
    headerWidth: Double
) : Container() {
    val onHeaderSectionClick = Signal<UIHeaderSection>()

    lateinit var selectedSectionBox: SolidRect

    val sectionToTextMap = mutableMapOf<UIHeaderSection, Text>()

    init {
        this.container {
            val HEADER_HEIGHT = 50.0
            val HEADER_TITLE_PADDING_LEFT = 10.0
            val PROFILE_PADDING_TOP_AND_BOTTOM = 10.0
            val PROFILE_PADDING_RIGHT = 5.0

            val headerSection = this.solidRect(
                headerWidth, HEADER_HEIGHT,
                color = Colors.BROWN
            )
            this.text(userName, textSize = 30.0) {
                this.centerYOn(headerSection)
                this.x += HEADER_TITLE_PADDING_LEFT
            }

            val headerTextSize = 20.0
            val textSpacing = 25.0
            val textWidth = 75.0

            val headerOptions = this.container {
                val playText = this.text("Play", textSize = headerTextSize) {
                    onClick {
                        onHeaderSectionClick(UIHeaderSection.PLAY)
                    }
                }
                val editorText = this.text("Editor", textSize = headerTextSize) {
                    onClick {
                        onHeaderSectionClick(UIHeaderSection.EDITOR)
                    }
                }
                editorText.x += textWidth + textSpacing
                val myMapsText = this.text("My Maps", textSize = headerTextSize) {
                    x += textWidth * 2 + textSpacing * 2
                    onClick {
                        onHeaderSectionClick(UIHeaderSection.MY_MAPS)
                    }
                }

                sectionToTextMap[UIHeaderSection.PLAY] = playText
                sectionToTextMap[UIHeaderSection.EDITOR] = editorText
                sectionToTextMap[UIHeaderSection.MY_MAPS] = myMapsText

                selectedSectionBox = this.solidRect(25, 25, Colors.BLACK.withAd(0.5))
                this.sendChildToBack(selectedSectionBox)

                selectedSectionBox.scaledWidth = playText.scaledWidth + 10
                selectedSectionBox.scaledHeight = playText.scaledHeight + 10
                selectedSectionBox.centerOn(playText)

                println("uiHeader:")
                println("""
                    playText.scaledDimensions(): ${playText.scaledDimensions()}
                    editorText.scaledDimensions(): ${editorText.scaledDimensions()}
                    myMapsText.scaledDimensions(): ${myMapsText.scaledDimensions()}
                """.trimIndent())
            }

            headerOptions.centerYOn(headerSection)
            headerOptions.x += 200.0

            val profileSection = this.solidRect(
                100.0, HEADER_HEIGHT - PROFILE_PADDING_TOP_AND_BOTTOM,
                color = Colors.BLACK.withAd(0.25)
            )
            profileSection.alignRightToRightOf(headerSection, padding = PROFILE_PADDING_RIGHT)
            profileSection.centerYOn(headerSection)

            val profileText = this.text(userName, textSize = 20.0)
            profileText.centerOn(profileSection)
        }


    }
}