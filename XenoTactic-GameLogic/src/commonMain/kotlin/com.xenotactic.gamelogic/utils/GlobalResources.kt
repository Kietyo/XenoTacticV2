package com.xenotactic.gamelogic.utils

import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.font.TtfFont
import com.soywiz.korim.font.readTtfFont
import com.soywiz.korim.format.ASE
import com.soywiz.korim.format.ImageDataContainer
import com.soywiz.korim.format.readImageDataContainer
import com.soywiz.korim.format.toProps
import com.soywiz.korio.file.std.resourcesVfs
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object GlobalResources {

    lateinit var MONSTER_SPRITE: ImageDataContainer
    lateinit var GUN_SPRITE: Bitmap32
    lateinit var TOWER_BASE_SPRITE: Bitmap32
    lateinit var TOWER_BASE_DETAIL_SPRITE: Bitmap32

    lateinit var DAMAGE_ICON: Bitmap32
    lateinit var COOLDOWN_ICON: Bitmap32
    lateinit var MONEY_ICON: Bitmap32

    lateinit var FONT_ATKINSON_REGULAR: TtfFont
    lateinit var FONT_ATKINSON_BOLD: TtfFont

    suspend fun init() {
        MONSTER_SPRITE = resourcesVfs["8_directional_character.aseprite"].readImageDataContainer(ASE.toProps())
        val towerSprites =
            resourcesVfs["tower_sprites.aseprite"].readImageDataContainer(ASE.toProps()).toAsepriteModel()

        TOWER_BASE_SPRITE = towerSprites.getAsepriteLayerWithAllFrames("base").frames.first().computeUncroppedBitmap()
        TOWER_BASE_DETAIL_SPRITE =
            towerSprites.getAsepriteLayerWithAllFrames("base_detail").frames.first().computeUncroppedBitmap()
        GUN_SPRITE = towerSprites.getAsepriteLayerWithAllFrames("gun3").frames.first().computeUncroppedBitmap()

        DAMAGE_ICON = resourcesVfs["damage_icon.aseprite"].readImageDataContainer(ASE.toProps()).toAsepriteModel()
            .getAsepriteLayerWithAllFrames("icon").frames.first().computeUncroppedBitmap()
        COOLDOWN_ICON = resourcesVfs["cooldown_icon.aseprite"].readImageDataContainer(ASE.toProps()).toAsepriteModel()
            .getAsepriteLayerWithAllFrames("icon").frames.first().computeUncroppedBitmap()
        MONEY_ICON = resourcesVfs["gold_icon.aseprite"].readImageDataContainer(ASE.toProps()).toAsepriteModel()
            .getAsepriteLayerWithAllFrames("icon").frames.first().computeUncroppedBitmap()

        FONT_ATKINSON_REGULAR = resourcesVfs["fonts/AtkinsonHyperlegible-Regular.ttf"].readTtfFont()
        FONT_ATKINSON_BOLD = resourcesVfs["fonts/AtkinsonHyperlegible-Bold.ttf"].readTtfFont()
    }
}