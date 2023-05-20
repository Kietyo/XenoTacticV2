import com.xenotactic.gamelogic.utils.GlobalResources
import com.xenotactic.korge.ui.UITooltipDescription
import korlibs.image.color.Colors
import korlibs.io.async.runBlockingNoJs
import korlibs.korge.Korge
import korlibs.korge.KorgeConfig
import korlibs.korge.view.addTo
import korlibs.math.geom.Size
import kotlin.jvm.JvmStatic

object DebugMain4 {

    @JvmStatic
    fun main(args: Array<String>) = runBlockingNoJs {
        Korge(
            KorgeConfig(
                backgroundColor = Colors.LIGHTGRAY,
                virtualSize = Size(1280, 720)
            )
        ) {
            GlobalResources.init()
            //            val font = resourcesVfs["fonts/AtkinsonHyperlegible-Regular.ttf"].readTtfFont()
            //            val font = resourcesVfs["fonts/AtkinsonHyperlegible-Bold.ttf"].readTtfFont()

            //            val padding = 5.0
            //            val bg = solidRect(200, 100, MaterialColors.YELLOW_100)
            //            val textColor = Colors.BLACK
            //
            //            val headerTextSize = 25.0
            //            val descriptionTextSize = 20.0
            //
            //            val titleText = text(
            //                "BASIC TOWER",
            //                textSize = headerTextSize,
            //                color = textColor,
            //                font = GlobalResources.FONT_ATKINSON_BOLD
            //            ) {
            //                alignTopToTopOf(bg, padding)
            //                alignLeftToLeftOf(bg, padding)
            //            }
            //
            //            val costSection = container {
            //                val goldCostSection = container {
            //                    val i = image(GlobalResources.GOLD_ICON) {
            //                        smoothing = false
            //                    }
            //                    text(
            //                        "100", font = GlobalResources.FONT_ATKINSON_BOLD,
            //                        textSize = 40.0, color = textColor
            //                    ) {
            //                        scaleWhileMaintainingAspect(ScalingOption.ByHeight(i.scaledHeight))
            //                        alignLeftToRightOf(i, padding = 5.0)
            //                        centerYOn(i)
            //                    }
            //                }
            //
            //                val supplyCostSection = container {
            //                    val i = image(GlobalResources.SUPPLY_ICON) {
            //                        smoothing = false
            //                    }
            //                    val t = text(
            //                        "1", font = GlobalResources.FONT_ATKINSON_BOLD,
            //                        textSize = 40.0, color = textColor
            //                    ) {
            //                        scaleWhileMaintainingAspect(ScalingOption.ByHeight(i.scaledHeight))
            //                        alignLeftToRightOf(i, padding = 5.0)
            //                        centerYOn(i)
            //                    }
            //                    alignLeftToRightOf(goldCostSection, padding = 10.0)
            //                }
            //
            //                scaleWhileMaintainingAspect(ScalingOption.ByHeight(20.0))
            //                alignLeftToLeftOf(bg, padding)
            //                alignTopToBottomOf(titleText)
            //            }
            //
            //            val descriptionText = text(
            //                "A basic tower.",
            //                textSize = descriptionTextSize,
            //                font = GlobalResources.FONT_ATKINSON_REGULAR,
            //                color = textColor
            //            ).addTo(this) {
            //                alignTopToBottomOf(costSection, padding)
            //                alignLeftToLeftOf(bg, padding)
            //            }

            val tooltip = UITooltipDescription(8).addTo(this)

        }
    }
}