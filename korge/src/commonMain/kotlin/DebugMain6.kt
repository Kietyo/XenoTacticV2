import com.soywiz.korge.Korge
import com.soywiz.korge.annotations.KorgeExperimental
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.MaterialColors
import com.soywiz.korio.async.runBlockingNoJs
import com.xenotactic.gamelogic.utils.GlobalResources
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


            val d = UITowerDetails(15.0, 600.0, 7.0, 22, 30, 51).addTo(this)


        }
    }
}