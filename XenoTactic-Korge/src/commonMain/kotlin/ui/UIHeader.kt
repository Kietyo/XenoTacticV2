package ui

import com.soywiz.korge.input.MouseEvents
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korio.async.Signal

class UIHeader(
    val userName: String,
    headerWidth: Double
) : Container() {
    val onMyMapsClick = Signal<MouseEvents>()
    init {
        this.container {
            val HEADER_HEIGHT = 50.0
            val HEADER_TITLE_PADDING_LEFT = 10.0
            val PROFILE_PADDING_TOP_AND_BOTTOM = 10.0
            val PROFILE_PADDING_RIGHT = 5.0

            val headerSection = this.solidRect(
                headerWidth, HEADER_HEIGHT, color = Colors
                    .BROWN
            )
            this.text("XenoTactic", textSize = 30.0) {
                this.centerYOn(headerSection)
                this.x += HEADER_TITLE_PADDING_LEFT
            }

            val headerTextSize = 20.0
            val textSpacing = 25.0
            val textWidth = 75.0

            val headerOptions = this.container {
                val playText = this.text("Play", textSize = headerTextSize)
                val editorText = this.text("Editor", textSize = headerTextSize)
                editorText.x += textWidth + textSpacing
                val myMapsText = this.text("My Maps", textSize = headerTextSize) {
                    x += textWidth * 2 + textSpacing * 2
                    onClick {
                        onMyMapsClick(it)
                    }
                }
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