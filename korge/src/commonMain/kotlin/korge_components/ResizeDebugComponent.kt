package korge_components

import com.soywiz.korge.component.ResizeComponent
import com.soywiz.korge.view.*
import korge_utils.getReferenceParent

class ResizeDebugComponent(override val view: Container) : ResizeComponent {
    val text: Text

    init {
        text = view.text("", textSize = 12.0)
        text.text = getText()
        text.visible(false)
    }

    private fun getText(width: Int = 0, height: Int = 0): String {
        val parent = view.getReferenceParent()

        return """
            text.localXY(): ${text.localXY()}
            text.globalXY(): ${text.globalXY()}
            width: $width
            height: $height
            view.windowBounds: ${view.windowBounds}
            view.parent.windowBounds: ${view.parent!!.windowBounds}
            view.width: ${view.width}
            view.height: ${view.height}
            view.getBounds(): ${view.getBounds()}
            view.getBoundsNoAnchoring(): ${view.getBoundsNoAnchoring()}
            view.localXY(): ${view.localXY()}
            view.globalXY(): ${view.globalXY()}
            view.globalBounds: ${view.globalBounds}
            view.getWindowBounds(): ${view.getWindowBounds()}
            view.getGlobalBounds(): ${view.getGlobalBounds()}
            view.getLocalBounds(): ${view.getLocalBounds()}
            view.getLocalBoundsOptimized(): ${view.getLocalBoundsOptimized()}
            view.getLocalBoundsOptimizedAnchored(): ${view.getLocalBoundsOptimizedAnchored()}
            view.getVisibleLocalArea(): ${view.getVisibleLocalArea()}
            view.getVisibleGlobalArea(): ${view.getVisibleGlobalArea()}
            view.getVisibleWindowArea(): ${view.getVisibleWindowArea()}
            parent.getVisibleLocalArea(): ${parent.getVisibleLocalArea()}
            parent.getVisibleGlobalArea(): ${parent.getVisibleGlobalArea()}
            parent.getVisibleWindowArea(): ${parent.getVisibleWindowArea()}
        """.trimIndent()

    }

    override fun resized(views: Views, width: Int, height: Int) {
        val txt = getText(width, height)

        //        println(txt)
        //        println()

        text.text = txt
        text.xy(view.getVisibleLocalArea().x.toInt(), view.getVisibleLocalArea().y.toInt())
    }

}