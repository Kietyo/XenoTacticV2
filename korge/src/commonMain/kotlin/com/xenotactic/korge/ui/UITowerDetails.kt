package com.xenotactic.korge.ui

import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.MaterialColors
import com.soywiz.korio.util.toStringDecimal
import com.xenotactic.gamelogic.utils.GlobalResources
import com.xenotactic.gamelogic.utils.toWorldUnit
import com.xenotactic.korge.korge_utils.createUIEntityContainerForTower
import com.xenotactic.korge.korge_utils.distributeVertically

class UITowerDetails(
    damage: Double,
    weaponSpeedMillis: Double,
    range: Double
): Container() {
    init {
        val solidRect = solidRect(500, 250, MaterialColors.BROWN_200)
        val padding = 10.0
        val tower = createUIEntityContainerForTower(
            220.toWorldUnit(), 220.toWorldUnit()
        ).addTo(this) {
            centerYOn(solidRect)
            alignLeftToLeftOf(solidRect, padding = padding)
        }

        val textColor = Colors.BLACK


        val rightSection = container {
            val textContainer = container {
                val textSize = 30.0
                val textPadding = 6.0
                val damageText = container {
                    val t1 = text("Damage:", font = GlobalResources.FONT_ATKINSON_BOLD, textSize = textSize, color = textColor)
                    text(damage.toStringDecimal(2), font = GlobalResources.FONT_ATKINSON_REGULAR, textSize = textSize, color = textColor) {
                        alignLeftToRightOf(t1, textPadding)
                    }
                }
                val speedText = container {
                    val t1 = text("Speed:", font = GlobalResources.FONT_ATKINSON_BOLD, textSize = textSize, color = textColor)
                    val attacksPerSecond = (1E3 / weaponSpeedMillis).toStringDecimal(2)
                    text("$attacksPerSecond ATK/s", font = GlobalResources.FONT_ATKINSON_REGULAR, textSize = textSize, color = textColor) {
                        alignLeftToRightOf(t1, textPadding)
                    }
                }
                val rangeText = container {
                    val t1 = text("Range:", font = GlobalResources.FONT_ATKINSON_BOLD, textSize = textSize, color = textColor)
                    text(range.toStringDecimal(2), font = GlobalResources.FONT_ATKINSON_REGULAR, textSize = textSize, color = textColor) {
                        alignLeftToRightOf(t1, textPadding)
                    }
                }
                distributeVertically(listOf(damageText, speedText, rangeText))
            }


            val iconsContainer = container {
                val iconPadding = 15.0
                val iconSize = 80.0

                val damageIcon = image(GlobalResources.DAMAGE_ICON) {
                    smoothing = false
                    scaleWhileMaintainingAspect(
                        ScalingOption.ByWidth(iconSize)
                    )
                }

                val cooldownIcon = image(GlobalResources.COOLDOWN_ICON) {
                    smoothing = false
                    scaleWhileMaintainingAspect(
                        ScalingOption.ByWidth(iconSize)
                    )
                    alignLeftToRightOf(damageIcon, iconPadding)
                }

                val textSize = 25.0

                val damageText =
                    text(
                        "30",
                        textSize = textSize,
                        color = textColor,
                        font = GlobalResources.FONT_ATKINSON_BOLD
                    ) {
                        smoothing = false
                        centerOn(damageIcon)
                        alignTopToBottomOf(damageIcon, 5.0)
                    }

                val speedText = text(
                    "41/41",
                    textSize = textSize,
                    color = textColor,
                    font = GlobalResources.FONT_ATKINSON_BOLD
                ) {
                    smoothing = false
                    centerOn(cooldownIcon)
                    alignTopToBottomOf(cooldownIcon, 5.0)
                }

                centerXOn(textContainer)
                alignTopToBottomOf(textContainer, padding = 15.0)
            }

            centerYOn(solidRect)
            alignLeftToRightOf(tower, padding = padding)
        }
    }
}