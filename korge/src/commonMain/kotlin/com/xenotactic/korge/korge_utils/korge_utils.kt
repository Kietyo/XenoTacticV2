package com.xenotactic.korge.korge_utils

import com.soywiz.kmem.clamp
import com.soywiz.korev.MouseButton
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.input.MouseEvents
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.View
import com.soywiz.korge.view.Views
import com.soywiz.korge.view.getVisibleGlobalArea
import com.soywiz.korge.view.getVisibleLocalArea
import com.soywiz.korge.view.getVisibleWindowArea

import com.soywiz.korma.geom.Point

import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit
import kotlin.math.floor
import kotlin.math.roundToInt

fun getRoundedGridCoordinates(
    gridX: Double,
    gridY: Double,
    entityWidth: GameUnit,
    entityHeight: GameUnit,
    mapWidth: GameUnit,
    mapHeight: GameUnit
): GameUnitTuple {
    val roundedGridX = when {
        entityWidth.toInt() == 1 -> floor(
            gridX - entityWidth.value / 2
        ).toInt()
        else -> (gridX - entityWidth.value / 2).roundToInt()
    }

    val roundedGridY = when {
        entityHeight.toInt() == 1 -> floor(
            gridY - entityHeight.value / 2
        ).toInt()
        else -> (gridY - entityHeight.value / 2).roundToInt()
    }

    val gridXToInt = roundedGridX.clamp(
        0,
        mapWidth.toInt() - entityWidth.toInt()
    ).toGameUnit()
    val gridYToInt = roundedGridY.clamp(
        0,
        mapHeight.toInt() - entityHeight.toInt()
    ).toGameUnit()
    return GameUnitTuple(gridXToInt, gridYToInt)
}

fun getTopLeft(p1: IPoint, p2: IPoint): IPoint {
    return IPoint(
        minOf(p1.x, p2.x),
        minOf(p1.y, p2.y)
    )
}

fun getBottomRight(p1: IPoint, p2: IPoint): IPoint {
    return IPoint(
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
    println(
        """
        refParent.getVisibleLocalArea(): $localArea
        refParent.getVisibleGlobalArea(): $globalArea
        refParent.getVisibleWindowArea(): $windowsArea
        refParent.height: ${refParent.height}
        refParent.width: ${refParent.width}
        refParent.windowBounds: ${refParent.windowBounds}
        this.scaledHeight: ${this.scaledHeight}
        this.height: ${this.height}
        this.unscaledHeight: ${this.unscaledHeight}
    """.trimIndent()
    )
}

fun <T : View> T.alignBottomToBottomOfWindow(): T {
    val windowsArea = this.getVisibleLocalArea()
//    println(
//        """
//        alignBottomToBottomOfWindow:
//        windowsArea: $windowsArea
//    """.trimIndent()
//    )
//    debugPrint()
    return alignBottomToBottomOfWindow(
        windowsArea.width.toInt() + windowsArea.x.toInt(),
        windowsArea.height.toInt(),
    )
}

fun <T : View> T.alignBottomToBottomOfWindow(
    resizedWidth: Int, resizedHeight: Int,
    yOffset: Int = 0
): T {
    val refParent = getReferenceParent()
    val resizeWHToLocal =
        refParent.globalToLocal(Point(resizedWidth, resizedHeight))
//    println(
//        "alignBottomToBottomOfWindow(resizedWidth=$resizedWidth, " +
//                "resizedHeight=$resizedHeight, yOffset=$yOffset):"
//    )
//    debugPrint()
//    println(
//        """
//        resizeWHToLocal: $resizeWHToLocal
//        refParent.globalToLocal(0.0, yOffset.toDouble()).y: ${
//            refParent.globalToLocal(
//                0.0,
//                yOffset.toDouble()
//            ).y
//        }
//    """.trimIndent()
//    )

    this.y = resizeWHToLocal.y + yOffset - this.scaledHeight
    return this
}

fun <T : View> T.alignRightToRightOfWindow(padding: Double = 0.0): T {
    //    println("""
    //        refParent.getVisibleLocalArea(): ${refParent.getVisibleLocalArea()}
    //        refParent.getVisibleGlobalArea(): ${refParent.getVisibleGlobalArea()}
    //        refParent.getVisibleWindowArea(): ${refParent.getVisibleWindowArea()}
    //    """.trimIndent())
    this.x = getReferenceParent().getVisibleGlobalArea().width - this.width - padding
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

