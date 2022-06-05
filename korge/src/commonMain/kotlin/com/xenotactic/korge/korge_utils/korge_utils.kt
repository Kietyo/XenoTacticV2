package com.xenotactic.korge.korge_utils

import com.soywiz.korev.MouseButton
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.component.ResizeComponent
import com.soywiz.korge.input.MouseEvents
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.View
import com.soywiz.korge.view.Views
import com.soywiz.korge.view.getVisibleGlobalArea
import com.soywiz.korge.view.getVisibleLocalArea
import com.soywiz.korge.view.getVisibleWindowArea
import com.soywiz.korma.geom.Point

fun getTopLeft(p1: Point, p2: Point): Point {
    return Point(
        minOf(p1.x, p2.x),
        minOf(p1.y, p2.y)
    )
}

fun getBottomRight(p1: Point, p2: Point): Point {
    return Point(
        maxOf(p1.x, p2.x),
        maxOf(p1.y, p2.y)
    )
}

fun <T : View> T.getReferenceParent(): Container {
    val parentView = this.parent!!
    return parentView.referenceParent
        ?: parentView
}

fun <T : View> T.alignLeftToLeftOfWindow(): T {
    this.x = getReferenceParent().getVisibleLocalArea().x
    return this
}

fun <T : View> T.alignTopToTopOfWindow(): T {
    this.y = getReferenceParent().getVisibleLocalArea().y
    return this
}

fun <T : View> T.debugPrint() {
    val refParent = getReferenceParent()
    val globalArea = refParent.getVisibleGlobalArea()
    val localArea = refParent.getVisibleLocalArea()
    val windowsArea = this.getVisibleWindowArea()
    val windowBounds = refParent.windowBounds
    val localAreaFromGlobal = globalToLocalXY(globalArea.width, globalArea.height)
    val globalAreaFromLocal = localToGlobalXY(localArea.width, localArea.height)
    val windowBoundsToGlobal = localToGlobalXY(windowBounds.height, windowBounds.width)
    val windowBoundsToLocal = globalToLocalXY(windowBounds.height, windowBounds.width)
    println(
        """
        refParent.getVisibleLocalArea(): $localArea
        refParent.getVisibleGlobalArea(): $globalArea
        refParent.getVisibleWindowArea(): $windowsArea
        localAreaFromGlobal: $localAreaFromGlobal
        globalAreaFromLocal: $globalAreaFromLocal
        refParent.height: ${refParent.height}
        refParent.width: ${refParent.width}
        refParent.windowBounds: ${refParent.windowBounds}
        this.scaledHeight: ${this.scaledHeight}
        this.height: ${this.height}
        this.unscaledHeight: ${this.unscaledHeight}
        windowBoundsToGlobal: ${windowBoundsToGlobal}
        windowBoundsToLocal: ${windowBoundsToLocal}
    """.trimIndent()
    )
}

fun <T : View> T.alignBottomToBottomOfWindow(): T {
    val windowsArea = this.getVisibleLocalArea()
    println(
        """
        alignBottomToBottomOfWindow:
        windowsArea: $windowsArea
    """.trimIndent()
    )
    debugPrint()
    return alignBottomToBottomOfWindow(
        windowsArea.width.toInt() + windowsArea.x.toInt(),
        windowsArea.height.toInt(),
        yOffset = windowsArea.y.toInt()
    )
}

fun <T : View> T.alignBottomToBottomOfWindow(
    resizedWidth: Int, resizedHeight: Int,
    yOffset: Int = 0
): T {
    val refParent = getReferenceParent()
    val resizeWHToLocal =
        refParent.globalToLocalXY(resizedWidth.toDouble(), resizedHeight.toDouble())
    println(
        "alignBottomToBottomOfWindow(resizedWidth=$resizedWidth, " +
                "resizedHeight=$resizedHeight, yOffset=$yOffset):"
    )
    debugPrint()
    println(
        """
        resizeWHToLocal: $resizeWHToLocal
        refParent.globalToLocalXY(0.0, yOffset.toDouble()).y: ${
            refParent.globalToLocalXY(
                0.0,
                yOffset.toDouble()
            ).y
        }
    """.trimIndent()
    )

    this.y = resizeWHToLocal.y + yOffset - this.scaledHeight
    return this
}

fun <T : View> T.alignRightToRightOfWindow(): T {
    val refParent = getReferenceParent()
    //    println("""
    //        refParent.getVisibleLocalArea(): ${refParent.getVisibleLocalArea()}
    //        refParent.getVisibleGlobalArea(): ${refParent.getVisibleGlobalArea()}
    //        refParent.getVisibleWindowArea(): ${refParent.getVisibleWindowArea()}
    //    """.trimIndent())
    this.x = getReferenceParent().getVisibleGlobalArea().width - this.width
    return this
}

fun <T : View> T.scaledDimensions() =
    Pair(this.scaledWidth, this.scaledHeight)

fun <T : View> T.unscaledDimensions() =
    Pair(this.unscaledWidth, this.unscaledHeight)

fun <T : Container> T.onStageResizedV2(
    firstTrigger: Boolean = true, block: Views.(
        width: Int,
        height: Int
    ) -> Unit
): T = this.apply {
    if (firstTrigger) {
        deferWithViews { views ->
            val windowsArea = getVisibleWindowArea()
            block(
                views,
                windowsArea.width.toInt(),
                windowsArea.height.toInt()
            )
        }
    }
    addComponent(ResizeComponent(this, block))
}

fun MouseEvents.isScrollDown(): Boolean {
    val event = this.lastEvent
    return event.type == MouseEvent.Type.SCROLL && event.button == MouseButton.BUTTON_WHEEL &&
            event.scrollDeltaYLines > 0
}

fun MouseEvents.isScrollUp(): Boolean {
    val event = this.lastEvent
    return event.type == MouseEvent.Type.SCROLL && event.button == MouseButton.BUTTON_WHEEL &&
            event.scrollDeltaYLines < 0
}
