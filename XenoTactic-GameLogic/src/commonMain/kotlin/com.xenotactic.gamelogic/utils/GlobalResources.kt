package com.xenotactic.gamelogic.utils

import korlibs.image.bitmap.Bitmap
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
    lateinit var HIGH_DAMAGE_GUN_SPRITE: Bitmap32
    lateinit var DEPOT_SPRITE: Bitmap32
    lateinit var TOWER_BASE_SPRITE: Bitmap32
    lateinit var TOWER_BASE_DETAIL_SPRITE: Bitmap32

    lateinit var DAMAGE_ICON: Bitmap
    lateinit var COOLDOWN_ICON: Bitmap
    lateinit var UPGRADE_TOWER_ICON: Bitmap
    lateinit var GOLD_ICON: Bitmap32
    lateinit var SUPPLY_ICON: Bitmap32

    lateinit var FONT_ATKINSON_REGULAR: TtfFont
    lateinit var FONT_ATKINSON_BOLD: TtfFont

    suspend fun init() {
        MONSTER_SPRITE = resourcesVfs["8_directional_character.aseprite"].readImageDataContainer(ASE.toProps())
        val towerSprites =
            resourcesVfs["tower_sprites.aseprite"].readImageDataContainer(ASE.toProps().apply {
                onlyReadVisibleLayers = false
            }).toAsepriteModel()

        TOWER_BASE_SPRITE = towerSprites.getAsepriteLayerWithAllFrames("base").frames.first().computeUncroppedBitmap()
        TOWER_BASE_DETAIL_SPRITE =
            towerSprites.getAsepriteLayerWithAllFrames("base_detail").frames.first().computeUncroppedBitmap()
        GUN_SPRITE = towerSprites.getAsepriteLayerWithAllFrames("gun3").frames.first().computeUncroppedBitmap()
        HIGH_DAMAGE_GUN_SPRITE = towerSprites.getAsepriteLayerWithAllFrames("high_damage").frames.first().computeUncroppedBitmap()
        DEPOT_SPRITE = towerSprites.getAsepriteLayerWithAllFrames("depot").frames.first().computeUncroppedBitmap()

        val icons = resourcesVfs["icons.aseprite"].readImageDataContainer(ASE.toProps().apply {
            onlyReadVisibleLayers = false
        }).toAsepriteModel()
        val backgroundLayerName = "background"
        val backgroundUpgradeLayerName = "background_upgrade"
        val goldIconLayerName = "gold_icon"
        val supplyIconLayerName = "supply_icon"
        val damageIconLayerName = "damage_icon"
        val cooldownIconLayerName = "cooldown_icon"
        val upgradeTowerIconLayerName = "upgrade_tower_icon"

//        DAMAGE_ICON = resourcesVfs["damage_icon.aseprite"].readImageDataContainer(ASE.toProps()).toAsepriteModel()
//            .getAsepriteLayerWithAllFrames("icon").frames.first().computeUncroppedBitmap()
//        COOLDOWN_ICON = resourcesVfs["cooldown_icon.aseprite"].readImageDataContainer(ASE.toProps()).toAsepriteModel()
//            .getAsepriteLayerWithAllFrames("icon").frames.first().computeUncroppedBitmap()
//        MONEY_ICON = COOLDOWN_ICON


        DAMAGE_ICON = icons.frames.first().createMergedBitmap(backgroundUpgradeLayerName, damageIconLayerName)
        COOLDOWN_ICON = icons.frames.first().createMergedBitmap(backgroundUpgradeLayerName, cooldownIconLayerName)
        UPGRADE_TOWER_ICON = icons.frames.first().createMergedBitmap(backgroundUpgradeLayerName, upgradeTowerIconLayerName)
        GOLD_ICON = icons.frames.first().createMergedBitmap(backgroundLayerName, goldIconLayerName)
        SUPPLY_ICON = icons.frames.first().createMergedBitmap(backgroundLayerName, supplyIconLayerName)

        FONT_ATKINSON_REGULAR = resourcesVfs["fonts/AtkinsonHyperlegible-Regular.ttf"].readTtfFont()
        FONT_ATKINSON_BOLD = resourcesVfs["fonts/AtkinsonHyperlegible-Bold.ttf"].readTtfFont(enableLigatures = false)
    }
}