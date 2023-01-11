package com.xenotactic.gamelogic.views

import com.soywiz.korge.view.*
import com.soywiz.korim.format.ImageData
import com.soywiz.korim.format.ImageDataContainer

enum class EightDirection {
    UP_LEFT,
    UP,
    UP_RIGHT,
    LEFT,
    RIGHT,
    DOWN_LEFT,
    DOWN,
    DOWN_RIGHT
}

class UIEightDirectionalSprite(
    val imageDataContainer: ImageDataContainer,
): RectBase() {
//    override var anchorX: Double = 0.0
//        set(value) {
//            field = value
//            invalidateRender()
//        }
//    override var anchorY: Double = 0.0
//        set(value) {
//            field = value
//            invalidateRender()
//        }
    val upLeft = imageDataContainer.imageDatasByName["up_left"]!!
    val up = imageDataContainer.imageDatasByName["up"]!!
    val upRight = imageDataContainer.imageDatasByName["up_right"]!!

    val left = imageDataContainer.imageDatasByName["left"]!!
    val right = imageDataContainer.imageDatasByName["right"]!!

    val downLeft = imageDataContainer.imageDatasByName["down_left"]!!
    val down = imageDataContainer.imageDatasByName["down"]!!
    val downRight = imageDataContainer.imageDatasByName["down_right"]!!

    private val allSprites = listOf(
        upLeft, up, upRight, left, right, downLeft, down, downRight
    )

    var currentDirection = EightDirection.DOWN
//    val animationView = imageAnimationView(down.defaultAnimation) {
//        smoothing = false
//    }

    init {
        baseBitmap = down.defaultAnimation.frames.first().slice
    }

    override var width: Double = baseBitmap.width.toDouble(); set(v) {
        if (field != v) {
            field = v
            dirtyVertices = true
            invalidateRender()
        }
    }
    override var height: Double = baseBitmap.height.toDouble(); set(v) {
        if (field != v) {
            field = v
            dirtyVertices = true
            invalidateRender()
        }
    }

    override val bwidth: Double get() = width
    override val bheight: Double get() = height

    private val frameCount = down.frames.size
    var currentFrame = 0

    init {
//        baseBitmap = down.defaultAnimation.frames.first().bitmap.slice()
        println("""
            EightDirectionalSpriteUI
            baseBitmap: $baseBitmap
            baseBitmap.width: ${baseBitmap.width}
            baseBitmap.height: ${baseBitmap.height}
            down: $down
        """.trimIndent())
        require(allSprites.all {
            it.frames.size == frameCount
        }) {
            "Expected all sprites to have the same number of walking frames."
        }
        smoothing = false
//        dirtyVertices = true
//        invalidateRender()
    }

    fun changeToDirection(direction: EightDirection) {
        val imageData = getImageDataForDirection(direction)
        baseBitmap = imageData.defaultAnimation.frames[currentFrame].slice
        currentDirection = direction
    }

    fun incrementFrame() {
        currentFrame = (currentFrame + 1) % frameCount
        changeToDirection(currentDirection)
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