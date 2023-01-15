package com.xenotactic.gamelogic.utils

import com.soywiz.korim.bitmap.BaseBmpSlice
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.bitmap.rotatedRight
import com.soywiz.korim.format.ASE
import com.soywiz.korim.format.ImageDataContainer
import com.soywiz.korim.format.readImageDataContainer
import com.soywiz.korim.format.toProps
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.resources.Resourceable
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object GlobalResources {

    lateinit var MONSTER_SPRITE: ImageDataContainer
    lateinit var GUN_SPRITE: Bitmap32

    suspend fun init() {
        MONSTER_SPRITE = resourcesVfs["8_directional_character.aseprite"].readImageDataContainer(ASE.toProps())
        val towerSprites = resourcesVfs["tower_sprites.aseprite"].readImageDataContainer(ASE.toProps()).toAsepriteModel()
        GUN_SPRITE = towerSprites.getAsepriteLayerWithAllFrames("gun3").frames.first().computeUncroppedBitmap()
    }
}