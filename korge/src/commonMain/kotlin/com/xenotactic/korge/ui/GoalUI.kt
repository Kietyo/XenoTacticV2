package com.xenotactic.korge.ui

import com.soywiz.korge.component.ResizeComponent
import com.soywiz.korge.ui.UIContainer
import com.soywiz.korge.ui.uiContainer
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Image
import com.soywiz.korge.view.Text
import com.soywiz.korge.view.Views
import com.soywiz.korge.view.getVisibleLocalArea
import com.soywiz.korge.view.getVisibleWindowArea
import com.soywiz.korge.view.image
import com.soywiz.korge.view.roundRect
import com.soywiz.korge.view.text
import com.soywiz.korge.view.visible
import com.soywiz.korge.view.xy
import com.soywiz.korim.color.ColorTransform
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.PNG
import com.soywiz.korim.format.readBitmap
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.file.std.resourcesVfs
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.events.UpdatedGoalDataEvent
import com.xenotactic.korge.events.UpdatedPathLengthEvent

class GoalUI(override val view: Container, val eventBus: EventBus) : ResizeComponent {
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
                        colorTransform = ColorTransform(Colors.BLACK)
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
        eventBus.register<UpdatedPathLengthEvent> {
            handleNewPathLengthEvent(it)
        }
    }

    fun handleNewGoalDataEvent(event: UpdatedGoalDataEvent) {
        _rankTexts[0].text = event.data.bronzeGoal.toString()
        _rankTexts[1].text = event.data.silverGoal.toString()
        _rankTexts[2].text = event.data.goldGoal.toString()

        rankPathLengthGoals[0] = event.data.bronzeGoal
        rankPathLengthGoals[1] = event.data.silverGoal
        rankPathLengthGoals[2] = event.data.goldGoal
        goalContainer.visible(true)
    }

    fun handleNewPathLengthEvent(event: UpdatedPathLengthEvent) {
        if (event.newPathLength == null) {
            return
        }
        val newPathLength = event.newPathLength
        for ((i, goal) in rankPathLengthGoals.withIndex()) {
            if (newPathLength >= goal) {
                discoveredRanks[i] = true
                _rankImages[i].colorTransform = ColorTransform(Colors.WHITE)
                _rankImages[i].invalidateColorTransform()
            } else {
                break
            }
        }
    }

    override fun resized(views: Views, width: Int, height: Int) {
        resizeInternal(width.toDouble(), height.toDouble())
    }

    fun resizeInternal(width: Double, height: Double) {
        val visibleLocalArea = view.getVisibleLocalArea()
        goalContainer.xy(visibleLocalArea.x, 50.0)
        goalContainer.scale = 0.75
        //        goalContainer.x += bronzeText!!.width / 2
    }

}