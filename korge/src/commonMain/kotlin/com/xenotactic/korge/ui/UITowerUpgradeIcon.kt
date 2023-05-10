package com.xenotactic.korge.ui

import com.xenotactic.gamelogic.components.DamageUpgradeComponent
import com.xenotactic.gamelogic.state.GameplayState
import com.xenotactic.gamelogic.state.MutableCurrentlySelectedTowerState
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.GlobalResources
import com.xenotactic.gamelogic.utils.calculateCostOfUpgrades
import com.xenotactic.korge.events.UpgradeTowerDamageEvent
import korlibs.event.Key
import korlibs.korge.component.onAttachDetach
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
    val tooltipUpgradeDamage: UITooltipDescription,
    val tooltipSize: Double,
    val guiContainer: UIGuiContainer
) : Container() {
    private val eventBus = engine.eventBus
    private val world = engine.gameWorld.world
    private val gameplayState = engine.stateInjections.getSingleton<GameplayState>()
    private val mutableCurrentlySelectedTowerState =
        engine.stateInjections.getSingleton<MutableCurrentlySelectedTowerState>()
    var numUpgrades = 1

    private val currentTowerId get() = mutableCurrentlySelectedTowerState.currentTowerId

    private val img = image(GlobalResources.DAMAGE_ICON) {
        smoothing = false
        scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(50.0, 50.0))
    }
    private val towerDamageUpgradeText: UITextWithShadow = UITextWithShadow("+1").addTo(this) {
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

        onAttachDetach(onDetach = {
            setNumTowerUpgradesText(1)
        })

        onOver {
            this.tooltipUpgradeDamage.addTo(guiContainer.stage) {
                scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(tooltipSize, tooltipSize))
                alignBottomToTopOf(this@UITowerUpgradeIcon, padding = 5.0)
                centerXOn(this@UITowerUpgradeIcon)
            }
        }
        onOut {
            this.tooltipUpgradeDamage.removeFromParent()
        }

        onClick {
            eventBus.send(UpgradeTowerDamageEvent(numUpgrades))
        }
    }

    fun setNumTowerUpgradesText(newNumUpgrades: Int) {
        numUpgrades = newNumUpgrades

        val towerDamageUpgradeComponent =
            world[currentTowerId!!, DamageUpgradeComponent::class]
        this.tooltipUpgradeDamage.updateMoneyCost(
            calculateCostOfUpgrades(
                towerDamageUpgradeComponent.numUpgrades,
                gameplayState.initialDamageUpgradeCost, newNumUpgrades
            )
        )

        towerDamageUpgradeText.text = "+$newNumUpgrades"
        towerDamageUpgradeText.centerOn(img)
    }
}