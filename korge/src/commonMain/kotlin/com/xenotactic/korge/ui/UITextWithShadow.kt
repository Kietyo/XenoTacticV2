package com.xenotactic.korge.ui

import korlibs.korge.view.*
import korlibs.image.color.Colors
import com.xenotactic.gamelogic.utils.GlobalResources

class UITextWithShadow(
    initialText: String
): Container() {
    val fontSize = Text.DEFAULT_TEXT_SIZE
    val t1 = text(initialText, font = GlobalResources.FONT_ATKINSON_BOLD, color = Colors.BLACK,
        textSize = fontSize + 3) {
        smoothing = false
//        x += 1
//        y += 1
    }
    val t2 = text(initialText, font = GlobalResources.FONT_ATKINSON_BOLD, color = Colors.YELLOW) {
        smoothing = false
        centerOn(t1)
    }

    var text: String
        get() = t1.text
        set(value) {
            t1.text = value
            t2.text = value
        }
}