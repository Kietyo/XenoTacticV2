import com.xenotactic.gamelogic.utils.toAsepriteModel
import com.xenotactic.gamelogic.utils.toScale
import korlibs.image.color.Colors
import korlibs.image.format.ASE
import korlibs.image.format.onlyReadVisibleLayers
import korlibs.image.format.readImageDataContainer
import korlibs.image.format.toProps
import korlibs.io.async.runBlockingNoJs
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.Korge
import korlibs.korge.KorgeConfig
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.view.align.alignLeftToRightOf
import korlibs.korge.view.image
import korlibs.math.geom.Size
import kotlin.jvm.JvmStatic

object DebugMain6 {

    @OptIn(KorgeExperimental::class)
    @JvmStatic
    fun main(args: Array<String>) = runBlockingNoJs {
        Korge(
            KorgeConfig(
                backgroundColor = Colors.LIGHTGRAY,
                virtualSize = Size(640, 480)
            )
        ) {
            //            GlobalResources.init()

            //            val weaponSpeedMillis = 250.0
            //            val attacksPerSecond = 1000.0 / weaponSpeedMillis

            //            val d = UITowerDetails(15.0, weaponSpeedMillis, attacksPerSecond, 7.0, 22, 30, 51).addTo(this)

            // Playing around with tooltips
            //            val d = container { solidRect(50, 50) }
            //
            //            val tooltip = d.uiTooltipContainer()
            //
            //            d.centerOnStage()
            //
            //            d.apply {
            //                tooltip(tooltip, "hello world")
            //            }

            val asp = resourcesVfs["icons.aseprite"].readImageDataContainer(ASE.toProps().apply {
                onlyReadVisibleLayers = false
            })

            val aspModel = asp.toAsepriteModel()
            println(aspModel)

            val backgroundLayerName = "background"
            val goldIconLayerName = "gold_icon"
            val supplyIconLayerName = "supply_icon"

            val bmp = aspModel.frames.first().createMergedBitmap(backgroundLayerName, goldIconLayerName)

            val i1 = image(bmp) {
                smoothing = false
                scale = 3.0.toScale()
            }

            image(aspModel.frames.first().createMergedBitmap(backgroundLayerName, supplyIconLayerName)) {
                smoothing = false
                scale = 3.0.toScale()
                alignLeftToRightOf(i1)
            }
        }
    }
}