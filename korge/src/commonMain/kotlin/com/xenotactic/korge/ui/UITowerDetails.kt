package com.xenotactic.korge.ui

import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.GlobalResources
import com.xenotactic.gamelogic.utils.toWorldUnit
import com.xenotactic.korge.events.UpgradedTowerDamageEvent
import com.xenotactic.korge.events.UpgradedTowerSpeedEvent
import com.xenotactic.korge.utils.createUIEntityContainerForTower
import com.xenotactic.korge.utils.distributeVertically
import korlibs.image.color.Colors
import korlibs.image.color.MaterialColors
import korlibs.io.util.toStringDecimal
import korlibs.korge.view.*
import korlibs.korge.view.align.*

class UITowerDetails(
    damage: Double,
    weaponSpeedMillis: Double,
    attacksPerSecond: Double,
    range: Double,
    damageUpgrades: Int,
    speedUpgrades: Int,
    maxSpeedUpgrades: Int,
    engine: Engine? = null,
) : Container() {
    init {
        val eventBus = engine?.eventBus
        val solidRect = solidRect(550, 300, MaterialColors.BROWN_200)
        val padding = 10.0
        val towerImage = createUIEntityContainerForTower(
            220.toWorldUnit(), 220.toWorldUnit()
        ).addTo(this) {
            centerYOn(solidRect)
            alignLeftToLeftOf(solidRect, padding = padding)
        }

        val textColor = Colors.BLACK
        val paddingBetweenImageAndText = 15.0
        val rightSection = container {
            val textContainer = container {
                val textSize = 30f
                val textPadding = 6.0
                val damageTextSection = container {
                    val t1 = text(
                        "Damage:",
                        font = GlobalResources.FONT_ATKINSON_BOLD,
                        textSize = textSize,
                        color = textColor
                    )
                    val t2 = text(
                        damage.toStringDecimal(2),
                        font = GlobalResources.FONT_ATKINSON_REGULAR,
                        textSize = textSize,
                        color = textColor
                    ) {
                        alignLeftToRightOf(t1, textPadding)
                    }
                    eventBus?.register<UpgradedTowerDamageEvent> {
                        t2.text = it.newDamage.toStringDecimal(2)
                    }
                }
                val speedTextSection = container {
                    val t1 = text(
                        "Speed:",
                        font = GlobalResources.FONT_ATKINSON_BOLD,
                        textSize = textSize,
                        color = textColor
                    )
                    val t2 = text(
                        "${attacksPerSecond.toStringDecimal(2)} ATK/s",
                        font = GlobalResources.FONT_ATKINSON_REGULAR,
                        textSize = textSize,
                        color = textColor
                    ) {
                        alignLeftToRightOf(t1, textPadding)
                    }
                    eventBus?.register<UpgradedTowerSpeedEvent> {
                        t2.text = "${it.newAttacksPerSecond.toStringDecimal(2)} ATK/s"
                    }
                }
                val weaponSpeedTextSection = container {
                    val t1 = text(
                        "Weapon Speed:",
                        font = GlobalResources.FONT_ATKINSON_BOLD,
                        textSize = textSize,
                        color = textColor
                    )
                    val t2 = text(
                        (weaponSpeedMillis / 1E3).toStringDecimal(2),
                        font = GlobalResources.FONT_ATKINSON_REGULAR,
                        textSize = textSize,
                        color = textColor
                    ) {
                        alignLeftToRightOf(t1, textPadding)
                    }
                    eventBus?.register<UpgradedTowerSpeedEvent> {
                        t2.text = (it.newWeaponSpeedMillis / 1E3).toStringDecimal(2)
                    }
                }
                val rangeTextSection = container {
                    val t1 = text(
                        "Range:",
                        font = GlobalResources.FONT_ATKINSON_BOLD,
                        textSize = textSize,
                        color = textColor
                    )
                    text(
                        range.toStringDecimal(2),
                        font = GlobalResources.FONT_ATKINSON_REGULAR,
                        textSize = textSize,
                        color = textColor
                    ) {
                        alignLeftToRightOf(t1, textPadding)
                    }
                }
                distributeVertically(
                    listOf(
                        damageTextSection,
                        speedTextSection,
                        weaponSpeedTextSection,
                        rangeTextSection
                    )
                )
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

                val textSize = 25f

                val damageUpgradesText =
                    text(
                        damageUpgrades.toString(),
                        textSize = textSize,
                        color = textColor,
                        font = GlobalResources.FONT_ATKINSON_BOLD
                    ) {
                        smoothing = false
                        centerOn(damageIcon)
                        alignTopToBottomOf(damageIcon, 5.0)
                    }

                eventBus?.register<UpgradedTowerDamageEvent> {
                    damageUpgradesText.text = it.newDamageUpgrade.toString()
                }

                val speedUpgradesText = text(
                    "$speedUpgrades/$maxSpeedUpgrades",
                    textSize = textSize,
                    color = textColor,
                    font = GlobalResources.FONT_ATKINSON_BOLD
                ) {
                    smoothing = false
                    centerOn(cooldownIcon)
                    alignTopToBottomOf(cooldownIcon, 5.0)
                }

                eventBus?.register<UpgradedTowerSpeedEvent> {
                    speedUpgradesText.text = "${it.newSpeedUpgrade}/$maxSpeedUpgrades"
                }

                centerXOn(textContainer)
                alignTopToBottomOf(textContainer, padding = 15.0)
            }

            centerYOn(solidRect)
            alignLeftToRightOf(towerImage, padding = paddingBetweenImageAndText)
        }
    }
}