package com.xenotactic.korge.scenes

import com.xenotactic.gamelogic.utils.toScale
import com.xenotactic.korge.ui.Column
import com.xenotactic.korge.ui.Modifier
import com.xenotactic.korge.ui.Modifiers
import com.xenotactic.korge.ui.Row
import korlibs.image.color.Colors
import korlibs.korge.scene.Scene
import korlibs.korge.ui.UIText
import korlibs.korge.view.SContainer
import korlibs.korge.view.align.centerOnStage
import korlibs.logger.Logger
import korlibs.math.geom.Size

class TestScene : Scene() {

    override suspend fun SContainer.sceneInit() {
        //        val textBox = this.container {
        //            val text = this.text("This is a sample test")
        //            this.line(
        //                text.x.toDouble(),
        //                text.y.toDouble() + text.scaledHeight,
        //                text.scaledWidth.toDouble(),
        //                text.y.toDouble() + text.scaledHeight
        //            )
        //        }

        //        val content = container {
        //            solidRect(Size(100, 100), color = Colors.RED)
        //            uiText("UI test")
        //        }
        //
        //        content.scale = Scale(3)
        //        content.x += 50
        //        content.y += 50

        val col = Column().addTo(
            this@sceneInit.stage!!,
            modifiers = Modifiers.of(
                Modifier.SpacingBetween(between = 5f),
                Modifier.SolidBackgroundColor(Colors.RED),
                Modifier.Padding(left = 5f)
            )
        ) {
            val rowSpacing = Modifier.SpacingBetween(between = 0f)
            addLayout {
                Row(modifiers = Modifiers.of(rowSpacing)) {
                    addItem { UIText("User Name", size = Size(100, 18)) }
                    addItem { UIText("Kills", size = Size(64, 18)) }
                    addItem {
                        UIText("Damage").apply {
                            width = 1f
                        }
                    }
                }
            }

            addLayout {
                Row(modifiers = Modifiers.of(rowSpacing)) {
                    addItem { UIText("Xenotactic", size = Size(100, 18)) }
                    addItem { UIText("13", size = Size(64, 18)) }
                    addItem { UIText("123456") }
                }
            }
        }

        col.content.scale = 3.toScale()
        col.content.centerOnStage()

        //        this.sgraphics {
        //            this.stroke(
        //                Colors.WHITE, StrokeInfo(
        //                    thickness = 50.0,
        //                    pixelHinting = true,
        //                    scaleMode = LineScaleMode.NONE,
        //                    lineJoin = LineJoin.ROUND
        //                )
        //            ) {
        //                this.line(
        //                    text.x,
        //                    text.y + text.scaledHeight,
        //                    text.scaledWidth,
        //                    text.y + text.scaledHeight + 1
        //                )
        //            }
        //        }

        //        logger.info {
        //            """
        //                text.scaledDimensions(): ${text.scaledDimensions()}
        //                text.unscaledDimensions(): ${text.unscaledDimensions()}
        //            """.trimIndent()
        //        }
    }

    companion object {

        val logger = Logger<TestScene>()
    }
}
