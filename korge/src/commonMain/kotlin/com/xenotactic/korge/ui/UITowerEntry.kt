package com.xenotactic.korge.ui

import com.xenotactic.gamelogic.utils.GlobalResources
import com.xenotactic.gamelogic.utils.toWorldUnit
import com.xenotactic.korge.utils.createUIEntityContainerForTower
import korlibs.image.color.Colors
import korlibs.image.color.MaterialColors
import korlibs.korge.view.*
import korlibs.korge.view.align.*

class UITowerEntry : Container() {
    init {
        val solidRect = solidRect(250, 400, MaterialColors.BROWN_300)
        val padding = 15f

        val tower = createUIEntityContainerForTower(
            220.toWorldUnit(), 220.toWorldUnit()
        ).addTo(this)
        tower.centerXOn(solidRect)
        tower.alignTopToTopOf(solidRect, padding)

        val iconWidth = 220.0 / 2.5

        val damageIcon = image(GlobalResources.DAMAGE_ICON) {
            smoothing = false
            scaleWhileMaintainingAspect(
                ScalingOption.ByWidthAndHeight(
                    iconWidth, iconWidth
                )
            )
            alignTopToBottomOf(tower, padding)
            alignLeftToLeftOf(tower, padding)
        }

        val cooldownIcon = image(GlobalResources.COOLDOWN_ICON) {
            smoothing = false
            scaleWhileMaintainingAspect(
                ScalingOption.ByWidthAndHeight(
                    iconWidth, iconWidth
                )
            )
            alignTopToBottomOf(tower, padding)
            alignRightToRightOf(tower, padding)
        }

        val textSize = 30f

        val damageText =
            text("30", textSize = textSize, color = Colors.BLACK, font = GlobalResources.FONT_ATKINSON_BOLD) {
                smoothing = false
                centerOn(damageIcon)
                alignTopToBottomOf(damageIcon, 5.0)
            }

        val speedText =
            text("41\u002F41", textSize = textSize, color = Colors.BLACK, font = GlobalResources.FONT_ATKINSON_BOLD) {
                smoothing = false
                centerOn(cooldownIcon)
                alignTopToBottomOf(cooldownIcon, 5.0)
            }

        val bottom = damageText.globalBounds.bottom

        solidRect.scaledHeight = bottom + padding
    }
}