package com.xenotactic.korge.ui

import com.soywiz.korge.view.Container
import com.soywiz.korge.view.text
import com.soywiz.korma.math.roundDecimalPlaces
import com.xenotactic.gamelogic.utils.GameUnit

class UIPathText : Container() {
    val pathText = text("Path Length: N/A")

    fun updatePathLength(pathLength: GameUnit?) {
        if (pathLength == null) {
            pathText.text = "Path Length: N/A"
        } else {
            pathText.text = "Path Length: ${pathLength.toDouble().roundDecimalPlaces(2)}"
        }
    }
}