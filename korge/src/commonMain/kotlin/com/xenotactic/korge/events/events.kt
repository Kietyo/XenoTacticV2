package com.xenotactic.korge.events

import com.soywiz.korge.view.View
import com.xenotactic.gamelogic.model.MapEntity

object GoldensEntryHoverOutEvent

data class UIEntityClickedEvent(
    val view: View,
    val entity: MapEntity
)

