package com.xenotactic.gamelogic.korge_utils

import com.soywiz.korge.view.View
import com.soywiz.korge.view.size
import com.soywiz.korge.view.xy
import com.xenotactic.gamelogic.utils.WorldUnit


fun View.size(worldWidth: WorldUnit, worldHeight: WorldUnit) = size(worldWidth.value, worldHeight.value)

fun View.xy(worldX: WorldUnit, worldY: WorldUnit) = xy(worldX.value, worldY.value)


