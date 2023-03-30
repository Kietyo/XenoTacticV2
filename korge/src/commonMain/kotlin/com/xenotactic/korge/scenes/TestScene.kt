package com.xenotactic.korge.scenes

import korlibs.logger.Logger
import korlibs.korge.scene.Scene
import korlibs.korge.view.SContainer
import korlibs.korge.view.container
import korlibs.korge.view.line
import korlibs.korge.view.text

class TestScene : Scene() {

    override suspend fun SContainer.sceneInit() {
        val textBox = this.container {
            val text = this.text("This is a sample test")
            this.line(
                text.x,
                text.y + text.scaledHeight,
                text.scaledWidth,
                text.y + text.scaledHeight
            )
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
