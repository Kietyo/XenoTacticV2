package com.xenotactic.korge.ui

import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.state.MutableCurrentlySelectedTowerState
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.calculateCostOfUpgrades
import korlibs.event.Key
import korlibs.image.bitmap.Bitmap
import korlibs.korge.input.keys
import korlibs.korge.input.onClick
import korlibs.korge.input.onOut
import korlibs.korge.input.onOver
import korlibs.korge.view.*
import korlibs.korge.view.align.alignBottomToTopOf
import korlibs.korge.view.align.centerOn
import korlibs.korge.view.align.centerXOn

class UITowerUpgradeIcon(
    val engine: Engine,
    val tooltip: UITooltipDescription,
    val tooltipSize: Double,
    val guiContainer: UIGuiContainer,
    val icon: Bitmap,
    val initialUpgradeCost: Int,
    val getCurrentUpgradesFn: (currentTowerId: EntityId) -> Int,
    val onUpgradeClick: (numUpgrades: Int) -> Unit
) : Container() {
    private val mutableCurrentlySelectedTowerState =
        engine.stateInjections.getSingleton<MutableCurrentlySelectedTowerState>()
    var numUpgrades = 1

    private val currentTowerId get() = mutableCurrentlySelectedTowerState.currentTowerId

    private val img = image(icon) {
        smoothing = false
        scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(50.0, 50.0))
    }
    private val upgradeNumText: UITextWithShadow = UITextWithShadow("+1").addTo(this) {
        scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(40.0, 40.0))
        centerOn(img)
    }

    init {
        keys {
            justDown(Key.SHIFT) {
                setNumTowerUpgradesText(5)
            }
            up(Key.SHIFT) {
                setNumTowerUpgradesText(1)
            }
        }

        onOver {
            this.tooltip.addTo(guiContainer.stage) {
                scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(tooltipSize, tooltipSize))
                alignBottomToTopOf(this@UITowerUpgradeIcon, padding = 5.0)
                centerXOn(this@UITowerUpgradeIcon)
            }
        }
        onOut {
            this.tooltip.removeFromParent()
        }

        onClick {
            onUpgradeClick(numUpgrades)
        }
    }

    fun setNumTowerUpgradesText(newNumUpgrades: Int) {
        numUpgrades = newNumUpgrades

        val currentUpgrades =
            getCurrentUpgradesFn(currentTowerId!!)

        this.tooltip.updateMoneyCost(
            calculateCostOfUpgrades(
                currentUpgrades,
                initialUpgradeCost,
                newNumUpgrades
            )
        )

        upgradeNumText.text = "+$newNumUpgrades"
        upgradeNumText.centerOn(img)
    }
}