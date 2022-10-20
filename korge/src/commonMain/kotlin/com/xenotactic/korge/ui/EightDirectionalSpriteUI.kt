package com.xenotactic.korge.ui

import EightDirection
import com.soywiz.korge.view.*
import com.soywiz.korge.view.animation.imageAnimationView
import com.soywiz.korim.bitmap.slice
import com.soywiz.korim.format.ImageAnimation
import com.soywiz.korim.format.ImageData
import com.soywiz.korim.format.ImageDataContainer
import com.soywiz.korma.geom.Anchor

class EightDirectionalSpriteUI(
    val imageDataContainer: ImageDataContainer,
): RectBase() {
    override var anchorX: Double = 0.0
        set(value) {
            field = value
            invalidateRender()
        }
    override var anchorY: Double = 0.0
        set(value) {
            field = value
            invalidateRender()
        }
    val upLeft = imageDataContainer.imageDatasByName["up_left"]!!
    val up = imageDataContainer.imageDatasByName["up"]!!
    val upRight = imageDataContainer.imageDatasByName["up_right"]!!

    val left = imageDataContainer.imageDatasByName["left"]!!
    val right = imageDataContainer.imageDatasByName["right"]!!

    val downLeft = imageDataContainer.imageDatasByName["down_left"]!!
    val down = imageDataContainer.imageDatasByName["down"]!!
    val downRight = imageDataContainer.imageDatasByName["down_right"]!!

    var currentDirection = EightDirection.DOWN
//    val animationView = imageAnimationView(down.defaultAnimation) {
//        smoothing = false
//    }

    init {
        baseBitmap = down.defaultAnimation.frames.first().slice
    }

    override val bwidth: Double = baseBitmap.width.toDouble()
    override val bheight: Double = baseBitmap.height.toDouble()

    init {
//        baseBitmap = down.defaultAnimation.frames.first().bitmap.slice()
        println("""
            EightDirectionalSpriteUI
            baseBitmap: $baseBitmap
            baseBitmap.width: ${baseBitmap.width}
            baseBitmap.height: ${baseBitmap.height}
        """.trimIndent())
        smoothing = false
//        dirtyVertices = true
//        invalidateRender()
    }

    fun changeToDirection(direction: EightDirection) {
        val imageData = getImageDataForDirection(direction)
        baseBitmap = imageData.defaultAnimation.frames.first().slice
    }

    fun getImageDataForDirection(direction: EightDirection): ImageData {
        return when (direction) {
            EightDirection.UP_LEFT -> upLeft
            EightDirection.UP -> up
            EightDirection.UP_RIGHT -> upRight
            EightDirection.LEFT -> left
            EightDirection.RIGHT -> right
            EightDirection.DOWN_LEFT -> downLeft
            EightDirection.DOWN -> down
            EightDirection.DOWN_RIGHT -> downRight
        }
    }
}