package com.xenotactic.korge.components

import com.xenotactic.korge.utils.getReferenceParent
import korlibs.event.EventListener
import korlibs.event.ReshapeEvent
import korlibs.korge.view.*

class ResizeDebugComponent(val view: Container) {
    val text: Text = view.text("", textSize = 12f)

    init {
        text.text = getText()
        text.visible(false)
    }

    fun setup(eventListener: EventListener) {
        eventListener.onEvents(ReshapeEvent) {
            resized(it.width, it.height)
        }
    }

    private fun getText(width: Int = 0, height: Int = 0): String {
        val parent = view.getReferenceParent()

        return """
            text.pos: ${text.pos}
            width: $width
            height: $height
            view.windowBounds: ${view.windowBounds}
            view.parent.windowBounds: ${view.parent!!.windowBounds}
            view.width: ${view.width}
            view.height: ${view.height}
            view.getBounds(): ${view.getBounds()}
            view.getBoundsNoAnchoring(): ${view.getBoundsNoAnchoring()}
            view.globalXY(): ${view.globalPos}
            view.globalBounds: ${view.globalBounds}
            view.getGlobalBounds(): ${view.getGlobalBounds()}
            view.getLocalBounds(): ${view.getLocalBounds()}
            view.getLocalBoundsInternal(): ${view.getLocalBoundsInternal()}
            view.getVisibleLocalArea(): ${view.getVisibleLocalArea()}
            view.getVisibleGlobalArea(): ${view.getVisibleGlobalArea()}
            view.getVisibleWindowArea(): ${view.getVisibleWindowArea()}
            parent.getVisibleLocalArea(): ${parent.getVisibleLocalArea()}
            parent.getVisibleGlobalArea(): ${parent.getVisibleGlobalArea()}
            parent.getVisibleWindowArea(): ${parent.getVisibleWindowArea()}
        """.trimIndent()

    }

    fun resized(width: Int, height: Int) {
        val txt = getText(width, height)

        //        println(txt)
        //        println()

        text.text = txt
        text.xy(view.getVisibleLocalArea().x.toInt(), view.getVisibleLocalArea().y.toInt())
    }

}