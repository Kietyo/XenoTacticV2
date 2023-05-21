package com.xenotactic.korge.ui

import com.xenotactic.gamelogic.utils.GlobalResources
import korlibs.image.color.Colors
import korlibs.image.color.MaterialColors
import korlibs.korge.view.*
import korlibs.korge.view.align.*

class UITooltipDescription(
    goldCost: Int? = null,
    supplyCost: Int? = null,
    titleText: String = "BASIC TOWER",
    descriptionText: String = "A basic tower."
) : Container() {
    private var goldCostSectionText: Text? = null

    init {
        val padding = 5f
        val bg = solidRect(20, 20, MaterialColors.YELLOW_100)
        val textColor = Colors.BLACK

        val headerTextSize = 24f
        val descriptionTextSize = 20f

        val content = container {
            val titleTextUI = text(
                titleText,
                textSize = headerTextSize,
                color = textColor,
                font = GlobalResources.FONT_ATKINSON_BOLD
            ) {
                alignTopToTopOf(bg, padding)
                alignLeftToLeftOf(bg, padding)
            }

            var hasCostSection = false
            val costSection = container {
                val viewsToAlign = mutableListOf<View>()
                goldCost?.let {
                    hasCostSection = true
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
                    viewsToAlign.add(goldCostSection)
                }

                supplyCost?.let {
                    hasCostSection = true
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
                    }
                    viewsToAlign.add(supplyCostSection)
                }

                viewsToAlign.windowed(2) {
                    it[1].alignLeftToRightOf(it[0], padding = 10.0)
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
                alignTopToBottomOf(if (hasCostSection) costSection else titleTextUI, padding)
                alignLeftToLeftOf(bg, padding)
            }
        }

        bg.width = content.width + padding * 2
        bg.height = content.height + padding * 2
    }

    fun updateMoneyCost(newCost: Int) {
        goldCostSectionText?.text = newCost.toString()
    }
}