package com.xenotactic.gamelogic.utils

import korlibs.image.font.readTtfFont
import korlibs.image.format.*
import korlibs.image.bitmap.Bitmap32
import korlibs.image.font.TtfFont
import korlibs.image.format.ImageDataContainer
import korlibs.io.file.std.resourcesVfs
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object GlobalResources {

    lateinit var MONSTER_SPRITE: ImageDataContainer
    lateinit var GUN_SPRITE: Bitmap32
    lateinit var TOWER_BASE_SPRITE: Bitmap32
    lateinit var TOWER_BASE_DETAIL_SPRITE: Bitmap32

    lateinit var DAMAGE_ICON: Bitmap32
    lateinit var COOLDOWN_ICON: Bitmap32
    lateinit var GOLD_ICON: Bitmap32
    lateinit var SUPPLY_ICON: Bitmap32

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
//        MONEY_ICON = COOLDOWN_ICON

        val icons = resourcesVfs["icons.aseprite"].readImageDataContainer(ASE.toProps().apply {
            onlyReadVisibleLayers = false
        }).toAsepriteModel()
        val backgroundLayerName = "background"
        val goldIconLayerName = "gold_icon"
        val supplyIconLayerName = "supply_icon"

        GOLD_ICON = icons.frames.first().createMergedBitmap(backgroundLayerName, goldIconLayerName)
        SUPPLY_ICON = icons.frames.first().createMergedBitmap(backgroundLayerName, supplyIconLayerName)

        FONT_ATKINSON_REGULAR = resourcesVfs["fonts/AtkinsonHyperlegible-Regular.ttf"].readTtfFont()
        FONT_ATKINSON_BOLD = resourcesVfs["fonts/AtkinsonHyperlegible-Bold.ttf"].readTtfFont()
    }
}