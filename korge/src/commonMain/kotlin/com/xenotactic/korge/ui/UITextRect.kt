package com.xenotactic.korge.ui

import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.font.Font
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.resources.Resourceable

class UITextRect(
    text: String,
    width: Double,
    height: Double,
    padding: Double,
    font: Resourceable<out Font>
) : Container() {
    init {
        val textBg = solidRect(width, height, Colors.DARKGRAY)
        text(text, font = font) {
            smoothing = false
            scaleWhileMaintainingAspect(
                ScalingOption.ByWidthAndHeight(
                    width - padding, height - padding
                )
            )
            centerOn(textBg)
        }
    }
}