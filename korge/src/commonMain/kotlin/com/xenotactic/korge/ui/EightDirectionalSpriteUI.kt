package com.xenotactic.korge.ui

import EightDirection
import com.soywiz.korge.view.Anchorable
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.anchor
import com.soywiz.korge.view.animation.imageAnimationView
import com.soywiz.korge.view.centerOnStage
import com.soywiz.korim.format.ImageData
import com.soywiz.korim.format.ImageDataContainer
import com.soywiz.korma.geom.Anchor

class EightDirectionalSpriteUI(
    val imageDataContainer: ImageDataContainer,
): Container(), Anchorable {
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
    val animationView = imageAnimationView(down.defaultAnimation) {
        smoothing = false
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