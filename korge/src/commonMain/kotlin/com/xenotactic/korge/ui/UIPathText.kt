package com.xenotactic.korge.ui

import com.xenotactic.gamelogic.utils.GameUnit
import korlibs.korge.view.Container
import korlibs.korge.view.text
import korlibs.math.roundDecimalPlaces

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