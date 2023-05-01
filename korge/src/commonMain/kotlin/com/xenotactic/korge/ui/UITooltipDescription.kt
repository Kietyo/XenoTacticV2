package com.xenotactic.korge.ui

import korlibs.korge.view.*
import korlibs.image.color.Colors
import korlibs.image.color.MaterialColors
import com.xenotactic.gamelogic.utils.GlobalResources
import korlibs.korge.view.align.alignLeftToLeftOf
import korlibs.korge.view.align.alignLeftToRightOf
import korlibs.korge.view.align.alignTopToTopOf
import korlibs.korge.view.align.centerYOn

class UITooltipDescription(
    towerCost: Int
): Container() {
    init {
        val padding = 5.0
        val bg = solidRect(200, 100, MaterialColors.YELLOW_100)
        val textColor = Colors.BLACK

        val headerTextSize = 24.0
        val descriptionTextSize = 20.0

        val titleText = text(
            "BASIC TOWER",
            textSize = headerTextSize,
            color = textColor,
            font = GlobalResources.FONT_ATKINSON_BOLD
        ) {
            alignTopToTopOf(bg, padding)
            alignLeftToLeftOf(bg, padding)
        }

        val costSection = container {
            val goldCostSection = container {
                val i = image(GlobalResources.GOLD_ICON) {
                    smoothing = false
                }
                text(
                    towerCost.toString(), font = GlobalResources.FONT_ATKINSON_BOLD,
                    textSize = 40.0, color = textColor
                ) {
                    scaleWhileMaintainingAspect(ScalingOption.ByHeight(i.scaledHeight))
                    alignLeftToRightOf(i, padding = 5.0)
                    centerYOn(i)
                }
            }

            val supplyCostSection = container {
                val i = image(GlobalResources.SUPPLY_ICON) {
                    smoothing = false
                }
                val t = text(
                    "1", font = GlobalResources.FONT_ATKINSON_BOLD,
                    textSize = 40.0, color = textColor
                ) {
                    scaleWhileMaintainingAspect(ScalingOption.ByHeight(i.scaledHeight))
                    alignLeftToRightOf(i, padding = 5.0)
                    centerYOn(i)
                }
                alignLeftToRightOf(goldCostSection, padding = 10.0)
            }

            scaleWhileMaintainingAspect(ScalingOption.ByHeight(23.0))
            alignLeftToLeftOf(bg, padding)
            alignTopToBottomOf(titleText)
        }

        val descriptionText = text(
            "A basic tower.",
            textSize = descriptionTextSize,
            font = GlobalResources.FONT_ATKINSON_REGULAR,
            color = textColor
        ).addTo(this) {
            alignTopToBottomOf(costSection, padding)
            alignLeftToLeftOf(bg, padding)
        }
    }
}