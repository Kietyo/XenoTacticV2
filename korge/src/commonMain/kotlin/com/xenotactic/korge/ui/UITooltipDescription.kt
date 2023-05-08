package com.xenotactic.korge.ui

import korlibs.korge.view.*
import korlibs.image.color.Colors
import korlibs.image.color.MaterialColors
import com.xenotactic.gamelogic.utils.GlobalResources
import korlibs.korge.view.align.*

class UITooltipDescription(
    goldCost: Int,
    supplyCost: Int? = 1,
    titleText: String = "BASIC TOWER",
    descriptionText: String = "A basic tower."
): Container() {
    private lateinit var goldCostSectionText: Text

    init {
        val padding = 5.0
        val bg = solidRect(200, 100, MaterialColors.YELLOW_100)
        val textColor = Colors.BLACK

        val headerTextSize = 23f
        val descriptionTextSize = 18f

        val titleTextUI = text(
            titleText,
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
                goldCostSectionText = text(
                    goldCost.toString(), font = GlobalResources.FONT_ATKINSON_BOLD,
                    textSize = 40f, color = textColor
                ) {
                    scaleWhileMaintainingAspect(ScalingOption.ByHeight(i.scaledHeightD))
                    alignLeftToRightOf(i, padding = 5.0)
                    centerYOn(i)
                }
            }

            supplyCost?.let {
                val supplyCostSection = container {
                    val i = image(GlobalResources.SUPPLY_ICON) {
                        smoothing = false
                    }
                    val t = text(
                        supplyCost.toString(), font = GlobalResources.FONT_ATKINSON_BOLD,
                        textSize = 40f, color = textColor
                    ) {
                        scaleWhileMaintainingAspect(ScalingOption.ByHeight(i.scaledHeightD))
                        alignLeftToRightOf(i, padding = 5.0)
                        centerYOn(i)
                    }
                    alignLeftToRightOf(goldCostSection, padding = 10.0)
                }
            }


            scaleWhileMaintainingAspect(ScalingOption.ByHeight(23.0))
            alignLeftToLeftOf(bg, padding)
            alignTopToBottomOf(titleTextUI)
        }

        val descriptionTextUI = text(
            descriptionText,
            textSize = descriptionTextSize,
            font = GlobalResources.FONT_ATKINSON_REGULAR,
            color = textColor
        ).addTo(this) {
            alignTopToBottomOf(costSection, padding)
            alignLeftToLeftOf(bg, padding)
        }
    }

    fun updateMoneyCost(newCost: Int) {
        goldCostSectionText.text = newCost.toString()
    }
}