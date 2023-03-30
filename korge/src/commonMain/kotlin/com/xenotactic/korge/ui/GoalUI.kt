package com.xenotactic.korge.ui

import com.soywiz.korev.EventListener
import com.soywiz.korev.ReshapeEvent
import com.soywiz.korge.ui.UIContainer
import com.soywiz.korge.ui.uiContainer
import korlibs.korge.view.Container
import korlibs.korge.view.Image
import korlibs.korge.view.Text
import korlibs.korge.view.Views
import korlibs.korge.view.getVisibleLocalArea
import korlibs.korge.view.getVisibleWindowArea
import korlibs.korge.view.image
import korlibs.korge.view.roundRect
import korlibs.korge.view.text
import korlibs.korge.view.visible
import korlibs.korge.view.xy
import korlibs.image.color.ColorTransform
import korlibs.image.color.Colors
import korlibs.image.format.PNG
import korlibs.image.format.readBitmap
import com.soywiz.korim.text.TextAlignment
import korlibs.io.async.runBlockingNoJs
import korlibs.io.file.std.resourcesVfs
import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.korge.events.UpdatedGoalDataEvent
import com.xenotactic.gamelogic.events.UpdatedPathLineEvent

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

        val _textPaddingFromGoalImage = 5.0
        val _borderPadding = 5.0

        lateinit var rank1Image: Image
        var toggle = false

        goalContainer = view.uiContainer {
            this.roundRect(
                192.0,
                64.0 + _textPaddingFromGoalImage + 20.0 + _borderPadding,
                10.0,
                10.0,
                fill = Colors
                    .BLACK.withAd(0.2)
            )
            val textViews = mutableListOf<Text>()
            val rankImages = mutableListOf<Image>()
            val rankBitmaps = arrayListOf(rank1, rank2, rank3)
            val text = arrayListOf("?", "?", "?")
            for (i in 0 until 3) {
                uiContainer {
                    val currImage = this.image(rankBitmaps[i]).apply {
                        setSizeScaled(64.0, 64.0)
                    }
                    val currText = this.text(text[i], alignment = TextAlignment.CENTER)
                    currText.y += 64.0 + _textPaddingFromGoalImage
                    currText.x += 32.0
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
        resizeInternal(globalArea.width, globalArea.height)

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
        resizeInternal(width.toDouble(), height.toDouble())
    }

    fun resizeInternal(width: Double, height: Double) {
        val visibleLocalArea = view.getVisibleLocalArea()
        goalContainer.xy(visibleLocalArea.x, 50.0)
        goalContainer.scale = 0.75
        //        goalContainer.x += bronzeText!!.width / 2
    }

}