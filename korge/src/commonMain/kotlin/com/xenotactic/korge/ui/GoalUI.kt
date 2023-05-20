package com.xenotactic.korge.ui

import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.events.UpdatedPathLineEvent
import com.xenotactic.gamelogic.utils.size
import com.xenotactic.gamelogic.utils.toScale
import com.xenotactic.korge.events.UpdatedGoalDataEvent
import korlibs.event.EventListener
import korlibs.event.ReshapeEvent
import korlibs.image.color.Colors
import korlibs.image.format.PNG
import korlibs.image.format.readBitmap
import korlibs.image.text.TextAlignment
import korlibs.io.async.runBlockingNoJs
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.ui.UIContainer
import korlibs.korge.ui.uiContainer
import korlibs.korge.view.*
import korlibs.math.geom.RectCorners
import korlibs.math.geom.Size

class GoalUI(val view: Container, val eventBus: EventBus) {
    val goalContainer: UIContainer
    lateinit var _rankTexts: List<Text>
    lateinit var _rankImages: List<Image>
    val discoveredRanks = BooleanArray(3)
    val rankPathLengthGoals = IntArray(3) { Int.MAX_VALUE }

    init {
        val rank1 = runBlockingNoJs {
            resourcesVfs["medals/Medals256/rank_1.png"].readBitmap(PNG)
        }
        val rank2 = runBlockingNoJs {
            resourcesVfs["medals/Medals256/rank_2.png"].readBitmap(PNG)
        }
        val rank3 = runBlockingNoJs {
            resourcesVfs["medals/Medals256/rank_3.png"].readBitmap(PNG)
        }

        val _textPaddingFromGoalImage = 5f
        val _borderPadding = 5.0

        lateinit var rank1Image: Image
        var toggle = false

        goalContainer = view.uiContainer {
            this.roundRect(
                192.0 size
                        64.0 + _textPaddingFromGoalImage + 20.0 + _borderPadding,
                RectCorners(10f, 10f),
                fill = Colors.BLACK.withAd(0.2)
            )
            val textViews = mutableListOf<Text>()
            val rankImages = mutableListOf<Image>()
            val rankBitmaps = arrayListOf(rank1, rank2, rank3)
            val text = arrayListOf("?", "?", "?")
            for (i in 0 until 3) {
                uiContainer {
                    val currImage = this.image(rankBitmaps[i]).apply {
                        scaledSize = Size(64, 64)
                    }
                    val currText = this.text(text[i], alignment = TextAlignment.CENTER)
                    currText.y += 64f + _textPaddingFromGoalImage
                    currText.x += 32f
                    textViews.add(currText)
                    rankImages.add(currImage)
                }.xy(i * 64.0, _borderPadding)
            }
            _rankTexts = textViews
            _rankImages = rankImages
        }

        goalContainer.visible(false)

        //        view.uiButton {
        //            text = "click"
        //            onClick {
        //                println("Clicked~")
        //                if (toggle) {
        //                    rank1Image.colorTransform = ColorTransform(Colors.WHITE)
        //                } else {
        //                    rank1Image.colorTransform = ColorTransform(Colors.BLACK)
        //                }
        //                rank1Image.invalidateColorTransform()
        //                toggle = !toggle
        //            }
        //        }

        val globalArea = view.getVisibleWindowArea()
        reSizeernal(globalArea.widthD, globalArea.heightD)

        eventBus.register<UpdatedGoalDataEvent> {
            handleNewGoalDataEvent(it)
        }
        eventBus.register<UpdatedPathLineEvent> {
            handleNewPathLengthEvent(it)
        }
    }

    private fun handleNewGoalDataEvent(event: UpdatedGoalDataEvent) {
        _rankTexts[0].text = event.data.bronzeGoal.toString()
        _rankTexts[1].text = event.data.silverGoal.toString()
        _rankTexts[2].text = event.data.goldGoal.toString()

        rankPathLengthGoals[0] = event.data.bronzeGoal
        rankPathLengthGoals[1] = event.data.silverGoal
        rankPathLengthGoals[2] = event.data.goldGoal
        goalContainer.visible(true)
    }

    private fun handleNewPathLengthEvent(event: UpdatedPathLineEvent) {
        if (event.newPathLength == null) {
            return
        }
        val newPathLength = event.newPathLength!!
        for ((i, goal) in rankPathLengthGoals.withIndex()) {
            if (newPathLength >= goal) {
                discoveredRanks[i] = true
                _rankImages[i].invalidateColorTransform()
            } else {
                break
            }
        }
    }

    fun setup(eventListener: EventListener) {
        eventListener.onEvent(ReshapeEvent) {
            resized(it.width, it.height)
        }
    }

    fun resized(width: Int, height: Int) {
        reSizeernal(width.toDouble(), height.toDouble())
    }

    fun reSizeernal(width: Double, height: Double) {
        val visibleLocalArea = view.getVisibleLocalArea()
        goalContainer.xy(visibleLocalArea.xD, 50.0)
        goalContainer.scale = 0.75.toScale()
        //        goalContainer.x += bronzeText!!.width / 2
    }

}