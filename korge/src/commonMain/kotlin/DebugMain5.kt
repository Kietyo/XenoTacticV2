import com.xenotactic.gamelogic.utils.GlobalResources
import com.xenotactic.gamelogic.utils.size
import com.xenotactic.korge.ui.UITowerEntry
import korlibs.image.color.Colors
import korlibs.io.async.runBlockingNoJs
import korlibs.korge.Korge
import korlibs.korge.KorgeConfig
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.ui.uiScrollable
import korlibs.korge.view.ScalingOption
import korlibs.korge.view.addTo
import korlibs.korge.view.scaleWhileMaintainingAspect
import korlibs.math.geom.Size
import kotlin.jvm.JvmStatic

object DebugMain5 {

    @OptIn(KorgeExperimental::class)
    @JvmStatic
    fun main(args: Array<String>) = runBlockingNoJs {
        Korge(
            KorgeConfig(
                backgroundColor = Colors.LIGHTGRAY,
                virtualSize = Size(640, 480)
            )
        ) {
            GlobalResources.init()

            //            val solidRect = solidRect(250, 400, MaterialColors.BROWN_300)
            //            val padding = 15.0
            //
            //            val tower = createEntityContainerForTower(
            //                220.toWorldUnit(), 220.toWorldUnit()
            //            ).addTo(this)
            //            tower.centerXOn(solidRect)
            //            tower.alignTopToTopOf(solidRect, padding)
            //
            //            val iconWidth = 220.0 / 2.5
            //
            //            val damageIcon = image(GlobalResources.DAMAGE_ICON) {
            //                smoothing = false
            //                scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(
            //                    iconWidth, iconWidth
            //                ))
            //                alignTopToBottomOf(tower, padding)
            //                alignLeftToLeftOf(tower, padding)
            //            }
            //
            //            val cooldownIcon = image(GlobalResources.COOLDOWN_ICON) {
            //                smoothing = false
            //                scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(
            //                    iconWidth, iconWidth
            //                ))
            //                alignTopToBottomOf(tower, padding)
            //                alignRightToRightOf(tower, padding)
            //            }
            //
            //            val textSize = 30.0
            //
            //            val damageText = text("30", textSize = textSize, color = Colors.BLACK, font = GlobalResources.FONT_ATKINSON_BOLD) {
            //                centerOn(damageIcon)
            //                alignTopToBottomOf(damageIcon, 5.0)
            //            }
            //
            //            val speedText = text("41\u002F41", textSize = textSize, color = Colors.BLACK, font = GlobalResources.FONT_ATKINSON_BOLD) {
            //                centerOn(cooldownIcon)
            //                alignTopToBottomOf(cooldownIcon, 5.0)
            //            }
            //
            //            val bottom = damageText.globalBounds.bottom
            //
            //            solidRect.scaledHeight = bottom + padding
            //
            //            println("bottom: $bottom")

            //            val bg = solidRect(500.0, 250.0, MaterialColors.CYAN_800)

            val padding = 10f

            val bg = uiScrollable(500.0 size 250.0) {
                val entryHeight = (250f - padding * 2 - padding) / 2
                repeat(10) {
                    UITowerEntry().addTo(this) {
                        scaleWhileMaintainingAspect(ScalingOption.ByHeight(entryHeight.toDouble()))
                        //                    alignTopToTopOf(bg, padding = padding)
                        //                    alignLeftToLeftOf(bg, padding = padding)
                        x += 100f * it
                    }
                }

                repeat(10) {
                    UITowerEntry().addTo(this) {
                        scaleWhileMaintainingAspect(ScalingOption.ByHeight(entryHeight.toDouble()))
                        //                    alignTopToTopOf(bg, padding = padding)
                        //                    alignLeftToLeftOf(bg, padding = padding)
                        y = entryHeight
                        x += 75f * it
                    }
                }
            }

            //            val uiTowerEntry = UITowerEntry().addTo(bg) {
            //                scaleWhileMaintainingAspect(ScalingOption.ByHeight((250.0 - padding * 2 - padding) / 2))
            //                alignTopToTopOf(bg, padding = padding)
            //                alignLeftToLeftOf(bg, padding = padding)
            //            }

        }
    }
}