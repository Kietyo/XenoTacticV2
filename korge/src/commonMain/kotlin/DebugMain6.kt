import com.soywiz.korge.Korge
import com.soywiz.korge.annotations.KorgeExperimental
import com.soywiz.korge.ui.UITooltipContainer
import com.soywiz.korge.ui.tooltip
import com.soywiz.korge.ui.uiTooltipContainer
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.MaterialColors
import com.soywiz.korim.format.ASE
import com.soywiz.korim.format.readImageDataContainer
import com.soywiz.korim.format.toProps
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.file.std.resourcesVfs
import com.xenotactic.gamelogic.utils.GlobalResources
import com.xenotactic.gamelogic.utils.toAsepriteModel
import com.xenotactic.gamelogic.utils.toWorldUnit
import com.xenotactic.korge.korge_utils.createUIEntityContainerForTower
import com.xenotactic.korge.korge_utils.distributeVertically
import com.xenotactic.korge.ui.UITowerDetails
import kotlin.jvm.JvmStatic

object DebugMain6 {

    @OptIn(KorgeExperimental::class)
    @JvmStatic
    fun main(args: Array<String>) = runBlockingNoJs {
        Korge(width = 640, height = 480, bgcolor = Colors.LIGHTGRAY) {
            GlobalResources.init()

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

            val asp = resourcesVfs["gold_icon.aseprite"].readImageDataContainer(ASE.toProps()).toAsepriteModel()
            println(asp)
        }
    }
}