package com.xenotactic.gamelogic.utils

import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.bitmap.BitmapSlice
import com.soywiz.korim.bitmap.BmpSlice
import com.soywiz.korim.format.ASE
import com.soywiz.korim.format.ImageDataContainer

data class AsepriteLayer(
    val id: Int,
    val name: String,
    val baseWidth: Int,
    val baseHeight: Int,
    val bitmapSlice: BmpSlice,
    val offsetX: Int,
    val offsetY: Int,
) {
    fun computeUncroppedBitmap(): Bitmap32 {
        val uncroppedBitmap = Bitmap32(baseWidth, baseHeight)
        uncroppedBitmap.draw(bitmapSlice.bmp.toBMP32(), offsetX, offsetY)
        return uncroppedBitmap
    }
}

data class AsepriteFrame(
    val layers: List<AsepriteLayer>
) {
    val baseWidth: Int get() = layers.first().baseWidth
    val baseHeight: Int get() = layers.first().baseHeight
}

data class AsepriteLayerWithAllFrames(
    val baseWidth: Int,
    val baseHeight: Int,
    val frames: List<AsepriteLayer>
) {
    val name get() = frames.first().name
}

data class AsepriteModel(
    val baseWidth: Int,
    val baseHeight: Int,
    val frames: List<AsepriteFrame>
) {
    fun getAsepriteLayerWithAllFrames(name: String): AsepriteLayerWithAllFrames {
        val frames = frames.map { frame -> frame.layers.first { it.name == name } }
        return AsepriteLayerWithAllFrames(baseWidth, baseHeight, frames)
    }
}

fun ImageDataContainer.toAsepriteModel(): AsepriteModel {
    val imageData = this.default
    val baseWidth = imageData.width
    val baseHeight = imageData.height

    val frames = imageData.frames.map {frame ->
        val imageFrameLayers = frame.layerData
        val aseLayers = imageFrameLayers.map { layer ->
            val aseLayer = layer.layer as ASE.AseLayer
            val id = aseLayer.index
            val name = aseLayer.name!!
            val offsetX = layer.targetX
            val offsetY = layer.targetY
            val slice = layer.slice!!
            AsepriteLayer(id, name, baseWidth, baseHeight, slice, offsetX, offsetY)
        }
        AsepriteFrame(aseLayers)
    }
    return AsepriteModel(baseWidth, baseHeight, frames)
}