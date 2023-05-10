package com.xenotactic.gamelogic.utils

import korlibs.image.bitmap.Bitmap32
import korlibs.image.bitmap.BmpSlice
import korlibs.image.bitmap.bmp
import korlibs.image.format.ASE
import korlibs.image.format.ImageDataContainer

data class AsepriteLayer(
    val originalAseIndex: Int,
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

data class AsepriteFrameWithAllLayers(
    val layers: List<AsepriteLayer>
) {
    val baseWidth: Int get() = layers.first().baseWidth
    val baseHeight: Int get() = layers.first().baseHeight

    fun createMergedBitmap(vararg originalAseIndexes: Int): Bitmap32 {
        val layers = originalAseIndexes.map {originalAseIndex ->
            val layersMatching = layers.filter { it.originalAseIndex == originalAseIndex }
            require(layersMatching.size == 1) { "There should be only one layer with that index: $originalAseIndex, but found: $layersMatching" }
            layersMatching.first()
        }
        return createMergedBitmapInternal(layers)
    }

    fun createMergedBitmap(vararg layerNames: String): Bitmap32 {
        val layers = layerNames.map {layerName ->
            val layersMatching = layers.filter { it.name == layerName }
            require(layersMatching.size == 1) { "There should be only one layer with that name: $layerName, but found: $layersMatching" }
            layersMatching.first()
        }
        return createMergedBitmapInternal(layers)
    }

    private fun createMergedBitmapInternal(layers: List<AsepriteLayer>): Bitmap32 {
        val uncroppedBitmap = Bitmap32(baseWidth, baseHeight)
        for (layer in layers) {
            val bmp = layer.bitmapSlice.bmp
            bmp.forEach { n, x, y ->
                val pixel = bmp.getRgba(x, y)
                if (pixel.a != 0) {
                    uncroppedBitmap[x + layer.offsetX, y + layer.offsetY] = pixel
                }
            }
        }
        return uncroppedBitmap
    }
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
    val frames: List<AsepriteFrameWithAllLayers>
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
            val originalAseIndex = aseLayer.originalAseIndex
            val id = aseLayer.index
            val name = aseLayer.name!!
            val offsetX = layer.targetX
            val offsetY = layer.targetY
            val slice = layer.slice
            AsepriteLayer(originalAseIndex, id, name, baseWidth, baseHeight, slice, offsetX, offsetY)
        }
        AsepriteFrameWithAllLayers(aseLayers)
    }
    return AsepriteModel(baseWidth, baseHeight, frames)
}