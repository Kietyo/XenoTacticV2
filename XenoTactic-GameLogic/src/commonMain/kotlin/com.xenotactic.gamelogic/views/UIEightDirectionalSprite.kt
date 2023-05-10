package com.xenotactic.gamelogic.views

import korlibs.korge.view.RectBase
import korlibs.image.format.ImageData
import korlibs.image.format.ImageDataContainer

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
    imageDataContainer: ImageDataContainer,
): RectBase() {
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

//    override var width: Float = baseBitmap.width; set(v) {
//        if (field != v) {
//            field = v
//            dirtyVertices = true
//            invalidateRender()
//        }
//    }
//    var height: Float = baseBitmap.height; set(v) {
//        if (field != v) {
//            field = v
//            dirtyVertices = true
//            invalidateRender()
//        }
//    }

    override val bwidth: Float get() = baseBitmap.width.toFloat()
    override val bheight: Float get() = baseBitmap.height.toFloat()

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