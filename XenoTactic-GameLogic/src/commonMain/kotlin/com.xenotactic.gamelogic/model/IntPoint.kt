package com.xenotactic.gamelogic.model

import com.soywiz.korma.geom.Point
import kotlinx.serialization.Serializable

@Serializable
data class IntPoint(val x: Int, val y: Int) {
    companion object {
        val ZERO = IntPoint(0, 0)
    }

    fun toPoint(): Point {
        return Point(x, y)
    }
}