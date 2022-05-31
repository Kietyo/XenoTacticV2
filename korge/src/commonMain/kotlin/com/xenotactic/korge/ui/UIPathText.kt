package com.xenotactic.korge.ui

import com.soywiz.korge.view.Container
import com.soywiz.korge.view.text
import com.soywiz.korma.math.roundDecimalPlaces

class UIPathText : Container() {
    val pathText = text("Path Length: N/A")

    fun updatePathLength(pathLength: Double?) {
        if (pathLength == null) {
            pathText.text = "Path Length: N/A"
        } else {
            pathText.text = "Path Length: ${pathLength.roundDecimalPlaces(2)}"
        }
    }
}