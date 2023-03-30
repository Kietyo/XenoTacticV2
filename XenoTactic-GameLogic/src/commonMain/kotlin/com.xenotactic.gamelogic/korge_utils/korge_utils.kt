package com.xenotactic.gamelogic.korge_utils

import korlibs.korge.view.View
import korlibs.korge.view.size
import korlibs.korge.view.xy
import com.xenotactic.gamelogic.utils.WorldUnit

fun View.size(worldWidth: WorldUnit, worldHeight: WorldUnit) = size(worldWidth.value, worldHeight.value)

fun View.xy(worldX: WorldUnit, worldY: WorldUnit) = xy(worldX.value, worldY.value)


