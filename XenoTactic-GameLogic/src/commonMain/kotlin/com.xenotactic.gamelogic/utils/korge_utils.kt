package com.xenotactic.gamelogic.utils

import korlibs.korge.view.View
import korlibs.korge.view.size
import korlibs.korge.view.xy
import com.xenotactic.gamelogic.utils.WorldUnit

fun View.size(worldWidth: WorldUnit, worldHeight: WorldUnit) = this.size(worldWidth.toFloat(), worldHeight.toFloat())

fun View.xy(worldX: WorldUnit, worldY: WorldUnit) = xy(worldX.value, worldY.value)


