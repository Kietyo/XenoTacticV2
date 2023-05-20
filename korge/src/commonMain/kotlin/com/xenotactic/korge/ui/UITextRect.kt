package com.xenotactic.korge.ui

import korlibs.image.color.Colors
import korlibs.image.font.Font
import korlibs.io.resources.Resourceable
import korlibs.korge.view.*
import korlibs.korge.view.align.centerOn

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