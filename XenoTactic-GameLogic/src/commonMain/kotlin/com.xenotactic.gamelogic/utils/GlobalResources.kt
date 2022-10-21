package com.xenotactic.gamelogic.utils

import com.soywiz.korim.format.ASE
import com.soywiz.korim.format.ImageDataContainer
import com.soywiz.korim.format.readImageDataContainer
import com.soywiz.korim.format.toProps
import com.soywiz.korio.file.std.resourcesVfs
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object GlobalResources {

    lateinit var MONSTER_SPRITE: ImageDataContainer

    suspend fun init() {
        MONSTER_SPRITE = resourcesVfs["8_directional_character.aseprite"].readImageDataContainer(ASE.toProps())
    }
}