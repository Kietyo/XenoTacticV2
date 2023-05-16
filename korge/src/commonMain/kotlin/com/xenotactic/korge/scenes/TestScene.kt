package com.xenotactic.korge.scenes

import com.xenotactic.korge.ui.Column
import com.xenotactic.korge.ui.Modifier
import com.xenotactic.korge.ui.Modifiers
import com.xenotactic.korge.ui.Row
import korlibs.logger.Logger
import korlibs.korge.scene.Scene
import korlibs.korge.ui.UIText
import korlibs.korge.view.SContainer
import korlibs.korge.view.container
import korlibs.korge.view.line
import korlibs.korge.view.text

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

        val col = Column().addTo(
            this@sceneInit.stage!!,
            modifiers = Modifiers.with(Modifier.Spacing(start = 10f, between = 20f))
        ) {
            addLayout {
                Row(modifiers = Modifiers.with(Modifier.Spacing(between = 10f))) {
                    addItem { UIText("User Name") }
                    addItem { UIText("Kills") }
                    addItem { UIText("Damage") }
                }
            }

            //                addLayout {
            //                    Row(modifiers = Modifiers.with(Modifier.Spacing(start = 10f, between = 20f))) {
            //                        addItem { Text("Xenotactic") }
            //                        addItem { Text("13") }
            //                        addItem { Text("123456") }
            //                    }
            //                }
        }


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
