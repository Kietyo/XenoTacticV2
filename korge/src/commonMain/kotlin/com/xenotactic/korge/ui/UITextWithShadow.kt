package com.xenotactic.korge.ui

import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.xenotactic.gamelogic.utils.GlobalResources

class UITextWithShadow(
    initialText: String
): Container() {
    val t1 = text(initialText, font = GlobalResources.FONT_ATKINSON_BOLD, color = Colors.BLACK) {
        smoothing = false
        x += 2
        y += 2
    }
    val t2 = text(initialText, font = GlobalResources.FONT_ATKINSON_BOLD, color = Colors.YELLOW) {
        smoothing = false
    }

    var text: String
        get() = t1.text
        set(value) {
            t1.text = value
            t2.text = value
        }
}